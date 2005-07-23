
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