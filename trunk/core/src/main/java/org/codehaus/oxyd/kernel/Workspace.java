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
package org.codehaus.oxyd.kernel;

import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.document.DocumentTextImpl;

import java.util.Map;
import java.util.HashMap;

public class Workspace {
    private String  name;
    private Map     documents;

    public Workspace(String name)
    {
        this.name = name;
        documents = new HashMap();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getDocuments(Context context)
    {
        return documents;
    }

    public boolean isDocumentExist(String docName, Context context)
    {
        return (documents.get(docName) != null);
    }

    public IDocument getDocument(String docName, Context context) throws oxydException
    {
        if (isDocumentExist(docName, context))
            return (IDocument) documents.get(docName);
        throw new oxydException(oxydException.MODULE_WORKSPACE, oxydException.ERROR_DOCUMENT_NOT_EXIST, "This document does not exist");
    }

    public IDocument createDocument(String docName, Context context) throws oxydException {
        if (isDocumentExist(docName, context))
            throw new oxydException(oxydException.MODULE_WORKSPACE, oxydException.ERROR_ALREADY_EXIST, "This document already exist");
        IDocument  doc = new DocumentTextImpl(getName(), docName);
        documents.put(docName, doc);
        return doc;
    }


}
