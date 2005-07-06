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
import java.util.List;
import java.util.HashMap;

public class HistoryEventImpl implements IHistoryEvent {
    private String      command;
    private List        params;
    private String      Xml;
    private long        version;

    public HistoryEventImpl(String command, List params)
    {
        setCommand(command);
        setParams(params);
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List getParams() {
        return params;
    }

    public void setParams(List params) {
        this.params = params;
    }

    public String getVersion() {
        return Xml;
    }

    public void setVersion(String Xml) {
        this.Xml = Xml;
    }

    public void setVersionNumber(long version) {
        this.version = version;
    }

    public long getVersionNumber(){
        return version;
    }

}
