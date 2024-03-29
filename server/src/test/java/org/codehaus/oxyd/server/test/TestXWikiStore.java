/* ====================================================================
 *   Copyright 2005 J�r�mi Joslin.
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
import org.codehaus.oxyd.server.storage.XWikiStore;
import org.codehaus.oxyd.kernel.oxydException;

import java.util.Hashtable;

public class TestXWikiStore  extends TestCase {
    private XWikiStore store;

    public void setUp(){
        store = new XWikiStore();
    }

    public void testLogin() throws oxydException {
        String key = store.login();
        assertNotNull(key);
    }

    public void testgetWikiDocument() throws oxydException {
        String key = store.login();
        Hashtable page = store.getWikiDocument(key, "Main", "WebHome");
        assertEquals("", page.get("content"));
    }
}
