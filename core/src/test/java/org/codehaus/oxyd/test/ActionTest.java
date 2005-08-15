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

import org.codehaus.oxyd.kernel.Actions;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Workspace;
import org.codehaus.oxyd.kernel.document.IDocument;

import java.util.List;

import junit.framework.TestCase;

public class ActionTest extends TestCase {
    Actions     action;
    Context     context;

    public void setUp() throws Exception {
        action = new Actions(null);
        setUp(action);
    }

    public void setUp(Actions myAction) throws oxydException {
        context = Utils.initContext(new Actions(null));
        myAction.createWorkspace("firstSpace", context);
        myAction.createWorkspace("secondSpace", context);
        myAction.createDocument("firstSpace", "doc1", context);
        myAction.createDocument("secondSpace", "doc2", context);
        myAction.createDocument("firstSpace", "doc3", context);
        myAction.createDocument("secondSpace", "doc4", context);
    }



    public void testListWorkspace(){
        List spaces = action.getWorkspacesNames(context);
        assertEquals(2, spaces.size());
        if ((spaces.get(0) != "firstSpace") && (spaces.get(0) != "secondSpace"))
            assertTrue(false);
        if ((spaces.get(1) != "firstSpace") && (spaces.get(1) != "secondSpace"))
            assertTrue(false);
    }

    public void testListDocument(){
        List docs = action.getWorkspaceDocumentsName("firstSpace", context);
        assertEquals(2, docs.size());
        String docName = (String)docs.get(0);
        if ((docName.compareTo("doc1") != 0) && (docName.compareTo("doc3") != 0))
            assertTrue(false);
        docName = (String)docs.get(1);
        if ((docName.compareTo("doc1") != 0) && (docName.compareTo("doc3") != 0))
            assertTrue(false);

        docs = action.getWorkspaceDocumentsName("secondSpace", context);
        assertEquals(2, docs.size());
        if ((((String)docs.get(0)).compareTo("doc2") != 0) && (((String)docs.get(0)).compareTo("doc4") != 0))
            assertTrue(false);
        if ((((String)docs.get(0)).compareTo("doc2") != 0) && (((String)docs.get(0)).compareTo("doc4") != 0))
            assertTrue(false);
    }

    public void testGetDocument() throws oxydException {
        IDocument doc = action.getDocument("firstSpace", "doc1", context);
        assertNotNull(doc);
        assertEquals("firstSpace", doc.getWorkspace());
        assertEquals("doc1", doc.getName());

        try{
            doc = action.getDocument("firstSpace", "doc42", context);
//            assertTrue(false);
        }
        catch(oxydException e)
        {}

        try{
            doc = action.getDocument("Space23", "doc1", context);
            //assertTrue(false);
        }
        catch(oxydException e)
        {}


    }

    public void testCreateWorkspace() throws oxydException {
        Actions myActions = new Actions();
        setUp(myActions);
        assertFalse(myActions.isWorkspaceExist("titi", context));
        myActions.createWorkspace("titi", context);
        assertTrue(myActions.isWorkspaceExist("titi", context));
        try{
            myActions.createWorkspace("titi", context);
            assertTrue(false);
        }
        catch(oxydException e){
        }
    }



/*
public void testCreateWorkspaceWithAccents() throws oxydException {
  Actions myActions = new Actions();
  setUp(myActions);

  Workspace space;
  space =  myActions.createWorkspace("té & ti-", context);
  assertTrue("the name should be changed", "te  ti".compareTo(space.getName()) == 0);

  try{
      myActions.createWorkspace("té & \"ti-^", context);
      assertTrue(false);
  }
  catch(oxydException e){
  }

  space = myActions.createWorkspace("téàtiç", context);
  assertEquals("téàtiç", space.getName());
}
*/
}
