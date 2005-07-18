var baseUrl = "/oxyd/command/";
var debug = 1;
var editing = false;

document.onclick = startEdit;

if (document.getElementById && document.createElement)
{
	var butt = document.createElement('BUTTON');
	var buttext = document.createTextNode('Save');
	butt.appendChild(buttext);
	butt.onclick = saveEdit;
}

function saveEdit()
{
    var area = document.getElementsByTagName('TEXTAREA')[0];
    blockEl = area.parentNode;
    var contentEl = document.createElement('div');
    var blockEl = area.parentNode;
    contentEl.setAttribute('id', 'content_'+blockEl.id);
    contentEl.setAttribute('class', 'content');
    blockEl.appendChild(contentEl);
    contentEl.innerHTML = area.value;
	blockEl.insertBefore(contentEl,area);
	blockEl.removeChild(area);
	blockEl.removeChild(document.getElementsByTagName('button')[0]);
	editing = false;
	return false;
}

function startEdit(e)
{
	if (editing) return;
	if (!document.getElementById || !document.createElement) return;
	var obj = null;
    if (!e) obj = window.event.srcElement;
	else obj = e.target;
	while (obj.nodeType != 1)
	{
		obj = obj.parentNode;
	}
	if (obj.tagName == 'TEXTAREA' || obj.tagName == 'A') return;
	while (obj.nodeName != 'DIV' && obj.nodeName != 'HTML' && obj.className != 'CONTENT')
	{
		obj = obj.parentNode;
	}
	if (obj.nodeName == 'HTML') return;
	var x = obj.innerHTML;
	var y = document.createElement('TEXTAREA');
//	y.appendChild(document.createTextNode(x));
	var z = obj.parentNode;
	z.insertBefore(y,obj);
	z.insertBefore(butt,obj);
	z.removeChild(obj);
	y.value = x;
	y.focus();
	editing = true;
	return false;
}


function getDocument(workspace, document)
{
    var url = baseUrl + "getdocument/" + workspace + "/" + document;
    executeCommand(url, readDocument);
}

function createDocument(workspace, document)
{
    var url = baseUrl + "createdocument/" + workspace + "/" + document;
    executeCommand(url, readDocument);
}


function readDocument(xml)
{
    //addMessage(xml);
    var versionEl = document.getElementById('version');
    var spaceNameEl = document.getElementById('spaceName');
    var documentNameEl = document.getElementById('documentName');
    versionEl.innerHTML = xml.getElementsByTagName('version')[0].firstChild.data;
    spaceNameEl.innerHTML = xml.getElementsByTagName('workspace')[0].firstChild.data;
    documentNameEl.innerHTML = xml.getElementsByTagName('name')[0].firstChild.data;
    readBlocks(xml);
}

function myGetElementByTagName(node, tag){
    if (window.XMLHttpRequest) {
        //mozilla
        return node.getElementsByTagName(tag)[0];
    }
    for (j=0;j<node.childNodes.length;j++)
	{
        if (node.childNodes[j].nodeName == tag)
            return node.childNodes[j];
    }
}

function readBlocks(xml)
{
    var blocks = xml.getElementsByTagName('block');
    addMessage("blocks: " + blocks);
    for (var i = 0; i < blocks.length; i++)
    {
        readBlock(blocks[i]);
    }
}

function readBlock(xmlblock)
{
    var blocksEl = document.getElementById('blocks');
    var blockId = xmlblock.getElementsByTagName('id')[0].firstChild.data;
    //var blockId = myGetElementByTagName(xmlblock, 'id').firstChild.data;

    var blockEl = document.getElementById('block_'+blockId);
    if (blockEl == null)
    {
        blockEl = document.createElement('div');
        blockEl.setAttribute('id', blockId);
        blockEl.setAttribute('class', 'block');
        blocksEl.appendChild(blockEl);
    }

    var idEl = document.getElementById('id_'+blockId);
    if (positionEl == null)
    {
        positionEl = document.createElement('div');
        positionEl.setAttribute('id', 'position_'+blockId);
        blockEl.appendChild(positionEl);
    }
    positionEl.innerHTML = xmlblock.getElementsByTagName('position')[0].firstChild.data;

    var positionEl = document.getElementById('position_'+blockId);
    if (positionEl == null)
    {
        positionEl = document.createElement('div');
        positionEl.setAttribute('id', 'position_'+blockId);
        blockEl.appendChild(positionEl);
    }
    positionEl.innerHTML = xmlblock.getElementsByTagName('position')[0].firstChild.data;

    var contentEl = document.getElementById('content_'+blockId);
    if (contentEl == null)
    {
        contentEl = document.createElement('div');
        contentEl.setAttribute('id', 'content_'+blockId);
        contentEl.setAttribute('class', 'content');
        blockEl.appendChild(contentEl);
    }
    contentEl.innerHTML = decode64(xmlblock.getElementsByTagName('content')[0].firstChild.data);

}


function addMessage(text)
{
    if (debug == 1)
    {
        var message = document.getElementById('messages');
        message.innerHTML = "* " + text + "<br />" + message.innerHTML;
    }
}

//------------------
// threadsafe asynchronous XMLHTTPRequest code
function executeCommand(url, callback) {


    // we use a javascript feature here called "inner functions"
    // using these means the local variables retain their values after the outer function
    // has returned. this is useful for thread safety, so
    // reassigning the onreadystatechange function doesn't stomp over earlier requests.


    function ajaxBindCallback() {
        if (ajaxRequest.readyState == 4) {
            if (ajaxRequest.status == 200) {
                if (ajaxCallback) {
                    ajaxCallback(ajaxRequest.responseXML);
                } else {
                    alert('no callback defined');
                }
            } else {
                alert("There was a problem retrieving the xml data:\n" + ajaxRequest.status + ":\t" + ajaxRequest.statusText + "\n" + ajaxRequest.responseText);
            }
        }
    }

    addMessage(url);
    // use a local variable to hold our request and callback until the inner function is called...
    var ajaxRequest = null;
    var ajaxCallback = callback;


    // bind our callback then hit the server...
    if (window.XMLHttpRequest) {
        // moz et al
        ajaxRequest = new XMLHttpRequest();
        ajaxRequest.onreadystatechange = ajaxBindCallback;
        ajaxRequest.open("GET", url, true);
        ajaxRequest.send(null);
    } else if (window.ActiveXObject) {
        // ie
        ajaxRequest = new ActiveXObject("Microsoft.XMLHTTP");
        if (ajaxRequest) {
            ajaxRequest.onreadystatechange = ajaxBindCallback;
            ajaxRequest.open("GET", url, true);
            ajaxRequest.send();
        }
        else{
            alert("your browser does not support xmlhttprequest" )
        }

    }
    else{
        alert("your browser does not support xmlhttprequest" )
    }


}