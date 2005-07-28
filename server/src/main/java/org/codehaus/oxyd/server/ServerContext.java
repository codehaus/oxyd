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
package org.codehaus.oxyd.server;

import org.codehaus.oxyd.kernel.Context;
import org.codehaus.oxyd.kernel.Actions;
import org.codehaus.oxyd.kernel.auth.IAuthService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class ServerContext {
    private Context             context;
    private ServletContext      servletContext;
    private Actions             actions;
    private HttpServletRequest  request;
    private IAuthService        authService;

    public Context getKernelContext() {
        return context;
    }

    public void setKernelContext(Context context) {
        this.context = context;
    }

    public IAuthService getAuthService() {
        return authService;
    }

    public void setAuthService(IAuthService authService) {
        this.authService = authService;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setActionManager(Actions actions)
    {
        this.actions = actions;
    }

    public Actions getActionManager()
    {
        return actions;
    }

    public HttpServletRequest getRequest()
    {
        return this.request;
    }

    public void setRequest(HttpServletRequest req)
    {
        this.request = req;
    }
}
