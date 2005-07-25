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
package org.codehaus.oxyd.kernel.auth;

import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Context;

import java.util.Map;
import java.util.HashMap;

public class AuthService implements IAuthService {

    private Map logins;
    private Map loggedIn;

    public AuthService()
    {
        loggedIn = new HashMap();
        logins = new HashMap();
        logins.put("validLogin", "ValidPwd");
        logins.put("toto", "titi");
    }

    public String login(String login, String pwd, Context context) throws oxydException {
        String validPwd = (String) logins.get(login);
        if (validPwd == null)
            throw new oxydException(oxydException.MODULE_AUTH_SERVICE, oxydException.ERROR_INVALID_USERNAME_OR_PASSWORD, "invalid Username or password");
        if (validPwd.compareTo(pwd) != 0)
            throw new oxydException(oxydException.MODULE_AUTH_SERVICE, oxydException.ERROR_INVALID_USERNAME_OR_PASSWORD, "invalid Username or password");
        context.setUser(new User(login));
        loggedIn.put(new Integer(context.getUser().getLogin().hashCode()).toString(), context.getUser());
        return new Integer(context.getUser().getLogin().hashCode()).toString();
    }

    public void login(String key, Context context) throws oxydException {
        if (!loggedIn.containsKey(key))
            throw new oxydException(oxydException.MODULE_AUTH_SERVICE, oxydException.ERROR_INVALID_KEY, "invalid key");
        context.setUser((User) loggedIn.get(key));
    }

    public void logout(String key, Context context) throws oxydException {
        if (!loggedIn.containsKey(key))
            throw new oxydException(oxydException.MODULE_AUTH_SERVICE, oxydException.ERROR_INVALID_KEY, "invalid key");
        loggedIn.remove(key);
    }
}
