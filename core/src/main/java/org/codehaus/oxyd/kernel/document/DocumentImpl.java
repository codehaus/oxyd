/* ====================================================================
 *   Copyright 2005 J�r�mi Joslin.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * ====================================================================
 */
package org.codehaus.oxyd.kernel.document;

import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.auth.User;
import org.codehaus.oxyd.kernel.utils.Utils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.SAXReader;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;

import java.util.*;
import java.io.StringWriter;
import java.io.IOException;
import java.io.StringReader;

public class DocumentImpl implements IDocument {
    private String      name;
    private List        users;
    private long        version = 0;
    private Map         blocks;
    protected Map       lockedBlocks;
    private List        comments;
    private String      parentName;
    private String      directory;
    private IHistory    history;
    protected long      nextId = 1;
    private String      workspace;

    public DocumentImpl(String space, String name)
    {
        init(name, space);
    }

    public DocumentImpl(String name)
    {
        init(name, "");
    }

    public DocumentImpl() {
        init("", "");
    }

    private void init(String name, String space) {
        blocks = new HashMap();
        lockedBlocks = new HashMap();
        history = new HistoryImpl();
        comments = new ArrayList();
        version = 0;
        setName(name);
        setWorkspace(space);
    }

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public long getId() {
        return (workspace + "." + name).hashCode();
    }

    public void setId(long id) {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getUsers() {
        return users;
    }

    public void addUser(User user) {
        if (users == null)
            users = new ArrayList();
        if (!users.contains(user))
            users.add(user);
    }

     public void removeUser(User user) {
        if (users.contains(user))
            users.remove(user);
    }

    public void addUsersMsg(String msg, Context context)
    {
        Iterator it = users.iterator();
        while (it.hasNext())
        {
            User user = (User) it.next();
            user.addMessage(this.getWorkspace(), this.getName(), msg);
        }
    }

    public long getVersion() {
        return version;
    }

    public IBlock getVersion(long blockId, long version, Context context) {
        throw new Error("not implemented");
    }

    public IDocument getVersion(long version, Context context) {
        throw new Error("not implemented");
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getNextVersion()
    {
        return ++version;
    }

    public Map getBlocks() {
        return blocks;
    }

    public void setBlocks(Map blocks) {
        this.blocks = blocks;
    }

    public Map getLockedBlocks(){
        return lockedBlocks;
    }

    public List getComments() {
        return comments;
    }

    public List getComments(long blockId) {
        List comments = new ArrayList();
        for (int i = 0; i < this.comments.size(); i++)
            if (((IComment)this.comments.get(i)).getBlockId() == getId())
                comments.add(this.comments.get(i));
        return comments;
    }

    public void setComments(List comments) {
        this.comments = comments;
    }

    public void addComment(long blockId, String content, Context context)
    {
        IComment comment = new CommentImpl();
        comment.setBlockId(blockId);
        comment.setText(content);
        comment.setDocument(this);
        comment.setUserName(context.getUser().getLogin());
        comments.add(comment);
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public IHistory getHistory() {
        return history;
    }

    public void setHistory(IHistory history) {
        this.history = history;
    }

    public IBlock getBlock(long blockId, Context context) {
        if (isBlockLocked(blockId))
            return (IBlock) lockedBlocks.get(new Long(blockId));
        return (IBlock) blocks.get(new Long(blockId));
    }

    private boolean isEqual(byte[] b1, byte[] b2)
    {
        if (b1.length != b2.length)
            return false;
        for (int i = 0; i < b1.length; i++)
        {
            if (b1[i] != b2[i])
                return false;
        }
        return true;
    }

    public void updateBlock(long blockId, byte[] content, Context context) throws oxydException {
        if (!isBlockLocked(blockId))
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_NOT_LOCKED, "The block is not currently locked");
        IBlock block  = (IBlock) lockedBlocks.get(new Long(blockId));
        if (block == null)
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_NOT_EXIST, "This block does not exist");

        if (!isEqual(block.getContent(), content))
        {
            long  updateversion = getNextVersion();
            block.setContent(content);
            block.setVersion(updateversion);
            setVersion(updateversion);
        }
    }

    public boolean isBlockLocked(long blockId)
    {
        return lockedBlocks.containsKey(new Long(blockId));
    }

    public void lockBlock(long blockId, Context context) throws oxydException {
        if (isBlockLocked(blockId))
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_LOCKED, "This block is currently locked");
        IBlock block  = (IBlock) blocks.get(new Long(blockId));
        if (block != null)
        {
            IBlock lockedBlock = (IBlock) block.clone();
            lockedBlock.setLocked(true);
            lockedBlock.setUserName(context.getUser().getLogin());
            long tmpVersion = getNextVersion();
            lockedBlock.setVersion(tmpVersion);
            lockedBlocks.put(new Long(blockId), lockedBlock);
            setVersion(tmpVersion);
        }
        else
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_NOT_EXIST, "This block does not exist");
    }

    public void saveBlock(long blockId, Context context) throws oxydException {
        if (!isBlockLocked(blockId))
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_NOT_LOCKED, "This block is not currently locked");
        IBlock lockedBlock = (IBlock) lockedBlocks.get(new Long(blockId));
        blocks.put(new Long(blockId), lockedBlock.clone());
    }

    public void deleteBlock(long blockId, Context context) throws oxydException {
        if (isBlockLocked(blockId))
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_LOCKED, "The block is currently locked");
        IBlock block  = (IBlock) blocks.get(new Long(blockId));
        if (block != null)
        {
            block.setRemoved(true);
            long tmpVersion = getNextVersion();
            block.setUserName(context.getUser().getLogin());
            block.setVersion(tmpVersion);
            setVersion(tmpVersion);
        }
        else
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_NOT_EXIST, "The block does not exist");
    }

    public void unlockBlock(long blockId, Context context) throws oxydException {
        if (!isBlockLocked(blockId))
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_LOCKED, "The block is currently locked");
        IBlock block  = (IBlock) blocks.get(new Long(blockId));
        if (block != null)
        {
            lockedBlocks.remove(new Long(blockId));
            block.setLocked(false);
            long tmpVersion = getNextVersion();
            block.setVersion(tmpVersion);
            setVersion(tmpVersion);
            refactorBlock(block, context);
        }
        else
            throw new oxydException(oxydException.MODULE_DOCUMENT_IMPL, oxydException.ERROR_BLOCK_NOT_EXIST, "The block does not exist");
    }


    private void refactorBlock(IBlock block, Context context)
    {
        String content = new String(block.getContent());
        String[] tabs = content.split("\n\n");

        String pos = new Long(new Long(block.getPosition()).longValue() + 1).toString();
        for (int i = tabs.length - 1; i > 0; i--)
        {
            String tmpContent = tabs[i];
            tmpContent.replaceFirst("\n", "");
            this.createBlock(pos, tabs[i].getBytes(), context);
        }
        block.setContent(tabs[0].getBytes());
    }

    public List getUpdates(long sinceVersion, Context context) {
        if (sinceVersion >= getVersion())
            return null;
        List updates = new ArrayList();
        List blockId = new ArrayList();

        Iterator it = lockedBlocks.values().iterator();
        while(it.hasNext())
        {
            IBlock block = (IBlock) it.next();
            if (sinceVersion < block.getVersion())
            {
                updates.add(block);
                blockId.add(new Long(block.getId()));
            }
        }

        it = blocks.values().iterator();
        while(it.hasNext())
        {
            IBlock block = (IBlock) it.next();
            if (sinceVersion < block.getVersion() && !blockId.contains(new Long(block.getId())))
                updates.add(block);
        }

        return updates;
    }

    public Document toXMLDocument() {
        Document doc = new DOMDocument();
        Element docel = new DOMElement("document");
        doc.setRootElement(docel);

        Element el = new DOMElement("name");
        el.addText(getName());
        docel.add(el);

        if (getWorkspace() != null)
        {
            el = new DOMElement("workspace");
            el.addText(getWorkspace());
            docel.add(el);
        }

        if (getDirectory() != null)
        {
            el = new DOMElement("directory");
            el.addText(getDirectory());
            docel.add(el);
        }

        if (getParentName() != null)
        {
            el = new DOMElement("parentname");
            el.addText(getParentName());
            docel.add(el);
        }
        el = new DOMElement("version");
        el.addText(""+getVersion());
        docel.add(el);

        el = new DOMElement("blocks");
        docel.add(el);
        Iterator it = getBlocks().values().iterator();
        while (it.hasNext())
            el.add(((IBlock)it.next()).toXML());

        el = new DOMElement("lockedblocks");
        docel.add(el);
        it = lockedBlocks.values().iterator();
        while (it.hasNext())
            el.add(((IBlock)it.next()).toXML());

        el = new DOMElement("comments");
        if (getComments() != null)
        {
            docel.add(el);
            it = getComments().iterator();
            while (it.hasNext())
                el.add(((IComment)it.next()).toXML());
        }
        return doc;
    }

   public String toXML(Document doc) {
        OutputFormat outputFormat = new OutputFormat("", true);
        outputFormat.setEncoding("UTF-8");
        StringWriter out = new StringWriter();
        XMLWriter writer = new XMLWriter( out, outputFormat );
        try {
            writer.write(doc);
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
   }

   public String toXML()
   {
        return toXML(toXMLDocument());
   }


    public IBlock createBlock(String pos, byte[] content, Context context)
    {
        IBlock block = new BlockTextImpl(this);
        block.setContent(content);
        block.setRemoved(false);
        block.setUserName(context.getUser().getLogin());
        block.setId(nextId++);
        block.setPosition(new Long(getBlocks().size() + 1).toString());
        getBlocks().put(new Long(block.getId()), block);
        moveBlock(block.getId(), pos, context);
        return block;
    }


    public void moveBlock(long blockId, String pos, Context context)
    {
        IBlock block  = getBlock(blockId, context);
        long srcPos = new Long(block.getPosition()).longValue();
        long destPos = new Long(pos).longValue();

        long  moveversion = getNextVersion();

/*        if (destPos > getBlocks().size())
            destPos = getBlocks().size();*/
        if (destPos < 1)
            destPos = 1;
        Object[]  blockCol = getBlocks().values().toArray();
        for(int i = 0; i < blockCol.length; i++)
        {
            IBlock tmpBlock = ((IBlock) blockCol[i]);
            int tmpblockPos = (new Long(tmpBlock.getPosition())).intValue();
            if (!tmpBlock.isRemoved() && tmpblockPos >= destPos && destPos < srcPos)
            {
                tmpBlock.setPosition(new Long(tmpblockPos + 1).toString());
                if (isBlockLocked(tmpBlock.getId()))
                    getBlock(tmpBlock.getId(), context).setPosition(new Long(tmpblockPos + 1).toString());
               tmpBlock.setVersion(moveversion);
            }
        }

        block.setPosition(new Long(destPos).toString());
        block.setVersion(moveversion);
    }




    public void fromXML(String xml) throws oxydException {
        SAXReader reader = new SAXReader();
        Document domdoc = null;

        StringReader in = new StringReader(xml);
        try {
            domdoc = reader.read(in);
        } catch (DocumentException e) {
            throw new oxydException(oxydException.MODULE_DOCUMENT_TEXT_IMPL, oxydException.ERROR_XML_ERROR, "Could not read the XML file");
        }

        Element infosEl = domdoc.getRootElement();

        setName(Utils.getElementText(infosEl, "name"));
        setWorkspace(Utils.getElementText(infosEl, "workspace"));
        setDirectory(Utils.getElementText(infosEl, "directory"));
        setVersion(new Long(Utils.getElementText(infosEl, "version")).longValue());
        setParentName(Utils.getElementText(infosEl, "parentname"));

        Element blocksEl = infosEl.element("blocks");

        List ListFile =  blocksEl.elements("block");
        for (int i = 0; i < ListFile.size(); i++)
        {
            Element blockEl = ((Element)ListFile.get(i));
            IBlock block = new BlockTextImpl(this);
            block.fromXML(blockEl);
            this.getBlocks().put(new Long(block.getId()), block);
        }

        blocksEl = infosEl.element("lockedblocks");

        ListFile =  blocksEl.elements("block");
        for (int i = 0; i < ListFile.size(); i++)
        {
            Element blockEl = ((Element)ListFile.get(i));
            IBlock block = new BlockTextImpl(this);
            block.fromXML(blockEl);
            lockedBlocks.put(new Long(block.getId()), block);
        }
    }


}
