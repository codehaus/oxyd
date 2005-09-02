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

package org.codehaus.oxyd.server.test;

import junit.framework.TestCase;
import org.codehaus.oxyd.server.storage.HibernateStore;
import org.codehaus.oxyd.kernel.document.IDocument;
import org.codehaus.oxyd.kernel.document.DocumentImpl;
import org.codehaus.oxyd.kernel.document.IBlock;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.Actions;
import org.codehaus.oxyd.kernel.oxydException;

public class HibernateStoreTest   extends TestCase  {
   HibernateStore  store;
    Context         context;
    public void setUp() throws oxydException {
        store = new HibernateStore();
        context = Utils.initContext(new Actions( null));
    }

    public void testSaveDocument() throws oxydException {
        IDocument doc = new DocumentImpl("test", "toto");

        IBlock bloc1 = doc.createBlock("1", "That's the futur".getBytes(), context);
        IBlock bloc2 = doc.createBlock("2", "the second block".getBytes(), context);
        IBlock bloc3 = doc.createBlock("1", "the third block".getBytes(), context);

        store.saveDocument(doc, context);


        doc = store.openDocument("test", "toto", context);
        assertNotNull(doc);
    }
}
