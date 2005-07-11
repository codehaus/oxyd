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

package org.codehaus.oxyd.client.test;

import org.codehaus.oxyd.client.Actions;
import org.codehaus.oxyd.client.document.Document;
import org.codehaus.oxyd.kernel.oxydException;
import junit.framework.TestCase;

import java.util.List;


public class ActionTest extends TestCase {
    private Actions actions;

    public void setUp() throws oxydException {
        actions = new Actions();
    }

    public void testCreateDocument() throws oxydException {
        Document doc1 = actions.createDocument("test", "toto");
        try {
            actions.createDocument("test", "toto");
            assertTrue(false);
        }
        catch (oxydException e)
        {
        }
        Document doc2 = actions.getDocument("test", "toto");
        assertEquals(doc1.getId(), doc2.getId());
    }

    public void testListWorkspace() throws oxydException {
        actions.createDocument("test", "titi");
        actions.createDocument("foo", "titi");
        actions.createDocument("Foo", "titi");
        List docs = actions.listWorkspaces();

        assertEquals(3, docs.size());
        assertTrue(inList("test", docs));
        assertTrue(inList("foo", docs));
        assertTrue(inList("Foo", docs));
    }

    public void testListWorkspaceDucuments() throws oxydException {
        actions.createDocument("Bar", "titi");
        actions.createDocument("Bar", "toto");
        actions.createDocument("Bar", "tutu");
        List docs = actions.listWorkspaceDocuments("Bar");

        assertEquals(3, docs.size());
        assertTrue(inList("titi", docs));
        assertTrue(inList("toto", docs));
        assertTrue(inList("tutu", docs));
    }

    public void testCreateBlock() throws oxydException {
        Document doc = actions.createDocument("plip", "titi");
        List docs = actions.createBlock(doc, "1");

        assertEquals(3, docs.size());
        assertTrue(inList("test", docs));
        assertTrue(inList("foo", docs));
        assertTrue(inList("Foo", docs));
    }

    public void testLockBlock() throws oxydException {
        actions.createDocument("plop", "titi");
        actions.lockBlock();

        assertEquals(3, docs.size());
        assertTrue(inList("test", docs));
        assertTrue(inList("foo", docs));
        assertTrue(inList("Foo", docs));
    }

    private boolean inList(String obj, List list)
    {
        for (int i = 0; i < list.size(); i++)
            if (obj.compareTo((String)list.get(i)) == 0)
                return true;
        return false;
    }
}
