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

import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.oxydException;
import org.dom4j.Document;

import java.util.Map;
import java.util.List;


public interface IDocument {
    long getId();

    void setId(long id);

    String getName();

    void setName(String name);

    Map getUsers();

    void setUsers(Map users);

    long getVersion();

    /**
     * TODO rename this function who is not explicit
     * @param blockId
     * @param version
     * @param context
     * @return
     */
    IBlock getVersion(long blockId, long version, Context context);

    /**
     * TODO rename this function who is not explicit
     * @param version
     * @param context
     * @return
     */
    IDocument getVersion(long version, Context context);

    void setVersion(long version);

    Map getBlocks();

    void setBlocks(Map blocks);

    List getComments();

    List getComments(long id);

    void setComments(List comments);

    String getParentName();

    void setParentName(String parentName);

    java.lang.String getDirectory();

    void setDirectory(String directory);

    IHistory getHistory();

    void setHistory(IHistory history);

    IBlock getBlock(long blockId, Context context);

    void updateBlock(long blockId, byte[] content, Context context) throws oxydException;

    IBlock createBlock(String pos, byte[] content, Context context);

    void moveBlock(long blockId, String pos, Context context);

    void lockBlock(long blockId, Context context) throws oxydException;

    void saveBlock(long blockId, Context context) throws oxydException;

    void removeBlock(long blockId, Context context) throws oxydException;

    void unlockBlock(long blockId, Context context) throws oxydException;

    public List getUpdates(long sinceVersion, Context context);

    public String toXML();

    public Document toXMLDocument();

    public void fromXML(String xml) throws oxydException;

    public String getWorkspace();

    public void setWorkspace(String workspace);

    public boolean isBlockLocked(long blockId);


}
