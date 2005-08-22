var baseUrl = "/oxyd/command/";
var debug = 1;
var editing = false;

var workspace = null;
var documentName = null;

// current editing node infos
var editingContentNode = null;
var editingBlockNode = null;

// next Block to edit
var nextEditingNode = null;

//model for the block action
var blockActionModelNode = null;

//save some part of the xhtml
var topActionNode = null;
var topInfosNode = null;
var loginFormNode = null;
var buttonsNode = null;

var wikiUrl = "www.xwiki.org";

//key for getLoginKey
var key = null;


document.ondblclick = selectBlockToEdit;


window.onload = function() {
    buttonsNode = document.createElement('div');
    buttonsNode.id = "buttons";

    var buttonNode = document.createElement('a');
    buttonNode.id = "apply";
    buttonNode.innerHTML = "<img src=\"img/apply.png\" alt=\"Apply\" />";
    buttonNode.onclick = finishEditingBySaving;
    buttonsNode.appendChild(buttonNode);

    buttonNode = document.createElement('a');
    buttonNode.id = "cancel";
    buttonNode.innerHTML = "<img src=\"img/cancel.png\" alt=\"cancel\" />";
    buttonNode.onclick = finishEditingByCancel;
    buttonsNode.appendChild(buttonNode);



    blockActionModelNode = document.createElement('div');
    blockActionModelNode.setAttribute('id', 'actionModel');
    blockActionModelNode.setAttribute('class', 'action');

    var blockActionHTML = "<span class=\"blockActionButtons\"><a onclick=\" addBlock(this.parentNode.parentNode.parentNode.id)\"><img src=\"img/new.png\" alt=\"add a block\" class=\"actionButton\" onmouseover=\"onMouseOverButton(this)\" onmouseout=\"onMouseOutButton(this)\" /></a>";
    blockActionHTML = blockActionHTML + " <a onclick=\"deleteBlock(this.parentNode.parentNode.parentNode.id)\"><img src=\"img/trash.png\" alt=\"remove the block\" class=\"actionButton\" onmouseover=\"onMouseOverButton(this)\" onmouseout=\"onMouseOutButton(this)\" /></a></span>";

    blockActionHTML = blockActionHTML + "<span class=\"blockStatus\"></span>";

    blockActionModelNode.innerHTML = blockActionHTML;

    extractTop();
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

    el.style.borderLeft = "2px solid #0000FF";

    var blockId = el.id;
    var actionEl = document.getElementById('action_'+blockId);
    if (actionEl)
        actionEl.style.visibility = "visible";
}

function onMouseOutBlock(el)
{
                            
    el.style.borderLeft = "2px solid #FFFFFF";
    var blockId = el.id;
    var actionEl = document.getElementById('action_'+blockId);
    if (actionEl)
        actionEl.style.visibility = "hidden";
}


function selectBlockToEdit(e)
{
    if (editing)
    {
        nextEditingNode = e;
        finishEditingBySaving();
        return;
    }
    var ContentNode = null;
    if (!e) ContentNode = window.event.srcElement;
    else ContentNode = e.target;
    while (ContentNode.nodeType != 1)
    {
        ContentNode = obj.parentNode;
    }
    if (ContentNode.tagName == 'TEXTAREA' || ContentNode.tagName == 'A') return;
    while (ContentNode.nodeName != 'HTML' && ContentNode.className != 'content')
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

function extractTop()
{
    topActionNode = document.getElementById("formopendoc");
    topInfosNode = document.getElementById("infos");
    topActionNode.parentNode.removeChild(topActionNode);
    topInfosNode.parentNode.removeChild(topInfosNode);
}

function affDocumentInfos()
{
    var topEl = document.getElementById("top");
    try{
        topEl.removeChild(topActionNode);
    }
    catch(e)
    {}
    topEl.appendChild(topInfosNode);

    setUsers("");
    var sidebarEl = document.getElementById("sidebar");
    sidebarEl.style.visibility = "visible";

//    topInfosNode.style.visibility = "visible";

}

function affOpenFunctions()
{
    var topEl = document.getElementById("top");
    try{
        topEl.removeChild(topInfosNode);
    }
    catch(e)
    {}
    topEl.appendChild(topActionNode);

    var sidebarEl = document.getElementById("sidebar");
    sidebarEl.style.visibility = "hidden";

}




function isError(xml)
{
    var error = xml.getElementsByTagName('error');
    if (error.length == 0)
        return false;
    hideLoadingBox();
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
    if (documentNameEl)
        return(documentNameEl.innerHTML);
    return(null);
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

function openLoadingText(text)
{
    var loadingBoxEl = document.getElementById('loadingBox');
    loadingBoxEl.innerHTML = text;
    loadingBoxEl.style.visibility = "visible";
}

function hideLoadingBox()
{
    var loadingBoxEl = document.getElementById('loadingBox');
    loadingBoxEl.style.visibility = "hidden";
}

function login(login, pwd)
{
    var url = baseUrl + "login?login=" + login + "&pwd="+pwd;
    if (wikiUrl !=null)
        url = url + "&wikiServer=" + wikiUrl;
    openLoadingText("login");
    executeCommand(url, loginCallback);

}

function loginCallback(xml)
{
    hideLoadingBox();
    if (!isError(xml))
    {
        key = xml.getElementsByTagName('key')[0].firstChild.data;
        var loginEl = document.getElementById('loginForm');
        loginEl.parentNode.removeChild(loginEl);
        affOpenFunctions();
    }
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

    // addMessage(url);
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