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

package org.codehaus.oxyd.client;

import org.codehaus.oxyd.client.document.Document;
import org.codehaus.oxyd.client.document.Block;
import org.codehaus.oxyd.kernel.oxydException;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.util.List;
import java.util.ArrayList;
import java.io.StringReader;

public class Actions {
    private String serverUrl = "http://localhost:8080/oxyd/";

    public List listWorkspaces() throws oxydException {
        String url = serverUrl + "listworkspaces";
        String content = Utils.getURLContent(url);
        org.dom4j.Document xmlDoc = getXMLDocument(content);
        isError(xmlDoc);
        List workspaces = new ArrayList();
        Element el = xmlDoc.getRootElement();
        List els = el.element("workspaces").elements("workspace");
        for (int i = 0; i < els.size(); i++)
        {
            workspaces.add(((Element)els.get(i)).getText());
        }
        return workspaces;
    }

    public List listWorkspaceDocuments(String workspace) throws oxydException {
        String url = serverUrl + "listworkspacedocuments/" + workspace;
        String content = Utils.getURLContent(url);
        org.dom4j.Document xmlDoc = getXMLDocument(content);
        isError(xmlDoc);
        List docs = new ArrayList();
        Element el = xmlDoc.getRootElement();
        List els = el.element("documents").elements("document");
        for (int i = 0; i < els.size(); i++)
        {
            docs.add(((Element)els.get(i)).getText());
        }
        return docs;
    }

    public Document getDocument(String workspace, String docName) throws oxydException
    {
        String url = serverUrl + "getdocument/" + workspace + "/" + docName;
        String content = Utils.getURLContent(url);
        org.dom4j.Document xmlDoc = getXMLDocument(content);
        isError(xmlDoc);
        Document doc = new Document(xmlDoc);
        return doc;
    }

    public Document createDocument(String workspace, String docName) throws oxydException
    {
        String url = serverUrl + "createdocument/" + workspace + "/" + docName;
        String content = Utils.getURLContent(url);
        org.dom4j.Document xmlDoc = getXMLDocument(content);
        isError(xmlDoc);
        Document doc = new Document(xmlDoc);
        return doc;
    }

    public Block addBlock()
    {
        return null;
    }

    public void updateBlock()
    {

    }

    public void moveBlock()
    {

    }

    public void getUpdates(Document doc)
    {

    }

    public void lockBlock(Block block)
    {

    }

    public void unlockBlock(Block block)
    {

    }

    public boolean saveBlock(Document doc, Block block) throws oxydException {
        String url = serverUrl + "saveblock/" + doc.getWorkspace() + "/" + doc.getName();
        String content = Utils.getURLContent(url);
        org.dom4j.Document xmlDoc = getXMLDocument(content);
        isError(xmlDoc);
        return isOK(xmlDoc);
    }

    private org.dom4j.Document getXMLDocument(String xml) throws oxydException {
        SAXReader reader = new SAXReader();
        org.dom4j.Document domdoc;
        try {
        StringReader in = new StringReader(xml);
        domdoc = reader.read(in);
        }
        catch (DocumentException e){
            throw new oxydException(oxydException.MODULE_CLIENT_ACTION, oxydException.ERROR_XML_ERROR, "error in reading the xml");
        }
        return domdoc;
    }

    private void isError(org.dom4j.Document doc) throws oxydException {
        Element el = doc.getRootElement();
        if (el.getName().compareTo("error") == 0)
        {
            int module = new Long(el.attributeValue("module")).intValue();
            int errorCode = new Long(el.attributeValue("code")).intValue();
            String errorTxt = el.getText();
            throw new oxydException(module, errorCode, errorTxt);
        }
    }

    private boolean isOK(org.dom4j.Document doc) throws oxydException {
        Element el = doc.getRootElement();
        if (el.getText().compareTo("OK") == 0)
        {
            return true;
        }
        return false;
    }

    public Block createBlock(Document doc, String pos) {
        return null;
    }
}
