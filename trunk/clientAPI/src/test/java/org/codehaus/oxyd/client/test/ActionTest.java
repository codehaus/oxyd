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
import org.codehaus.oxyd.client.document.Block;
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
        List docs = actions.getWorkspaces();

        assertEquals(3, docs.size());
        assertTrue(inList("test", docs));
        assertTrue(inList("foo", docs));
        assertTrue(inList("Foo", docs));
    }

    public void testListWorkspaceDucuments() throws oxydException {
        actions.createDocument("Bar", "titi");
        actions.createDocument("Bar", "toto");
        actions.createDocument("Bar", "tutu");
        List docs = actions.getWorkspaceDocuments("Bar");

        assertEquals(3, docs.size());
        assertTrue(inList("titi", docs));
        assertTrue(inList("toto", docs));
        assertTrue(inList("tutu", docs));
    }

    public void testCreateBlock() throws oxydException {
        Document doc = actions.createDocument("plip", "titi");
        Block block = actions.addBlock(doc, "1", "aa".getBytes());
        Block block1 = actions.addBlock(doc, "2", "aa".getBytes());
        Block block2 = actions.addBlock(doc, "3", "aa".getBytes());

        Document doc1 = actions.getDocument(doc.getWorkspace(), doc.getName());

        assertEquals(3, doc1.getBlocks().size());
        assertTrue(doc1.getBlocks().containsKey(new Long(block.getId())));
        assertTrue(doc1.getBlocks().containsKey(new Long(block1.getId())));
        assertTrue(doc1.getBlocks().containsKey(new Long(block2.getId())));
    }

    public void testUpdateBlock() throws oxydException {
        Document doc = actions.createDocument("plup", "titi");
        Block block = actions.addBlock(doc, "1", "aa".getBytes());
        Block block1 = actions.addBlock(doc, "2", "aa".getBytes());
        Block block2 = actions.addBlock(doc, "3", "aa".getBytes());

        actions.lockBlock(block);
        block.setContent("is the futur?".getBytes());
        actions.updateBlock(block);

        actions.lockBlock(block1);
        block1.setContent("yes it is".getBytes());
        actions.updateBlock(block1);


        doc = actions.getDocument(doc.getWorkspace(), doc.getName());
        block = (Block) doc.getLockedBlocks().get(new Long(block.getId()));
        assertEquals("is the futur?", new String(block.getContent()));

        block1 = (Block) doc.getLockedBlocks().get(new Long(block1.getId()));
        assertEquals("yes it is", new String(block1.getContent()));
    }

    public void testLockBlock() throws oxydException {
        Document doc = actions.createDocument("plop", "titi");
        Block block = actions.addBlock(doc, "1", "aa".getBytes());
        actions.addBlock(doc, "1", "aa".getBytes());

        assertTrue(actions.lockBlock(block));
        doc = actions.getDocument(doc.getWorkspace(), doc.getName());
        assertEquals(1, doc.getLockedBlocks().size());

        assertTrue(actions.unlockBlock(block));
        doc = actions.getDocument(doc.getWorkspace(), doc.getName());
        assertEquals(0, doc.getLockedBlocks().size());
    }

    public void testGetUpdates() throws oxydException {
        Document doc = actions.createDocument("plopplip", "titi");
        Block block = actions.addBlock(doc, "1", "aa".getBytes());
        Block block2 = actions.addBlock(doc, "1", "aa".getBytes());
        actions.getUpdates(doc);
        assertEquals(2, doc.getVersion());
        assertEquals(0, doc.getLockedBlocks().size());
        assertEquals(2, doc.getBlocks().size());
        actions.lockBlock(block);
        actions.getUpdates(doc);
        assertEquals(1, doc.getLockedBlocks().size());
        assertEquals(1, doc.getBlocks().size());
    }

    private boolean inList(String obj, List list)
    {
        for (int i = 0; i < list.size(); i++)
            if (obj.compareTo((String)list.get(i)) == 0)
                return true;
        return false;
    }


}
