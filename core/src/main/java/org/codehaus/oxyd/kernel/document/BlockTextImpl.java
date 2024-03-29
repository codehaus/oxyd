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

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;
import org.codehaus.oxyd.kernel.utils.Utils;
import org.codehaus.oxyd.kernel.utils.Base64;

public class            BlockTextImpl implements IBlock {
    private long        id;
    private IDocument   doc;
    private String      userName;
    private long        version;
    private byte[]      content;
    private String      position;
    private String      type;
    private boolean     locked;
    private boolean     removed;

    public BlockTextImpl(IDocument doc)
    {
        this.doc = doc;
    }

    public BlockTextImpl()
    {
        this.doc = doc;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setBlockId(long id)
    {
    }

    public long getBlockId()
    {
        return (doc.getWorkspace() + "." + doc.getName() + "." +getId()).hashCode();
    }

    public IDocument getDoc() {
        return doc;
    }

    public void setDoc(IDocument doc) {
        this.doc = doc;
    }

    public long getDocId() {
        return doc.getId();
    }

    public void setDocId(long id) {
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

    public Object clone()
    {
        IBlock block = new BlockTextImpl(doc);
        if (getContent() != null)
            block.setContent((byte[])getContent().clone());
        block.setId(getId());
        block.setLocked(isLocked());
        block.setRemoved(isRemoved());
        if (getPosition() != null)
            block.setPosition(new String(getPosition()));
        if (getType() != null)
            block.setType(new String(getType()));
        block.setUserName(getUserName());
        block.setVersion(getVersion());
        return block;
    }

    public Element toXML() {
        Element blockel = new DOMElement("block");

        Element el = new DOMElement("id");
        el.addText(""+getId());
        blockel.add(el);

        el = new DOMElement("position");
        el.addText(getPosition());
        blockel.add(el);

        if (getType() != null)
        {
            el = new DOMElement("type");
            el.addText(getType());
            blockel.add(el);
        }

        el = new DOMElement("username");
        if (getUserName() != null)
            el.addText(getUserName());
        blockel.add(el);

        el = new DOMElement("isremoved");
        el.addText(new Boolean(isRemoved()).toString());
        blockel.add(el);

        el = new DOMElement("islocked");
        el.addText(new Boolean(isLocked()).toString());
        blockel.add(el);

        el = new DOMElement("version");
        el.addText(""+getVersion());
        blockel.add(el);

        el = new DOMElement("content");
        if (getContent()!=null) {
         String content = new String(Base64.encode(getContent()));
         el.addText(content);
        } else {
            el.addText("");
        }
        blockel.add(el);

        return (blockel);

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
