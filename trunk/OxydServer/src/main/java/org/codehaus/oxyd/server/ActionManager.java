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
package org.codehaus.oxyd.server;

import org.codehaus.oxyd.kernel.Actions;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.document.IDocument;

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

            if (action.compareTo("listworkspacedocuments") == 0)
            {
                List docs = actions.getWorkspaceDocumentsName(getWorkspaceName(req), serverContext.getKernelContext());
                render.listWorkspaceDocuments(docs, resp);
            }


            if (action.compareTo("getDocument") == 0)
            {
                IDocument doc = actions.getDocument(getWorkspaceName(req), getDocumentName(req), serverContext.getKernelContext());
                render.getDocument(doc, resp);
            }
        }
        catch (oxydException e)
        {
        }
        catch (IOException ioe)
        {
            
        }

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
