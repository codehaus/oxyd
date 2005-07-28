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
package org.codehaus.oxyd.kernel;


import org.codehaus.oxyd.kernel.auth.User;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Context {
    private User        user;
    private Actions     actions;
    private List        parameters;
    private Map         params;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Actions getAction() {
        return actions;
    }

    public void setAction(Actions actions) {
        this.actions = actions;
    }

    public List getParameters() {
        return parameters;
    }

    public void setParameters(List parameters) {
        this.parameters = parameters;
    }

    public String toString() {
        return "Context{" +
                "user=" + user +
                ", parameters=" + parameters +
                "}";
    }

    public Object get(String name)
    {
        if (params != null)
        {
            return params.get(name);
        }
        return null;
    }

    public void set(String name, Object value)
    {
        if (params == null)
            params = new HashMap();
        params.put(name, value);
    }
}
