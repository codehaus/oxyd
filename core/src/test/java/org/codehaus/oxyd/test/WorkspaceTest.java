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
package org.codehaus.oxyd.test;

import org.codehaus.oxyd.kernel.Workspace;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.Actions;
import org.codehaus.oxyd.kernel.document.IDocument;
import junit.framework.TestCase;

public class WorkspaceTest  extends TestCase{
    private Context     context;
    private Workspace   space;


    public void setUp() throws Exception {
        context = Utils.initContext(new Actions(null));
        space = new Workspace("test", null);
        space.createDocument("doc1", context);
        space.createDocument("doc2", context);
        space.createDocument("doc3", context);
        space.createDocument("doc4", context);
    }

    public void testGetDocument() throws oxydException {
        IDocument doc = space.getDocument("doc1", context);
        assertNotNull(doc);
        assertEquals("doc1", doc.getName());

        try{
            doc = space.getDocument("doc42", context);
            assertTrue(false);
        }
        catch(oxydException e)
        {}
    }

    public void testCreateDocument() throws oxydException {
        IDocument doc = space.createDocument("doc42", context);
        assertNotNull(doc);
        assertEquals("doc42", doc.getName());

        try{
            doc = space.createDocument("doc42", context);
            assertTrue(false);
        }
        catch(oxydException e)
        {}
    }
      /*
    public void testCreateDocumentwithAccents() throws oxydException {

        //assertTrue("pb with accent but no exception", false);
        IDocument doc = space.createDocument("docaccentsé&é\"'('", context);
        assertNotNull(doc);
        assertEquals("docaccents", doc.getName());

    }
      */

}
