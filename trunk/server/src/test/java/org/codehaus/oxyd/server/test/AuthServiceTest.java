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

import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.Actions;
import org.codehaus.oxyd.kernel.auth.AuthService;
import org.codehaus.oxyd.server.ServerContext;
import junit.framework.TestCase;

public class AuthServiceTest  extends TestCase {
    private ServerContext context;
    private AuthService authService;


    public void setUp() throws oxydException {

        context = Utils.initServerContext();
        authService = new AuthService();
        authService.addUser("validLogin", "ValidPwd");
        authService.addUser("toto", "titi");
    }

    public void testLogin() throws oxydException {
        context.getKernelContext().setUser(null);
        try {
            authService.login("invalidLogin", "pwd", context);
            assertTrue(false);
        }
        catch(oxydException e)
        {}
        assertNull(context.getKernelContext().getUser());

        try {
            authService.login("validLogin", "invalidPwd", context);
            assertTrue(false);
        }
        catch(oxydException e)
        {}
        assertNull(context.getKernelContext().getUser());

        try {
            authService.login("", "invalidPwd", context);
            assertTrue(false);
        }
        catch(oxydException e)
        {}
        assertNull(context.getKernelContext().getUser());

        try {
            authService.login("validLogin", "", context);
            assertTrue(false);
        }
        catch(oxydException e)
        {}
        assertNull(context.getKernelContext().getUser());

        try {
            authService.login("validLogin", "ValidPwd", context);

        }
        catch(oxydException e)
        {
            assertTrue(false);
        }
        assertNotNull(context.getKernelContext().getUser());
        assertEquals("validLogin", context.getKernelContext().getUser().getLogin());

    }

    public void testlogin2() throws oxydException {
        String key = null;
        context.getKernelContext().setUser(null);
        try {
            key = authService.login("validLogin", "ValidPwd", context);

        }
        catch(oxydException e)
        {
            assertTrue(false);
        }
        assertNotNull(context.getKernelContext().getUser());
        assertEquals("validLogin", context.getKernelContext().getUser().getLogin());
        context.getKernelContext().setUser(null);
        authService.login(key, context);
        assertNotNull(context.getKernelContext().getUser());
        assertEquals("validLogin", context.getKernelContext().getUser().getLogin());

        try {
            authService.login("plop", context);
            assertTrue(false);
        }
        catch(oxydException e)
        {
            
        }
    }
}
