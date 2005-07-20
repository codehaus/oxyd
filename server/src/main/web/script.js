var baseUrl = "/oxyd/command/";
var debug = 1;
var editing = false;
//var editNode = null;
var editingContentNode = null;
var editingBlockNode = null;
var workspace = null;
var documentName = null;

document.ondblclick = selectBlockToEdit;
/*
if (document.getElementById && document.createElement)
{
	var butt = document.createElement('BUTTON');
	var buttext = document.createTextNode('Save');
	butt.appendChild(buttext);
	butt.onclick = finishEditingBySaving;
}
 */

if (document.getElementById && document.createElement)
{
	var butt = document.createElement('a');
    butt.id = "apply";
	butt.innerHTML = "<img src=\"img/apply.png\" />";
	butt.onclick = finishEditingBySaving;
}

function getUpdates()
{
    if (getDocumentName())
    {
        var url = baseUrl + "getupdates/" + getWorkspaceName() + "/" + getDocumentName() + "?sinceversion=" + getVersion();
        executeCommand(url, getUpdatesCallback);
        if (editing)
            updateBlock(isError);
    }
    setTimeout("getUpdates()", 2000);
}

function getUpdatesCallback(xml){
    if (isError(xml))
        return;
    readBlocks(xml);
    setVersion(xml.getElementsByTagName('blocks')[0].getAttribute('version'));
}


function editBlockCallback(xml)
{
    if (isError(xml))
    {
        editing = false;
        return;
    }
    var x = editingContentNode.innerHTML;
	var y = document.createElement('TEXTAREA');
//	y.appendChild(document.createTextNode(x));
	var z = editingContentNode.parentNode;
	z.insertBefore(y,editingContentNode);
	z.insertBefore(butt,editingContentNode);
	z.removeChild(editingContentNode);
	y.value = x;
	y.focus();
}

function addBlock(blockId)
{
    if (editing)
    {
        addMessage("You can edit only one block at a time");
        return;
    }
    var previousBlock = document.getElementById(blockId);
    var pos = 0;
    if (previousBlock != null){
        var nodes = previousBlock.childNodes;
        for (j=0;j<nodes.length;j++)
        {
            if (nodes[j].className == "position")
            {
                pos = nodes[j].innerHTML + 1;
                break;
            }
        }
    }
    var url = baseUrl + "addblock/" + getWorkspaceName() + "/" + getDocumentName() + "?position=" + pos;
    executeCommand(url, addBlockCallback);
}

function addBlockCallback(xml){
    if (isError(xml))
        return;
    var blockEl = readBlock(xml);
    var nodes = blockEl.childNodes;
    for (j=0;j<nodes.length;j++)
    {
        if (nodes[j].className == "content")
        {
            editBlock(nodes[j]);
            break;
        }
    }

}

function selectBlockToEdit(e)
{
    if (editing) return;
    if (!document.getElementById || !document.createElement) return;
    var ContentNode = null;
    if (!e) ContentNode = window.event.srcElement;
    else ContentNode = e.target;
    while (ContentNode.nodeType != 1)
    {
        ContentNode = obj.parentNode;
    }
    if (ContentNode.tagName == 'TEXTAREA' || ContentNode.tagName == 'A') return;
    while (ContentNode.nodeName != 'DIV' && ContentNode.nodeName != 'HTML' && ContentNode.className != 'CONTENT')
    {
        ContentNode = ContentNode.parentNode;
    }
    if (ContentNode.nodeName == 'HTML') return;
    editBlock(ContentNode);
}

function editBlock(ContentNode)
{
	editingContentNode = ContentNode;
    editing = true;
    editingBlockNode = editingContentNode.parentNode;
    //Call the API
    var url = baseUrl + "lockblock/" + getWorkspaceName() + "/" + getDocumentName() + "?blockid=" + editingBlockNode.id;
    executeCommand(url, editBlockCallback);
	return false;
}

function unlockBlock(callbackFunction)
{
    if (!editing) return;

    var url = baseUrl + "unlockblock/" + getWorkspaceName() + "/" + getDocumentName() + "?blockid=" + editingBlockNode.id;
    executeCommand(url, callbackFunction);
    editing = false;
}

function xreplace(checkMe,toberep,repwith){
    var temp = checkMe;
    var i = temp.indexOf(toberep);
    while(i > -1){
        temp = temp.replace(toberep, repwith);
        i = temp.indexOf(toberep);
    }
    return temp;
}

function updateBlock(callbackFunction)
{
    if (!editing) return;
    var content = getEditingContent();
    if (content != null)
    {
        var url = baseUrl + "updateblock/" + getWorkspaceName() + "/" + getDocumentName() + "?blockid=" + editingBlockNode.id + "&content=" + UrlEncode(encode64(content));
        executeCommand(url, callbackFunction);
    }
}

function getEditingContent()
{
    var contentNode = document.getElementsByTagName('TEXTAREA');
    if (contentNode && contentNode[0] != null)
        return contentNode[0].value;
    return null;
}

function UrlEncode(str)
{
    str = xreplace(str, '=', '%3D');
    str = xreplace(str, '+', '%3D');
    str = xreplace(str, '/', '%3D');
    return str;
}

function finishEditingBySavingCallback1(xml)
{
    if (isError(xml))
        return;
    saveBlock(finishEditingBySavingCallback2);
}

function finishEditingBySavingCallback2(xml)
{
    if (isError(xml))
        return;
    unlockBlock(finishEditingBySavingCallback3);
}

function finishEditingBySavingCallback3(xml)
{
    if (isError(xml))
        return;
    var area = document.getElementsByTagName('TEXTAREA')[0];
    var contentEl = document.createElement('div');
    //var blockEl = area.parentNode;
    contentEl.setAttribute('id', 'content_'+editingBlockNode.id);
    contentEl.setAttribute('class', 'content');
    editingBlockNode.appendChild(contentEl);
    contentEl.innerHTML = area.value;
	editingBlockNode.insertBefore(contentEl,area);
	editingBlockNode.removeChild(area);
	//editingBlockNode.removeChild(document.getElementsByTagName('button')[0]);
    editingBlockNode.removeChild(document.getElementById('apply'));
}

function finishEditingBySaving()
{
    updateBlock(finishEditingBySavingCallback1);

	return false;
}

function saveBlock(callbackFunction)
{
    if (!editing) return;

    var url = baseUrl + "saveblock/" + getWorkspaceName() + "/" + getDocumentName() + "?blockid=" + editingBlockNode.id;
    executeCommand(url, callbackFunction);
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


function isError(xml)
{
    var error = xml.getElementsByTagName('error');
    if (error.length == 0)
        return false;

    addMessage(error[0].firstChild.data);
    return true;
}

function setWorkspaceName(name)
{
    var spaceNameEl = document.getElementById('spaceName');
    spaceNameEl.innerHTML = name;
}

function getWorkspaceName()
{
    var spaceNameEl = document.getElementById('spaceName');
    return spaceNameEl.innerHTML;
}

function setDocumentName(name)
{
    var documentNameEl = document.getElementById('documentName');
    documentNameEl.innerHTML = name;
}

function getDocumentName()
{
    var documentNameEl = document.getElementById('documentName');
    return(documentNameEl.innerHTML);
}

function setVersion(num)
{
    var versionEl = document.getElementById('version');
    versionEl.innerHTML = num;
}

function getVersion()
{
    var versionEl = document.getElementById('version');
    return(versionEl.innerHTML);
}


function readDocument(xml)
{
    setVersion(xml.getElementsByTagName('version')[0].firstChild.data);
    setWorkspaceName(xml.getElementsByTagName('workspace')[0].firstChild.data);
    setDocumentName(xml.getElementsByTagName('name')[0].firstChild.data);
    readBlocks(xml);
    setTimeout("getUpdates()", 2000);
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

    if ((editing == true) && editingBlockNode.id == blockId)
        return;

    var blockEl = document.getElementById(blockId);
    if (blockEl == null)
    {
        blockEl = document.createElement('div');
        blockEl.setAttribute('id', blockId);
        blockEl.setAttribute('class', 'block');
        blocksEl.appendChild(blockEl);
    }

    var positionEl = document.getElementById('position_'+blockId);
    if (positionEl == null)
    {
        positionEl = document.createElement('div');
        positionEl.setAttribute('id', 'position_'+blockId);
        positionEl.setAttribute('class', 'position');
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
    if (xmlblock.getElementsByTagName('islocked')[0].firstChild.data == "true")
        contentEl.className = "contentLocked";
    else
        contentEl.className = "content";
    if (xmlblock.getElementsByTagName('content')[0].firstChild)
        contentEl.innerHTML = decode64(xmlblock.getElementsByTagName('content')[0].firstChild.data);
    else
        contentEl.innerHTML = " ";

    var actionEl = document.getElementById('action_'+blockId);
    if (actionEl == null)
    {
        actionEl = document.createElement('div');
        actionEl.setAttribute('id', 'action_'+blockId);
        actionEl.setAttribute('class', 'action');
        blockEl.appendChild(actionEl);
        actionEl.innerHTML = "<a onclick=\"addBlock(" + blockId + ")\"><img src=\"img/button_add.png\" alt=\"add\" /></a>"
    }
    return blockEl;
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
                addMessage("There was a problem retrieving the xml data:\n" + ajaxRequest.status + ":\t" + ajaxRequest.statusText + "\n" + ajaxRequest.responseText);
            }
        }
    }

    //addMessage(url);
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