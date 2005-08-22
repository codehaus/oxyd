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

import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.document.IBlock;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.auth.User;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import org.dom4j.io.OutputFormat;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class render {


    static void listWorkspaces(List workspaces, HttpServletResponse response) throws IOException {
        Document doc = new DOMDocument();
        Element respel = new DOMElement("response");
        doc.setRootElement(respel);

        Element workspaceEl = new DOMElement("workspaces");
        respel.add(workspaceEl);

        for (int i = 0; i < workspaces.size(); i++)
        {
            Element el = new DOMElement("workspace");
            el.addText((String) workspaces.get(i));
            workspaceEl.add(el);
        }
        sendResponse(doc, response);
    }

    static public void sendResponse(Document doc, HttpServletResponse response) throws IOException {
        OutputFormat outputFormat = new OutputFormat("", true);
        outputFormat.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter( response.getOutputStream(), outputFormat );
        writer.write(doc);

    }

    public static void listWorkspaceDocuments(List docs, HttpServletResponse response) throws IOException {
        Document doc = new DOMDocument();
        Element respel = new DOMElement("response");
        doc.setRootElement(respel);

        Element docsEl = new DOMElement("documents");
        respel.add(docsEl);

        for (int i = 0; i < docs.size(); i++)
        {
            Element el = new DOMElement("document");
            el.addText((String) docs.get(i));
            docsEl.add(el);
        }
        sendResponse(doc, response);

    }

    public static void returnDocument(IDocument document, HttpServletResponse response) throws IOException {
        Document returndoc = new DOMDocument();
        Element respel = new DOMElement("response");
        returndoc.setRootElement(respel);

        Document doc = document.toXMLDocument();
        //Element docEl = new DOMElement("document");
        respel.add(doc.getRootElement());

        sendResponse(returndoc, response);
    }

    public static void returnBlock(IBlock block, HttpServletResponse response) throws IOException {
        Document returndoc = new DOMDocument();
        Element respel = new DOMElement("response");
        returndoc.setRootElement(respel);

        Element blockEl = block.toXML();
        //Element docEl = new DOMElement("document");
        respel.add(blockEl);

        sendResponse(returndoc, response);
    }

    public static void returnOk(HttpServletResponse response) throws IOException {
        Document returnOk = new DOMDocument();
        Element respel = new DOMElement("response");
        returnOk.setRootElement(respel);
        respel.addText("OK");
        sendResponse(returnOk, response);
    }

    public static void returnKey(String key, HttpServletResponse response) throws IOException {
        Document returndoc = new DOMDocument();
        Element respel = new DOMElement("response");
        returndoc.setRootElement(respel);
        Element keyEl = new DOMElement("key");
        respel.add(keyEl);
        keyEl.addText(key);
        sendResponse(returndoc, response);
    }

    public static void ReturnError(oxydException e, HttpServletResponse response) throws IOException {
        Document returndoc = new DOMDocument();
        Element respel = new DOMElement("error");
        returndoc.setRootElement(respel);
        respel.addText(e.getMessage());
        respel.addAttribute("module", ""+e.getModule());
        respel.addAttribute("code", ""+e.getCode());
        sendResponse(returndoc, response);
    }

    public static void ReturnError(Exception e, HttpServletResponse response) throws IOException {
        Document returndoc = new DOMDocument();
        Element respel = new DOMElement("error");
        returndoc.setRootElement(respel);
        if (e.getMessage() != null)
            respel.addText(e.getMessage());
        else
            respel.addText("unexpected error");
        respel.addAttribute("module", "-1");
        respel.addAttribute("code", "-1");
        sendResponse(returndoc, response);
    }

    public static void returnUpdates(List updates, long version, List users, HttpServletResponse response) throws IOException {
        Document doc = new DOMDocument();
        Element respel = new DOMElement("response");
        doc.setRootElement(respel);

        Element usersEl = new DOMElement("users");
        respel.add(usersEl);
        if (users != null)
        {
            for (int i = 0; i < users.size(); i++)
            {
                Element el = new DOMElement("user");
                el.addText(((User)users.get(i)).getLogin());
                usersEl.add(el);
            }
        }

        Element docsEl = new DOMElement("blocks");
        respel.add(docsEl);
        docsEl.addAttribute("version", ""+version);
        if (updates != null)
            for (int i = 0; i < updates.size(); i++)
            {
                Element el = ((IBlock)updates.get(i)).toXML();
                docsEl.add(el);
            }
        sendResponse(doc, response);
    }
}
