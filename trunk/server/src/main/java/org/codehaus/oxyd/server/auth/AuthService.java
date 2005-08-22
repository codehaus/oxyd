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
import org.codehaus.oxyd.kernel.utils.Utils;
import org.codehaus.oxyd.server.ServerContext;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.*;

public class AuthService implements IAuthService {

    private Map logins;
    private Map loggedIn;

    public AuthService()
    {
    }

    public void addUser(String login, String pwd)
    {
        if (logins == null)
            logins = new HashMap();
        logins.put(login, pwd);
    }

    public void removeUser(String login)
    {
        if (logins == null)
            return;
        if (logins.containsKey(login))
            logins.remove(login);
    }

    public void fromXML(Document xmlDoc)
    {
        Element el = xmlDoc.getRootElement();

        List ListFile =  el.elements("user");
        for (int i = 0; i < ListFile.size(); i++)
        {
            Element userEl = ((Element)ListFile.get(i));
            String login = Utils.getElementText(userEl, "login");
            String pwd = Utils.getElementText(userEl, "pwd");
            addUser(login, pwd);
        }
    }

    public void addLoggedIn(String key, User user)
    {
        if (loggedIn == null)
            loggedIn = new HashMap();
        loggedIn.put(key, user);
    }

    public User     getUser(String login, ServerContext serverContext)
    {
        if (loggedIn == null)
            return null;
        Iterator it = loggedIn.values().iterator();
        while (it.hasNext())
        {
            User user = (User) it.next();
            if (user.getLogin() == login)
                return user;
        }
        return null;
    }

    public String login(String login, String pwd, ServerContext serverContext) throws oxydException {
        String validPwd = (String) logins.get(login);
        if (validPwd == null)
            throw new oxydException(oxydException.MODULE_AUTH_SERVICE, oxydException.ERROR_INVALID_USERNAME_OR_PASSWORD, "invalid Username or password");
        if (validPwd.compareTo(pwd) != 0)
            throw new oxydException(oxydException.MODULE_AUTH_SERVICE, oxydException.ERROR_INVALID_USERNAME_OR_PASSWORD, "invalid Username or password");

        if (loggedIn == null)
            loggedIn = new HashMap();
        User user = getUser(login,  serverContext);
        if (user == null)
        {
            user = new User(login);
            loggedIn.put(new Integer(user.getLogin().hashCode()).toString(), user);
        }
        serverContext.getKernelContext().setUser(user);
        return new Integer(user.getLogin().hashCode()).toString();
    }

    public void login(String key, ServerContext serverContext) throws oxydException {
        if (!loggedIn.containsKey(key))
            throw new oxydException(oxydException.MODULE_AUTH_SERVICE, oxydException.ERROR_INVALID_KEY, "invalid key");
        serverContext.getKernelContext().setUser((User) loggedIn.get(key));
    }

    public void logout(String key, ServerContext serverContext) throws oxydException {
        if (!loggedIn.containsKey(key))
            throw new oxydException(oxydException.MODULE_AUTH_SERVICE, oxydException.ERROR_INVALID_KEY, "invalid key");
        loggedIn.remove(key);
    }

}
