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

import org.codehaus.oxyd.server.ServerContext;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.auth.User;
import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.document.IBlock;
import org.codehaus.oxyd.kernel.document.DocumentImpl;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.util.*;
import java.io.IOException;

public class XWikiPlugin extends OxydPlugin {
    String url = "oxyddemo.xwiki.com";
    String baseURL  = "/xwiki/bin/xmlrpc/confluence";
   // String userName = "OxydDemo" ;
   // String pwd = "demo";

    public String getName() {
        return "xwiki";
    }

    public Document execute(String command, ServerContext context) throws oxydException {
        String action = getAction(command);
        if (action.compareTo("save") ==  0)
            return saveDocument(command, context);
        else if (action.compareTo("load") ==  0)
            return loadDocument(command, context);
        return null;
    }

    public String login(String url, String login, String pwd, ServerContext context) throws oxydException {
        try {
            XmlRpcClient xmlrpc = new XmlRpcClient(getWikiUrl(context) + baseURL);
            Vector params = new Vector ();
            params.addElement (login);
            params.addElement (pwd);

            Object res = xmlrpc.execute ("confluence1.login", params);
            if (res instanceof String)
                return (String) res;
            throw new oxydException(oxydException.MODULE_PLUGIN, oxydException.ERROR_INVALID_USERNAME_OR_PASSWORD, "invalid Username or password");

        }
        catch(IOException e)
        {
            throw new oxydException (oxydException.MODULE_PLUGIN, oxydException.ERROR_UNKNOWN, e.getMessage());
        } catch (XmlRpcException e) {
            throw new oxydException (oxydException.MODULE_PLUGIN, oxydException.ERROR_UNKNOWN, e.getMessage());
        }
    }

    private Document saveDocument(String command, ServerContext context) throws oxydException {
        String space = getWorkspace(command);
        saveDocument(context.getActionManager().getDocument(space, getDocumentName(command), context.getKernelContext()), context);
        Document returnOk = new DOMDocument();
        Element respel = new DOMElement("response");
        returnOk.setRootElement(respel);
        respel.addText("OK");
        return returnOk;
    }

    private Document loadDocument(String command, ServerContext context) throws oxydException {
        IDocument doc = context.getActionManager().createDocument(getWorkspace(command), getDocumentName(command), context.getKernelContext());
        loadDocument(doc, context);
        return null;
    }

    public IDocument loadDocument(IDocument doc, ServerContext context) throws oxydException {
        String token = (String) context.getKernelContext().getUser().get("wikiToken");
        Hashtable page = getWikiDocument(token, doc.getWorkspace(), doc.getName(), context);
        String content = (String) page.get("content");

        String[] tab = content.split("\n\n");
        for (int i = 0; i < tab.length; i++)
            doc.createBlock((new Integer(i + 1)).toString(), tab[i].getBytes(), context.getKernelContext());
        return doc;
    }

    public void saveDocument(IDocument doc, ServerContext context) throws oxydException {
        String token = (String) context.getKernelContext().getUser().get("wikiToken");
        String space = doc.getWorkspace();
        space = space.substring(space.indexOf(":") + 1);
        Hashtable page = getWikiDocument(token, space, doc.getName(), context);
        page.put("content", generatePage(doc, context.getKernelContext()));

        try {
            XmlRpcClient xmlrpc = new XmlRpcClient(getWikiUrl(context.getKernelContext().getUser()) + baseURL);
            Vector params = new Vector ();
            params.addElement (token);
            params.addElement (page);

            xmlrpc.execute ("confluence1.storePage", params);
        }
        catch(IOException e)
        {
            throw new oxydException (oxydException.MODULE_PLUGIN, oxydException.ERROR_UNKNOWN, e.getMessage());
        } catch (XmlRpcException e) {
            throw new oxydException (oxydException.MODULE_PLUGIN, oxydException.ERROR_UNKNOWN, e.getMessage());
        }
    }

    public static class BlocksPositionComparator implements Comparator {

          public int compare(Object element1, Object element2) {
            IBlock entry1 = (IBlock) element1;
            IBlock entry2 = (IBlock) element2;

            if ((entry1.getPosition() == null) &&  (entry2.getPosition() == null))
                return 0;
            if (entry1.getPosition() == null)
                return 1;
            if (entry2.getPosition() == null)
                return -1;
            return ((new Long(entry1.getPosition()).compareTo(new Long(entry2.getPosition()))));
        }
    }

    private String generatePage(IDocument doc, Context serverContext)
    {
        String content = "";

        BlocksPositionComparator comp = new BlocksPositionComparator();

        List blocks = new ArrayList();
        Iterator it = doc.getBlocks().values().iterator();
        while (it.hasNext())
        {
            IBlock block = (IBlock) it.next();
            if (!block.isRemoved())
                blocks.add(block);
        }
        Collections.sort(blocks, comp);

        it = blocks.iterator();
        while (it.hasNext())
        {
            IBlock block = (IBlock) it.next();
            content = content + new String(block.getContent()) + "\n\n";
        }
        return content;
    }

    public Hashtable getWikiDocument(String token, String space, String document, ServerContext serverContext) throws oxydException {
      try {
            XmlRpcClient xmlrpc = new XmlRpcClient(getWikiUrl(serverContext.getKernelContext().getUser()) + baseURL);
            Vector params = new Vector ();
            params.addElement (token);
            space = space.substring(space.indexOf(":") + 1);
            params.addElement (space + "." + document);

            Hashtable result =  (Hashtable) xmlrpc.execute ("confluence1.getPage", params);
            return result;
        }
        catch(IOException e)
        {
            throw new oxydException (oxydException.MODULE_PLUGIN, oxydException.ERROR_UNKNOWN, e.getMessage());
        } catch (XmlRpcException e) {
            throw new oxydException (oxydException.MODULE_PLUGIN, oxydException.ERROR_UNKNOWN, e.getMessage());
        }
    }

    public String getWikiUrl(ServerContext context)
    {
        String url = context.getRequest().getParameter("wikiServer");
        if (url != null && url.indexOf("http://") < 0)
            url = "http://" + url;
        return url;
    }

    public String getWikiUrl(User user)
    {
        String url = (String) user.get("wikiServer");
        return url;
    }

    public String getWikiLogin(ServerContext context)
    {
        String login = context.getRequest().getParameter("wikiLogin");
        return login;
    }

    public String getWikiPwd(ServerContext context)
    {
        String pwd = context.getRequest().getParameter("wikiPwd");
        return pwd;
    }

    public String beforeLogin(String login, String pwd, ServerContext context) throws oxydException {
        String url = getWikiUrl(context);
        if (url != null)
        {
            String token = login(url, login, pwd, context);
            User user = context.getAuthService().getUser(login, context);
            if (user == null){
                user = new User(login);
                context.getAuthService().addLoggedIn(token, user);
            }
            user.set("wikiToken", token);
            user.set("wikiServer", getWikiUrl(context));
            context.getKernelContext().setUser(user);
            return token;
        }
        else
            return null;
    }

    public IDocument afterOpenningDocument(String space, String document, IDocument doc, ServerContext serverContext) throws oxydException {
        if (doc != null)
            return doc;
        String wikiUrl = getWikiUrl(serverContext.getKernelContext().getUser());
        if (wikiUrl == null)
            return null;
        try {
            doc = serverContext.getActionManager().getDocument(wikiUrl.substring(7) + ":" + space, document, serverContext.getKernelContext());
        }
        catch (oxydException e)
        {}
        if (doc == null)
        {
            doc = serverContext.getActionManager().createDocument(wikiUrl.substring(7) + ":" + space, document, serverContext.getKernelContext());
            loadDocument(doc, serverContext);
        }
        return doc;
    }
}
