var baseUrl = "/oxyd/command/";
var debug = 1;
var editing = false;
var editingContentNode = null;
var editingBlockNode = null;
var workspace = null;
var documentName = null;
var topActionNode = null;
var topInfosNode = null;
var blockActionModelNode = null;

document.ondblclick = selectBlockToEdit;

if (document.getElementById && document.createElement)
{
	var butt = document.createElement('a');
    butt.id = "apply";
	butt.innerHTML = "<img src=\"img/apply.png\" alt=\"Apply\" />";
	butt.onclick = finishEditingBySaving;

    blockActionModelNode = document.createElement('div');
    blockActionModelNode.setAttribute('id', 'actionModel');
    blockActionModelNode.setAttribute('class', 'action');
    blockActionModelNode.innerHTML = "<a onclick=\" addBlock(this.parentNode.parentNode.id)\"><img src=\"img/new.png\" alt=\"add a block\" class=\"actionButton\" onmouseover=\"onMouseOverButton(this)\" onmouseout=\"onMouseOutButton(this)\" /></a>"
    blockActionModelNode.innerHTML = blockActionModelNode.innerHTML + " <a onclick=\" removeBlock(this.parentNode.parentNode.id)\"><img src=\"img/trash.png\" alt=\"remove the block\" class=\"actionButton\" onmouseover=\"onMouseOverButton(this)\" onmouseout=\"onMouseOutButton(this)\" /></a>"


}

function onMouseOverButton(el)
{
    if (window.XMLHttpRequest) {
        el.style.MozOpacity=1;
    }
    else
    {
        el.filters.alpha.opacity=100
    }
}

function onMouseOutButton(el)
{
    if (window.XMLHttpRequest) {
        el.style.MozOpacity=0.2;
    }
    else
    {
        el.filters.alpha.opacity=50;
    }
}

function onMouseOverBlock(el)
{

    el.style.border = "1px solid #0000FF";

    var blockId = el.id;
    var actionEl = document.getElementById('action_'+blockId);
    if (actionEl)
        actionEl.style.visibility = "visible";
}

function onMouseOutBlock(el)
{

    el.style.border = "1px solid #FFFFFF";
    var blockId = el.id;
    var actionEl = document.getElementById('action_'+blockId);
    if (actionEl)
        actionEl.style.visibility = "hidden";
}

function getUpdates()
{
   try {
        if (getDocumentName())
        {
            var url = baseUrl + "getupdates/" + getWorkspaceName() + "/" + getDocumentName() + "?sinceversion=" + getVersion();
            executeCommand(url, getUpdatesCallback);
            if (editing)
                updateBlock(isError);
        }
   }
    catch (e)
    {
        addMessage(e);
    }
    setTimeout("getUpdates()", 2000);
}

function getUpdatesCallback(xml){
    if (isError(xml))
        return;
    readBlocks(xml);
    setVersion(xml.getElementsByTagName('blocks')[0].getAttribute('version'));
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
                pos = Math.round(nodes[j].innerHTML) + 1;
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
    for (var j=0;j<nodes.length;j++)
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
    executeCommand(url, editBlockCallback1);
	return false;
}

function editBlockCallback1(xml)
{
    if (isError(xml))
    {
        editing = false;
        return;
    }
    var url = baseUrl + "getblock/" + getWorkspaceName() + "/" + getDocumentName() + "?blockid=" + editingBlockNode.id;
    executeCommand(url, editBlockCallback2);
}

function editBlockCallback2(xml)
{
    var content = "";
    if (isError(xml) || xml.getElementsByTagName('content').length == 0 || !xml.getElementsByTagName('content')[0].firstChild)
    {

    }
    else
        content = decode64(xml.getElementsByTagName('content')[0].firstChild.data);
	var y = document.createElement('TEXTAREA');
//	y.appendChild(document.createTextNode(x));
	var z = editingContentNode.parentNode;
	z.insertBefore(y,editingContentNode);
	z.insertBefore(butt,editingContentNode);
	z.removeChild(editingContentNode);
	y.value = content;
	y.focus();
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
    contentEl.innerHTML = "<pre>" + area.value + "</pre>";
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

function affDocumentInfos()
{
    if (topActionNode == null)
        topActionNode = document.getElementById("formopendoc");
    var topEl = document.getElementById("top");
    if (topInfosNode == null)
        topInfosNode = document.getElementById("infos");
    if (topActionNode)
        topEl.removeChild(topActionNode);
    topEl.appendChild(topInfosNode);
    topInfosNode.style.visibility = "visible";

}

function affOpenFunctions()
{
    var topEl = document.getElementById("top");
    if (topInfosNode)
        topEl.removeChild(topInfosNode);
    topEl.appendChild(topActionNode);

}

function getDocument(workspace, document)
{
    var url = baseUrl + "getdocument/" + workspace + "/" + document;
    affDocumentInfos();
    executeCommand(url, readDocument);
}

function createDocument(workspace, document)
{
    var url = baseUrl + "createdocument/" + workspace + "/" + document;
    affDocumentInfos();
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
    if (isError(xml))
    {
        affOpenFunctions();
        return;
    }
    setVersion(xml.getElementsByTagName('version')[0].firstChild.data);
    setWorkspaceName(xml.getElementsByTagName('workspace')[0].firstChild.data);
    setDocumentName(xml.getElementsByTagName('name')[0].firstChild.data);
    readBlocks(xml);
    setTimeout("getUpdates()", 2000);
}
 /*
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
}  */

function readBlocks(xml)
{
    var blocks = xml.getElementsByTagName('block');
    for (var i = 0; i < blocks.length; i++)
    {
        readBlock(blocks[i]);
    }
}

function setBlockPosition(blockEl, blockId, pos)
{
    var positionEl = document.getElementById('position_'+blockId);
    if (positionEl == null)
    {
        positionEl = document.createElement('div');
        positionEl.setAttribute('id', 'position_'+blockId);
        positionEl.setAttribute('class', 'position');
        blockEl.appendChild(positionEl);
    }
    positionEl.innerHTML = pos;
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
        blockEl.setAttribute("onmouseover", "onMouseOverBlock(this)");
        blockEl.setAttribute("onmouseout", "onMouseOutBlock(this)");
        //blocksEl.appendChild(blockEl);
    }

    var pos = xmlblock.getElementsByTagName('position')[0].firstChild.data;
    setBlockPosition(blockEl, blockId, pos);

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
        contentEl.innerHTML = "<pre>" + decode64(xmlblock.getElementsByTagName('content')[0].firstChild.data) + "</pre>";
    else
        contentEl.innerHTML = "";

    var actionEl = document.getElementById('action_'+blockId);
    if (actionEl == null)
    {
        //actionEl = document.createElement('div');
        actionEl = blockActionModelNode.cloneNode(true);
        actionEl.setAttribute('id', 'action_'+blockId);
        blockEl.appendChild(actionEl);
    }
    moveBlocktoHisPosition(blocksEl, blockEl, Math.round(pos));
    return blockEl;
}

function getBlockPosition(blockEl)
{
    var nodes = blockEl.childNodes;
    for (var j=0;j<nodes.length;j++)
    {
        if (nodes[j].className == "position")
            return Math.round(nodes[j].innerHTML);
    }
    return -1;
}

function moveBlocktoHisPosition(blocksEl, blockEl, pos)
{
    var nodes = blocksEl.childNodes;
    var i = 0;
    var flag = false;
    for (; i < nodes.length ; i++)
    {
        if (nodes[i].className == "block" && pos <= getBlockPosition(nodes[i]))
        {
            blocksEl.insertBefore(blockEl, nodes[i]);
            flag = true;
            break;
        }
    }
    if (!flag)
        blocksEl.appendChild(blockEl);
}


function addMessage(text)
{
    if (debug == 1)
    {
        var message = document.getElementById('messages');
        message.innerHTML = "* " + text + "<br />" + message.innerHTML;
        message.style.visibility = "visible";
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