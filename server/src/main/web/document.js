function getUpdates()
{
   try {
        if (getDocumentName())
        {
            var url = baseUrl + "getupdates/" + getWorkspaceName() + "/" + getDocumentName() + "?sinceversion=" + getVersion() + "&key=" + key;
            executeCommand(url, getUpdatesCallback);
            if (editing)
                updateBlock(isError);
        }
   }
    catch (e)
    {
        addMessage(e);
    }
    if (getDocumentName())
    {
        setTimeout("getUpdates()", 2000);
    }
}

function getUpdatesCallback(xml){
    if (isError(xml))
        return;
    readBlocks(xml);
    readUsers(xml);
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
    var url = baseUrl + "addblock/" + getWorkspaceName() + "/" + getDocumentName() + "?position=" + pos + "&key=" + key;
    openLoadingText("Loading");
    executeCommand(url, addBlockCallback);
}

function addBlockCallback(xml){
    hideLoadingBox();
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

function getDocument(workspace, document)
{
    var url = baseUrl + "opendocument/" + workspace + "/" + document + "?key=" + key;
    openLoadingText("Loading");
    affDocumentInfos();
    executeCommand(url, readDocument);
}

function createDocument(workspace, document)
{
    var url = baseUrl + "createdocument/" + workspace + "/" + document + "?key=" + key;
    affDocumentInfos();
    executeCommand(url, readDocument);
}

function closeDocument(workspace, document)
{
    var url = baseUrl + "closedocument/" + workspace + "/" + document + "?key=" + key;
    executeCommand(url, closeDocumentCallback);
}

function closeDocumentCallback(xml)
{
    if (!isError(xml))
    {
        var blocksEl = document.getElementById('blocks');
        blocksEl.innerHTML = "";
        affOpenFunctions();
    }
}

function saveWikiDocument(workspace, document)
{
    var url = baseUrl + "plugin/xwiki/save/" + workspace + "/" + document + "?key=" + key;
    executeCommand(url, saveWikiDocumentCallback);
}

function saveWikiDocumentCallback(xml)
{
    if (!isError(xml))
    {
        var wikiSaveDocumentStatusEl = document.getElementById('WikiSaveDocumentStatus');
        wikiSaveDocumentStatusEl.innerHTML = "Ok";
    }
}


function readDocument(xml)
{
    if (isError(xml))
    {
        affOpenFunctions();
        return;
    }
    hideLoadingBox();
    setVersion(xml.getElementsByTagName('version')[0].firstChild.data);
    setWorkspaceName(xml.getElementsByTagName('workspace')[0].firstChild.data);
    setDocumentName(xml.getElementsByTagName('name')[0].firstChild.data);
    readBlocks(xml);
    setTimeout("getUpdates()", 2000);
}


function readBlocks(xml)
{
    var blocks = xml.getElementsByTagName('block');
    for (var i = 0; i < blocks.length; i++)
    {
        readBlock(blocks[i]);
    }
}

function setUsers(htmlUsers)
{
    var UsersEl = document.getElementById('users');
    UsersEl.innerHTML = htmlUsers;
}

function getUsers()
{
    var UsersEl = document.getElementById('users');
    return (UsersEl.innerHTML);

}

function readUsers(xml)
{
    var users = xml.getElementsByTagName('user');
    var htmlUsers = "<ul>";
    for (var i = 0; i < users.length; i++)
    {
        htmlUsers = htmlUsers + "<li>" + users[i].firstChild.data + "</li>";
    }
    htmlUsers = htmlUsers + "</ul>";
    if (getUsers() != htmlUsers)
        setUsers(htmlUsers);
}