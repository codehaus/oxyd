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

import org.codehaus.oxyd.kernel.document.DocumentTextImpl;
import org.codehaus.oxyd.kernel.document.IBlock;
import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.oxydException;

import junit.framework.TestCase;

import java.util.List;



/**
 *
 * TODO : test if we unlock, and not saving before, we must return to the previous saved value
 */
public class DocumentTextImplTest extends TestCase {

    public void testDocumentTextPositionImpl()
    {
        Context context = Utils.createContext();


        IDocument doc = new DocumentTextImpl("test");

        doc.setId(42);
        doc.setName("test");
        IBlock bloc1 = doc.createBlock("1", "That's the futur".getBytes(), context);
        IBlock bloc2 = doc.createBlock("2", "the second block".getBytes(), context);
        IBlock bloc3 = doc.createBlock("1", "the third block".getBytes(), context);

        assertEquals("1", bloc3.getPosition().toString());
        assertEquals("2", bloc1.getPosition().toString());
        assertEquals("3", bloc2.getPosition().toString());
    }

    public void testUnlockRollback()
    {
        Context context = Utils.createContext();
        IDocument doc = new DocumentTextImpl("test");
        doc.setId(42);
        doc.setName("test");
        IBlock bloc1 = doc.createBlock("1", "That's the futur".getBytes(), context);
        try {
            doc.lockBlock(bloc1.getId(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }

        IBlock b1 = doc.getBlock(bloc1.getId(), context);
        assertEquals("the version must have change", 2, b1.getVersion());

        try {
            doc.updateBlock(bloc1.getId(), "test update of this block1".getBytes(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }


        b1 = doc.getBlock(bloc1.getId(), context);
        assertEquals("test update of this block1", new String(b1.getContent()));
        assertEquals("the version must have change", 3, b1.getVersion());
        try {
            doc.unlockBlock(bloc1.getId(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }

        b1 = doc.getBlock(bloc1.getId(), context);
        assertEquals("the version must have change", 4, b1.getVersion());
        assertEquals("That's the futur", new String(b1.getContent()));


    }

    public void testDocumentTextUpdateBlocImpl() throws oxydException {
        Context context = Utils.createContext();


        IDocument doc = new DocumentTextImpl("test");

        doc.setId(42);
        doc.setName("test");
        IBlock bloc1 = doc.createBlock("1", "That's the futur".getBytes(), context);
        IBlock bloc2 = doc.createBlock("2", "the second block".getBytes(), context);
        IBlock bloc3 = doc.createBlock("3", "the third block".getBytes(), context);

        assertEquals("That's the futur", new String(bloc1.getContent()));
        assertEquals("the second block", new String(bloc2.getContent()));
        assertEquals("the third block", new String(bloc3.getContent()));

        doc.lockBlock(bloc2.getId(), context);
        assertTrue(doc.isBlockLocked(bloc2.getId()));
        try {
            doc.updateBlock(bloc2.getId(), "test update of this block2".getBytes(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }

        try {
            doc.updateBlock(bloc3.getId(), "test update of this block3".getBytes(), context);
            assertTrue(false);
        } catch (oxydException e) {

        }

        IBlock b1 = doc.getBlock(bloc1.getId(), context);
        IBlock b2 = doc.getBlock(bloc2.getId(), context);
        IBlock b3 = doc.getBlock(bloc3.getId(), context);

        assertEquals("That's the futur", new String(b1.getContent()));
        assertEquals("test update of this block2", new String(b2.getContent()));
        assertEquals("the third block", new String(b3.getContent()));

        assertEquals("the version must be the inital", 1, b1.getVersion());
        assertEquals("the version must have changed", 5, b2.getVersion());
        assertEquals("the version must be the inital", 3, b3.getVersion());
        assertEquals("the version must have changed", 5, doc.getVersion());

        doc.saveBlock(bloc2.getId(), context);
        b2 = doc.getBlock(bloc2.getId(), context);
        assertEquals("the version must have changed", 5, b2.getVersion());
        assertEquals("the version must have changed", 5, doc.getVersion());



        doc.unlockBlock(bloc2.getId(), context);
        doc.lockBlock(bloc2.getId(), context);
        try {
            doc.updateBlock(bloc2.getId(), "test update of this block2 blop".getBytes(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }
        b2 = doc.getBlock(bloc2.getId(), context);
        assertEquals("the version must have changed", 8, b2.getVersion());

        doc.lockBlock(bloc3.getId(), context);
        try {
            doc.updateBlock(bloc3.getId(), "test update of this block3 blop".getBytes(), context);

        } catch (oxydException e) {
            assertTrue(false);
        }
        b3 = doc.getBlock(bloc3.getId(), context);
        assertEquals(10, b3.getVersion());

        doc.saveBlock(bloc3.getId(), context);
        doc.saveBlock(bloc2.getId(), context);

        b2 = doc.getBlock(bloc2.getId(), context);
        b3 = doc.getBlock(bloc3.getId(), context);
        assertEquals(10, b3.getVersion());
        assertEquals(8, b2.getVersion());
        assertEquals(10, doc.getVersion());

    }

    public void testDocumentTextGetBlockHistoryImpl() throws oxydException {
        Context context = Utils.createContext();


        IDocument doc = new DocumentTextImpl("test");

        doc.setId(42);
        doc.setName("test");
        IBlock bloc = doc.createBlock("1", "sailefutur".getBytes(), context);
        assertEquals("the version must be the inital", 1, bloc.getVersion());
        doc.lockBlock(bloc.getId(), context);
        try {
            doc.lockBlock(bloc.getId(), context);
            assertFalse(true);
        }
        catch(oxydException e)
        {}
        IBlock b1  = doc.getBlock(bloc.getId(), context);
        assertEquals("the version must be the inital", 2, b1.getVersion());
        try {
            doc.updateBlock(bloc.getId(), "test".getBytes(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }
        b1  = doc.getBlock(bloc.getId(), context);
        assertEquals("the version must have changed", 3, b1.getVersion());
        try {
            doc.updateBlock(bloc.getId(), "test update".getBytes(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }
        b1  = doc.getBlock(bloc.getId(), context);
        assertEquals("the version must have changed", 4, b1.getVersion());
        doc.saveBlock(bloc.getId(), context);
        b1  = doc.getBlock(bloc.getId(), context);
        assertEquals("the version must have not changed", 4, b1.getVersion());
        try {
            doc.updateBlock(bloc.getId(), "test update of".getBytes(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }
        b1  = doc.getBlock(bloc.getId(), context);
        assertEquals("the version must have changed", 5, b1.getVersion());
        try {
            doc.updateBlock(bloc.getId(), "test update of this".getBytes(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }
        doc.saveBlock(bloc.getId(), context);
        b1  = doc.getBlock(bloc.getId(), context);
        assertEquals("the version must have changed", 6, b1.getVersion());
        try {
            doc.updateBlock(bloc.getId(), "test update of this block".getBytes(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }
        doc.saveBlock(bloc.getId(), context);
        try {
            doc.unlockBlock(bloc.getId(), context);
        } catch (oxydException e) {
            assertTrue(false);
        }
        b1  = doc.getBlock(bloc.getId(), context);
        assertEquals("the version must have changed", 8, b1.getVersion());
    }

    public void testGetUpdates() throws oxydException {
        Context context = Utils.createContext();
        IDocument doc = new DocumentTextImpl("test");
        long sinceVersion = 0;
        doc.setId(42);
        doc.setName("test");

        IBlock bloc1 = doc.createBlock("1", "That's the futur".getBytes(), context);
        IBlock bloc2 = doc.createBlock("2", "the second block".getBytes(), context);
        IBlock bloc3 = doc.createBlock("3", "the third block".getBytes(), context);

        List update = doc.getUpdates(sinceVersion, context);
        sinceVersion = doc.getVersion();
        assertEquals(3, update.size());
        IBlock b1 = (IBlock) update.get(0);
        IBlock b2 = (IBlock) update.get(1);
        IBlock b3 = (IBlock) update.get(2);

        assertTrue(inList(bloc3, update));
        assertTrue(inList(bloc2, update));
        assertTrue(inList(bloc1, update));



        doc.lockBlock(bloc2.getId(), context);
        update = doc.getUpdates(sinceVersion, context);
        sinceVersion = doc.getVersion();
        assertEquals(1, update.size());
        b2 = (IBlock) update.get(0);
        assertTrue(b2.isLocked());

        doc.updateBlock(b2.getId(), "Firt update".getBytes(), context);
        update = doc.getUpdates(sinceVersion, context);
        sinceVersion = doc.getVersion();
        assertEquals(1, update.size());
        b2 = (IBlock) update.get(0);

        doc.unlockBlock(bloc2.getId(), context);
        update = doc.getUpdates(sinceVersion, context);
        sinceVersion = doc.getVersion();
        assertEquals(1, update.size());
        b2 = (IBlock) update.get(0);
        assertEquals(b2.getId(), bloc2.getId());
        assertFalse(b2.isLocked());


        IDocument doc2 = new DocumentTextImpl("test2");
        IBlock doc2b1 = doc2.createBlock("1", "blip".getBytes(), context);
        IBlock doc2b2 = doc2.createBlock("2", "blop".getBytes(), context);



        doc.lockBlock(bloc1.getId(), context);
        doc.lockBlock(bloc3.getId(), context);

        doc2.lockBlock(doc2b1.getId(), context);
        doc2.lockBlock(doc2b2.getId(), context);


        update = doc.getUpdates(sinceVersion, context);
        sinceVersion = doc.getVersion();
        assertEquals(2, update.size());

        assertTrue(inList(bloc1, update));
        assertTrue(inList(bloc3, update));
    }

    public void testXML() throws oxydException {
        Context context = Utils.createContext();
        IDocument doc1 = new DocumentTextImpl("test", "titi");
        doc1.setParentName("parentName");
        doc1.setDirectory("/your/path");
        doc1.createBlock("1", "toto a la plage".getBytes(), context);
        doc1.createBlock("2", "toto en australie".getBytes(), context);
        doc1.createBlock("3", "toto en chine".getBytes(), context);

        String xml = doc1.toXML();
//        assertEquals("", xml);

        IDocument doc2 = new DocumentTextImpl();
        doc2.fromXML(xml);

        assertEquals(doc1.getName(), doc2.getName());
        assertEquals(doc1.getWorkspace(), doc2.getWorkspace());
        assertEquals(doc1.getParentName(), doc2.getParentName());
        assertEquals(doc1.getBlocks().size(), doc2.getBlocks().size());
        assertEquals(doc1.getDirectory(), doc2.getDirectory());
    }

    private boolean inList(IBlock obj, List list)
    {
        for (int i = 0; i < list.size(); i++)
            if (obj.getId() == ((IBlock)list.get(i)).getId())
                return true;
        return false;
    }
}


