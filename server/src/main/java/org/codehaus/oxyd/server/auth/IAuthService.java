package org.codehaus.oxyd.kernel.auth;

import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.server.ServerContext;
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

public interface IAuthService {

    String login(String login, String pwd, ServerContext context) throws oxydException;

    void login(String key, ServerContext context) throws oxydException;

    void logout(String key, ServerContext context) throws oxydException;

    void addLoggedIn(User user);

    User     getUser(String login, ServerContext serverContext);
}
