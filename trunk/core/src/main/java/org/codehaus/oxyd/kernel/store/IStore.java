package org.codehaus.oxyd.kernel.store;

import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.document.IDocument;
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

public interface IStore {

    void saveDocument(IDocument doc, Context context) throws oxydException;

    IDocument openDocument(String space, String document, Context context) throws oxydException;

    public void deleteDocument(IDocument doc, Context context) throws oxydException;
}
