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

import org.codehaus.oxyd.kernel.utils.Base64;
import org.codehaus.oxyd.client.Utils;
import org.dom4j.Element;

public class Block {
    private long        id;
    private String      userName;
    private long        version;
    private byte[]      content;
    private String      position;
    private String      type;
    private boolean     locked;
    private boolean     removed;
    private boolean     modified;
    private Document    doc;

    public Block(Document doc)
    {
        setDoc(doc);
    }

    public Block(Document doc, org.dom4j.Document xml)
    {
        this(doc);
        Element docEl = xml.getRootElement();
        Element infosEl = docEl.element("block");
        fromXML(infosEl);
    }

    public Document getDoc() {
        return doc;
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void fromXML(Element el) {
        setContent(Base64.decode(Utils.getElementText(el, "content").getBytes()));
        setPosition(Utils.getElementText(el, "position"));
        setRemoved(new Boolean(Utils.getElementText(el, "isremoved")).booleanValue());
        setType(Utils.getElementText(el, "type"));
        setUserName(Utils.getElementText(el, "username"));
        setVersion(new Long(Utils.getElementText(el, "version")).longValue());
        setId(new Long(Utils.getElementText(el, "id")).longValue());
        setLocked(new Boolean(Utils.getElementText(el, "islocked")).booleanValue());
    }
}
