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

package org.codehaus.oxyd.server.plugins;

import org.codehaus.oxyd.server.IOxydPlugin;
import org.codehaus.oxyd.server.ServerContext;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.document.IDocument;
import org.dom4j.Document;

public abstract class OxydPlugin implements IOxydPlugin {

    protected String getAction(String command){
        return command.substring(1, command.indexOf("/", 1));
    }

    protected String getWorkspace(String command)
    {
        return command.substring(command.indexOf("/", 1) + 1, command.indexOf("/", command.indexOf("/", 1) + 1));
    }

    protected String getDocumentName(String command)
    {
        int startPos = command.indexOf("/", command.indexOf("/", 1) + 1);
        int endPos = command.indexOf(startPos + 1);
        if (endPos < 0)
            endPos = command.length();
        return command.substring(startPos + 1, endPos);
    }

    protected long getBlockId(String command)
    {
        int startPos = command.indexOf(command.indexOf("/", command.indexOf("/", 1) + 1) + 1);
        int endPos = command.indexOf(command.indexOf(command.indexOf("/", command.indexOf("/", 1) + 1) + 1) + 1);
        if (endPos < 0)
            endPos = command.length();
        return new Long(command.substring(startPos + 1, endPos)).longValue();
    }


    public Document execute(String command, ServerContext context) throws oxydException
    {
        throw new oxydException(oxydException.MODULE_PLUGIN, oxydException.ERROR_NOT_IMPLEMENTED);
    }

    public String beforeLogin(String userName, String pwd, ServerContext context) throws oxydException {
        return null;
    }

    public Boolean beforeHasRight(String userName, String pwd, ServerContext context)
    {
        return null;
    }

    public IDocument afterOpenningDocument(String space, String document, IDocument doc, ServerContext serverContext) throws oxydException {
        return doc;
    }

    public void afterClosingDocument(IDocument doc, ServerContext serverContext) throws oxydException {

    }

}
