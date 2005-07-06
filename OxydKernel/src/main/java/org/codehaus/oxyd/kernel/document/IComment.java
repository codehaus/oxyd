package org.codehaus.oxyd.kernel.document;
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
import org.dom4j.Element;



public interface IComment {
    long getId();

    void setId(long id);

    long getBlockId();

    void setBlockId(long blockId);

    String getUserName();

    void setUserName(String userName);

    String getText();

    void setText(String text);

    Element toXML();

    void fromXML(Element el);
}
