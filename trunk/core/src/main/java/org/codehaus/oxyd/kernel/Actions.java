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
import org.codehaus.oxyd.kernel.document.IBlock;
import org.codehaus.oxyd.kernel.utils.Utils;
import org.codehaus.oxyd.kernel.store.IStore;
import org.apache.commons.transaction.locking.ReadWriteLockManager;
import org.apache.commons.transaction.util.PrintWriterLogger;
import org.apache.commons.transaction.util.LoggerFacade;

import java.util.*;
import java.io.PrintWriter;


/**
 * TODO terminer le lock, et verifier que l'on apelle bien release même en cas d'erreur
 */
public class Actions {
    private Map                     workspaces;
    private IStore                  storeService;
    private ReadWriteLockManager    lockManager;
    protected static final long TIMEOUT = 10000;


    public Actions()
    {
        workspaces = new HashMap();
        LoggerFacade logFacade = new PrintWriterLogger(new PrintWriter(System.out),
            Actions.class.getName(), false);
        lockManager = new ReadWriteLockManager(logFacade, TIMEOUT);

    }

    public Actions(IStore store)
    {
        this();
        setStoreService(store);
    }

    public IStore getStoreService() {
        return storeService;
    }

    public void setStoreService(IStore storeService) {
        this.storeService = storeService;
    }

    public List getWorkspacesNames(Context context)
    {
        List workspacesNames = new ArrayList();
        Iterator it = workspaces.values().iterator();

        while(it.hasNext())
            workspacesNames.add(((Workspace)it.next()).getName());
        return workspacesNames;
    }

    private Workspace getWorkspace(String workspaceName, Context context)
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
        IDocument doc;

        try {
        if (lockManager.tryReadLock(getOwnerId(), workspace + "." + docName))
        {
            if (isDocumentCached(workspace, docName, context))
            {
                Workspace space = getWorkspace(workspace, context);
                doc = space.getDocument(docName, context);
             }
            else if (storeService != null)
            {
                doc = storeService.openDocument(workspace, docName, context);
                if (doc != null)
                {
                    if (!isWorkspaceExist(workspace, context))
                        createWorkspace(workspace,  context);
                    Workspace space = getWorkspace(workspace, context);
                    space.addDocument(doc, context);
                }
            }
            else
                throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_DOCUMENT_NOT_EXIST, "Document does not exist");
            if (doc != null)
            {
                doc.addUser(context.getUser());
                context.getUser().addOpenDocument(doc);
            }
        }
        else
             throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_SYSTEM_LOCK, "can't lock this document");
        }
        finally{
            lockManager.release(getOwnerId(), workspace + "." + docName);
        }
        return doc;
    }

    public void UpdateDocumentBlock(String workspace, String docName, long blockId, byte[] content, Context context) throws oxydException {
        try {
            if (lockManager.tryWriteLock(getOwnerId(), workspace + "." + docName))
            {
                IDocument doc = getDocument(workspace, docName, context);
                doc.updateBlock(blockId, content, context);
            }
            else
                 throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_SYSTEM_LOCK, "can't lock this block");
        }
        finally{
            lockManager.release(getOwnerId(), workspace + "." + docName);
        }
    }

    public void getDocumentUsers(String workspace, String docName, Context context) throws oxydException {
        IDocument doc = getDocument(workspace, docName, context);
    }

    public List getUpdate(String workspace, String docName, long sinceVersion, Context context) throws oxydException {
        try {
            if (lockManager.tryReadLock(getOwnerId(), workspace + "." + docName))
            {
                if (isDocumentCached(workspace, docName, context))
                {
                    List updates = null;
                    Workspace space = getWorkspace(workspace, context);
                    updates = space.getDocument(docName, context).getUpdates(sinceVersion, context);                    return updates;
                }
                throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_ALREADY_EXIST, "Document Already exist");
            }else
                 throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_SYSTEM_LOCK, "can't lock this block");
        }
        finally{
            lockManager.release(getOwnerId(), workspace + "." + docName);
        }
    }

    public void lockDocumentBlock(String workspace, String docName, long blockId, Context context) throws oxydException {
        try{
            if (lockManager.tryWriteLock(getOwnerId(), workspace + "." + docName))
            {
                IDocument doc = getDocument(workspace, docName, context);
                doc.lockBlock(blockId, context);
            }
            else
                throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_SYSTEM_LOCK, "can't lock this block");
        }
        finally{
            lockManager.release(getOwnerId(), workspace + "." + docName);
        }
    }

    public void saveDocumentBlock(String workspace, String docName, long blockId, Context context) throws oxydException {
        try{
            if (lockManager.tryWriteLock(getOwnerId(), workspace + "." + docName))
            {
                IDocument doc = getDocument(workspace, docName, context);

                doc.saveBlock(blockId, context);
                if (getStoreService() != null)
                {
                    getStoreService().saveDocument(doc, context);
                }
            }
            else
                throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_SYSTEM_LOCK, "can't lock this block");
        }
        finally{
            lockManager.release(getOwnerId(), workspace + "." + docName);
        }

    }

    public void unlockDocumentBlock(String workspace, String docName, long blockId, Context context) throws oxydException {
        IDocument doc = getDocument(workspace, docName, context);
        try{
            if (lockManager.tryWriteLock(getOwnerId(), workspace + "." + docName))
            {
                doc.unlockBlock(blockId, context);
                if (getStoreService() != null)
                {
                    getStoreService().saveDocument(doc, context);
                }
            }
            else
               throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_SYSTEM_LOCK, "can't lock this block");
        }
        finally{
            lockManager.release(getOwnerId(), workspace + "." + docName);
        }
    }

    public IBlock addDocumentBlock(String workspace, String docName, String pos, byte[] content, Context context) throws oxydException {
        IDocument doc = getDocument(workspace, docName, context);
        try{
            if (lockManager.tryWriteLock(getOwnerId(), workspace + "." + docName))
            {
                IBlock block = doc.createBlock(pos, content, context);
                if (getStoreService() != null)
                {
                    getStoreService().saveDocument(doc, context);
                }
                return block;
            }
            else
                throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_SYSTEM_LOCK, "can't lock this block");
        }
        finally{
            lockManager.release(getOwnerId(), workspace + "." + docName);
        }
    }



    public List getDocumentVersion(String workSpace, String docName, long Version, Context context)
    {
        throw new Error("not Implemented");
    }

    /**
     * TODO create the document in the store
     * @param workspace
     * @param docName
     * @param context
     * @return
     * @throws oxydException
     */
    public IDocument createDocument(String workspace, String docName, Context context) throws oxydException {
        docName = Utils.noaccents(docName).replaceAll("[^(\\w| )]", "");
        try{
            if (lockManager.tryWriteLock(getOwnerId(), workspace + "." + docName))
            {
                if (!isWorkspaceExist(workspace, context))
                    createWorkspace(workspace,  context);
                if (!isDocumentCached(workspace, docName, context))
                {
                    Workspace space = getWorkspace(workspace, context);
                    IDocument doc = space.createDocument(docName, context);
                    doc.addUser(context.getUser());
                    context.getUser().addOpenDocument(doc);
                    return doc;
                }
                throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_ALREADY_EXIST, "Document Already exist");
            }
            else
                throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_SYSTEM_LOCK, "can't lock this block");
        }
        finally{
            lockManager.release(getOwnerId(), workspace + "." + docName);
        }
    }

    private boolean isDocumentCached(String workspace, String docName, Context context) throws oxydException {
        if (isWorkspaceExist(workspace, context))
        {
            Workspace space = getWorkspace(workspace, context);
            return space.isDocumentExist(docName, context);
        }
        else
            return false;
    }

    public Workspace createWorkspace(String workspace, Context context) throws oxydException {
        workspace = Utils.noaccents(workspace);        //[^(\w| )]
        if (isWorkspaceExist(workspace, context))
            throw new oxydException(oxydException.MODULE_ACTION, oxydException.ERROR_ALREADY_EXIST, "Workspace already exist");
        Workspace space = new Workspace(workspace, storeService);
        workspaces.put(workspace, space);
        return space;
    }

    public boolean isWorkspaceExist(String workspace, Context context)
    {
        return (workspaces.get(workspace) != null);
    }

    private Object getOwnerId()
    {
        return Thread.currentThread().toString();
        //return context.get("ownerId");
    }

}
