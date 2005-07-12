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
package org.codehaus.oxyd.kernel.document;

import org.dom4j.Element;
import org.dom4j.dom.DOMElement;

public class CommentImpl implements IComment {
    private long        id;
    private long        blockId;
    private String      userName;
    private String      text;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBlockId() {
        return blockId;
    }

    public void setBlockId(long blockId) {
        this.blockId = blockId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Element toXML() {
        Element commentel = new DOMElement("comment");

        Element el = new DOMElement("id");
        el.addText(""+getId());
        commentel.add(el);

        el = new DOMElement("blockid");
        el.addText(""+getBlockId());
        commentel.add(el);

        el = new DOMElement("text");
        el.addText(getText());
        commentel.add(el);

        el = new DOMElement("username");
        el.addText(getUserName());
        commentel.add(el);


        return commentel;
    }

    public void fromXML(Element el) {
        throw new Error("not implemented");
    }
}
