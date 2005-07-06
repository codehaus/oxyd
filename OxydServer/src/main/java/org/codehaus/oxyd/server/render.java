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

    static private void sendResponse(Document doc, HttpServletResponse response) throws IOException {
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

    public static void getDocument(IDocument document, HttpServletResponse response) throws IOException {
        Document doc = new DOMDocument();
        Element respel = new DOMElement("response");
        doc.setRootElement(respel);

        Element docEl = new DOMElement("document");
        respel.add(docEl);

        Element el = new DOMElement("name");
        el.addText(document.getName());
        docEl.add(el);

        sendResponse(doc, response);
    }
}
