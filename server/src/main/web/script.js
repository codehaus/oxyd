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


function xreplace(checkMe,toberep,repwith){
    var temp = checkMe;
    var i = temp.indexOf(toberep);
    while(i > -1){
        temp = temp.replace(toberep, repwith);
        i = temp.indexOf(toberep);
    }
    return temp;
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