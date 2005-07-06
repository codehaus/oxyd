package org.codehaus.oxyd.kernel;
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


import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.Authentication.RightService;
import org.codehaus.oxyd.kernel.Authentication.AuthService;

import java.util.*;


public class Actions {
    private Map             workspaces;
    private RightService    rightService;
    private AuthService     authService;
    private String          storeService;



    public Actions()
    {
      workspaces = new HashMap();
    }

    public List getWorkspacesNames(Context context)
    {
        List workspacesNames = new ArrayList();
        Iterator it = workspaces.values().iterator();

        while(it.hasNext())
            workspacesNames.add(((Workspace)it.next()).getName());
        return workspacesNames;
    }

    private Workspace getWorkspaces(String workspaceName, Context context)
    {
        return (Workspace) workspaces.get(workspaceName);
    }

    public List getWorkspaceDocumentsName(String spaceName, Context context)
    {
        Workspace space = (Workspace) workspaces.get(spaceName);
        List docs = new ArrayList();
        if (space == null)
            return null;
        Iterator it = space.getDocuments(context).values().iterator();
        while (it.hasNext())
            docs.add(((IDocument)it.next()).getName());
        return docs;
    }

    public IDocument getDocument(String workspace, String docName, Context context) throws oxydException
    {
        if (isDocumentExist(workspace, docName, context))
        {
            Workspace space = getWorkspaces(workspace, context);
            return space.getDocument(docName, context);
        }
        throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_ALREADY_EXIST, "Document Already exist");

    }

    public void UpdateDocument(String workspace, String docName, long blockId, byte[] content, Context context) throws oxydException {
        IDocument doc = getDocument(workspace, docName, context);
        doc.updateBlock(blockId, content, context);
    }

    public List getUpdate(String workspace, String docName, long sinceVersion, Context context) throws oxydException {
        if (isDocumentExist(workspace, docName, context))
        {
            Workspace space = getWorkspaces(workspace, context);
            return space.getDocument(docName, context).getUpdates(sinceVersion, context);
        }
        throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_ALREADY_EXIST, "Document Already exist");

    }

    public void lockDocumentBlock(String workpace, String docName, long blockId, Context context) throws oxydException {
        IDocument doc = getDocument(workpace, docName, context);
        doc.lockBlock(blockId, context);
    }

    public void unlockDocumentBlock(String workspace, String docName, long blockId, Context context) throws oxydException {
        IDocument doc = getDocument(workspace, docName, context);
        doc.unlockBlock(blockId, context);
    }

    public List getDocumentVersion(String workSpace, String docName, long Version, Context context)
    {
        throw new Error("not Implemented");
    }

    public IDocument createDocument(String workspace, String docName, Context context) throws oxydException {
        docName = Utils.noaccents(docName).replaceAll("[^(\\w| )]", "");
        if (!isDocumentExist(workspace, docName, context))
        {
            Workspace space = getWorkspaces(workspace, context);
            return space.createDocument(docName, context);
        }
        throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_ALREADY_EXIST, "Document Already exist");
    }

    private boolean isDocumentExist(String workspace, String docName, Context context) throws oxydException {
        if (isWorkspaceExist(workspace, context))
        {
            Workspace space = getWorkspaces(workspace, context);
            return space.isDocumentExist(docName, context);
        }
        else
            throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_WORKSPACE_NOT_EXIST, "this workspace does not exist");
    }

    public Workspace createWorkspace(String workspace, Context context) throws oxydException {
        workspace = Utils.noaccents(workspace).replaceAll("[^(\\w| )]", "");
        if (isWorkspaceExist(workspace, context))
            throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_ALREADY_EXIST, "Workspace already exist");
        Workspace space = new Workspace(workspace);
        workspaces.put(workspace, space);
        return space;
    }

    public boolean isWorkspaceExist(String workspace, Context context)
    {
        return (workspaces.get(workspace) != null);
    }


}
