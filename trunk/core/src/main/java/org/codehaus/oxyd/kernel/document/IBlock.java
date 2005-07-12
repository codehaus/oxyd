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


public interface IBlock {
    long getId();

    void setId(long id);

    String getUserName();

    void setUserName(String userName);

    long getVersion();

    void setVersion(long version);

    byte[] getContent();

    void setContent(byte[] content);

    String getPosition();

    void setPosition(String position);

    String getType();

    void setType(String type);

    boolean isLocked();

    void setLocked(boolean locked);

    boolean isRemoved();

    void setRemoved(boolean removed);

    Object clone();

    Element toXML();

    void fromXML(Element el);
}
