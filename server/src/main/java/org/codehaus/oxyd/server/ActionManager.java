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
package org.codehaus.oxyd.server;

import org.codehaus.oxyd.kernel.Actions;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.utils.Base64;
import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.document.IBlock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.*;
import java.util.List;
import java.io.IOException;

public class ActionManager extends HttpServlet{
    Actions         actions = new Actions();
    ServletConfig   config;

    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    {
        doGet(req, resp);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    {
        String action = getActionName(req);
        ServerContext serverContext = new ServerContext();

        serverContext.setKernelContext(new Context());
        serverContext.getKernelContext().setAction(action);
        serverContext.setServletContext(config.getServletContext());

        try{
            if (action.compareTo("listworkspaces") == 0)
            {
                List workspaces = actions.getWorkspacesNames(serverContext.getKernelContext());
                render.listWorkspaces(workspaces, resp);
            }

            else if (action.compareTo("listworkspacedocuments") == 0)
            {
                List docs = actions.getWorkspaceDocumentsName(getWorkspaceName(req), serverContext.getKernelContext());
                render.listWorkspaceDocuments(docs, resp);
            }


            else if (action.compareTo("getdocument") == 0)
            {
                IDocument doc = actions.getDocument(getWorkspaceName(req), getDocumentName(req), serverContext.getKernelContext());
                render.returnDocument(doc, resp);
            }

            else if (action.compareTo("createdocument") == 0)
            {
                IDocument doc = actions.createDocument(getWorkspaceName(req), getDocumentName(req), serverContext.getKernelContext());
                render.returnDocument(doc, resp);
            }

            else if (action.compareTo("addblock") == 0)
            {
                IBlock block = actions.addDocumentBlock(getWorkspaceName(req), getDocumentName(req), getPos(req),getContent(req), serverContext.getKernelContext());
                render.returnBlock(block, resp);
            }

            else if (action.compareTo("updateblock") == 0)
            {
                actions.UpdateDocumentBlock(getWorkspaceName(req), getDocumentName(req), getBlockId(req),getContent(req), serverContext.getKernelContext());
                render.returnOk(resp);
            }

            else if (action.compareTo("moveblock") == 0)
            {
                throw new oxydException(oxydException.MODULE_ACTION_MANAGER, oxydException.ERROR_NOT_IMPLEMENTED, "not implemented");
            }

            else if (action.compareTo("getupdates") == 0)
            {
                List updates = actions.getUpdate(getWorkspaceName(req), getDocumentName(req), getSinceVersion(req), serverContext.getKernelContext());
                render.returnUpdates(updates, resp);
            }

            else if (action.compareTo("lockblock") == 0)
            {
                actions.lockDocumentBlock(getWorkspaceName(req), getDocumentName(req), getBlockId(req), serverContext.getKernelContext());
                render.returnOk(resp);
            }

            else if (action.compareTo("saveblock") == 0)
            {
                actions.saveDocumentBlock(getWorkspaceName(req), getDocumentName(req), getBlockId(req), serverContext.getKernelContext());
                render.returnOk(resp);
            }

            else if (action.compareTo("unlockblock") == 0)
            {
                actions.unlockDocumentBlock(getWorkspaceName(req), getDocumentName(req), getBlockId(req), serverContext.getKernelContext());
                render.returnOk(resp);
            }

            else
            {
                throw new oxydException(oxydException.MODULE_ACTION_MANAGER, oxydException.ERROR_COMMAND_NOT_FOUND, "Command not found");
            }
        }
        catch (oxydException e)
        {
            try {
                render.ReturnError(e, resp);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        catch (IOException ioe)
        {
            
        }

    }


    private long getBlockId(HttpServletRequest req)
    {
        return new Long(req.getParameter("blockid")).longValue();
    }

    private long getSinceVersion(HttpServletRequest req)
    {
        return new Long(req.getParameter("sinceVersion")).longValue();
    }

    private byte[] getContent(HttpServletRequest req)
    {
        return Base64.decode(req.getParameter("content").getBytes());
    }

    private String getPos(HttpServletRequest req)
    {
        return req.getParameter("position");
    }

    private String getActionName(HttpServletRequest req)
    {
        try {
            String url;
            url = req.getRequestURI();
            int startWorkSpace = url.indexOf("/", 0);
            startWorkSpace = url.indexOf("/", startWorkSpace + 1) + 1;
            int endWorkspace = url.indexOf("/", startWorkSpace);
            if (endWorkspace <= 0)
                endWorkspace = url.length();
            return (url.substring(startWorkSpace, endWorkspace));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private String getWorkspaceName(HttpServletRequest req)
    {
        try {
            String url;
            url = req.getRequestURI();
            int startWorkSpace = url.indexOf("/", 0);
            startWorkSpace = url.indexOf("/", startWorkSpace + 1);
            startWorkSpace = url.indexOf("/", startWorkSpace + 1) + 1;
            int endWorkspace = url.indexOf("/", startWorkSpace);
            if (endWorkspace <= 0)
                endWorkspace = url.length();
            return (url.substring(startWorkSpace, endWorkspace));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private String getDocumentName(HttpServletRequest req)
    {
        try {
            String url;
            url = req.getRequestURI();
            int startDocument = url.indexOf("/", 0);
            startDocument = url.indexOf("/", startDocument + 1);
            startDocument = url.indexOf("/", startDocument + 1);
            startDocument = url.indexOf("/", startDocument + 1) + 1;
            int endDocument = url.indexOf("/", startDocument);
            if (endDocument <= 0)
                endDocument = url.length();
            return (url.substring(startDocument, endDocument));
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
