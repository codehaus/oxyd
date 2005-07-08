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
package org.codehaus.oxyd.kernel.document;

import java.util.Map;
import java.util.HashMap;

public class HistoryImpl implements IHistory {
    private Map history;

    public HistoryImpl()
    {
        history = new HashMap();
    }

    public void addHistoryEvent(long version, IHistoryEvent evt)
    {
        history.put(new Long(version), evt);
    }

    /**
     * 
     * @param version
     * @return
     */
    public IDocument getHistoryEvent(long version)
    {
        for (long i = version; i > 0; i--)
        {
            Long key = new Long(i);
             if (history.containsKey(key))
             {
                String Xml = (String) history.get(key);
                throw new Error("Not implemented");
             }
        }
        return null;
    }

}
