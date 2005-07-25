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

package org.codehaus.oxyd.server.storage;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.store.IStore;
import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.document.IBlock;
import org.codehaus.oxyd.kernel.document.DocumentImpl;

import java.util.*;
import java.io.IOException;

import sun.security.util.SignatureFile;

public class XWikiStore implements IStore {
    String baseURL  = "http://oxyddemo.xwiki.com/xwiki/bin/xmlrpc/confluence";
    String userName = "OxydDemo" ;
    String pwd = "demo";

    public String login() throws oxydException {
        try {
        XmlRpcClient xmlrpc = new XmlRpcClient(baseURL);
        Vector params = new Vector ();
        params.addElement (userName);
        params.addElement (pwd);

        String result = (String) xmlrpc.execute ("confluence1.login", params);
        return result;
        }
        catch(IOException e)
        {
            throw new oxydException (oxydException.MODULE_XWIKI_STORE, oxydException.ERROR_UNKNOWN, e.getMessage());
        } catch (XmlRpcException e) {
            throw new oxydException (oxydException.MODULE_XWIKI_STORE, oxydException.ERROR_UNKNOWN, e.getMessage());
        }
    }

    public IDocument openDocument(String space, String document, Context context) throws oxydException {
        String token = login();
        Hashtable page = getWikiDocument(token, space, document);
        String content = (String) page.get("content");

        IDocument doc = new DocumentImpl(space, document);
        String[] tab = content.split("\n\n");
        for (int i = 0; i < tab.length; i++)
            doc.createBlock("" + i + 1, tab[i].getBytes(), context);
        return doc;
    }

    public void saveDocument(IDocument doc, Context context) throws oxydException {
        String token = login();
        Hashtable page = getWikiDocument(token, doc.getWorkspace(), doc.getName());
        page.put("content", generatePage(doc, context));

        try {
            XmlRpcClient xmlrpc = new XmlRpcClient(baseURL);
            Vector params = new Vector ();
            params.addElement (token);
            params.addElement (page);

            xmlrpc.execute ("confluence1.storePage", params);
        }
        catch(IOException e)
        {
            throw new oxydException (oxydException.MODULE_XWIKI_STORE, oxydException.ERROR_UNKNOWN, e.getMessage());
        } catch (XmlRpcException e) {
            throw new oxydException (oxydException.MODULE_XWIKI_STORE, oxydException.ERROR_UNKNOWN, e.getMessage());
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

    private String generatePage(IDocument doc, Context context)
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

    public Hashtable getWikiDocument(String token, String space, String document) throws oxydException {
      try {
        XmlRpcClient xmlrpc = new XmlRpcClient(baseURL);
        Vector params = new Vector ();
        params.addElement (token);
        params.addElement (space + "." + document);

        Hashtable result =  (Hashtable) xmlrpc.execute ("confluence1.getPage", params);
        return result;
        }
        catch(IOException e)
        {
            throw new oxydException (oxydException.MODULE_XWIKI_STORE, oxydException.ERROR_UNKNOWN, e.getMessage());
        } catch (XmlRpcException e) {
            throw new oxydException (oxydException.MODULE_XWIKI_STORE, oxydException.ERROR_UNKNOWN, e.getMessage());
        }
    }
}