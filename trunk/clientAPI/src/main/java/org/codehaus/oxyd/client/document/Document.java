/* ====================================================================
 *   Copyright 2005 Jérémi Joslin.
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

package org.codehaus.oxyd.client.document;

import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.client.Utils;
import org.dom4j.io.SAXReader;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.io.StringReader;

public class Document {
    private String      name;
    private Map         users;
    private long        version;
    private Map         blocks;
    private Map         lockedBlocks;
    private List        comments;
    private String      parentName;
    private String      directory;
    private String      workspace;

    public Document(org.dom4j.Document xmlDoc) throws oxydException {
        blocks = new HashMap();
        lockedBlocks = new HashMap();
        fromXML(xmlDoc);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getUsers() {
        return users;
    }

    public void setUsers(Map users) {
        this.users = users;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Map getBlocks() {
        return blocks;
    }

    public void setBlocks(Map blocks) {
        this.blocks = blocks;
    }

    public Map getLockedBlocks() {
        return lockedBlocks;
    }

    public void setLockedBlocks(Map lockedBlocks) {
        this.lockedBlocks = lockedBlocks;
    }

    public List getComments() {
        return comments;
    }

    public void setComments(List comments) {
        this.comments = comments;
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

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

   public void fromXML(org.dom4j.Document domdoc) throws oxydException {
        Element docEl = domdoc.getRootElement();
        Element infosEl = docEl.element("document");

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
            Block block = new Block(this);
            block.fromXML(blockEl);
            this.getBlocks().put(new Long(block.getId()), block);
        }

        blocksEl = infosEl.element("lockedblocks");
        ListFile =  blocksEl.elements("block");
        for (int i = 0; i < ListFile.size(); i++)
        {
            Element blockEl = ((Element)ListFile.get(i));
            Block block = new Block(this);
            block.fromXML(blockEl);
            lockedBlocks.put(new Long(block.getId()), block);
        }


    }
}
