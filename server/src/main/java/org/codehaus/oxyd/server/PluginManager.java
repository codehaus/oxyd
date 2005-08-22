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
import org.codehaus.oxyd.kernel.oxydException;
import org.codehaus.oxyd.kernel.document.IDocument;
import org.dom4j.Document;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class PluginManager {
    private Map     pluginsObjects = new HashMap();

    public PluginManager(String plugins)
    {

        String[] classNames = plugins.split(",");
        for (int i=0;i<classNames.length;i++) {
            addPlugin(classNames[i]);
        }
    }

    public void addPlugin(String className) {
        try {

            IOxydPlugin plugin = (IOxydPlugin) Class.forName(className).getConstructor(null).newInstance(null);
            if (plugin!=null) {
                pluginsObjects.put(plugin.getName(), plugin);
            }
        } catch (Exception e) {
            // Log an error but do not fail..
            e.printStackTrace();
        }
    }

    public Document execute(String plugin, ServerContext serverContext ) throws oxydException {
        IOxydPlugin pluginObj = (IOxydPlugin) pluginsObjects.get(plugin);
        if (pluginObj != null)
        {
            String url;
            url = serverContext.getRequest().getRequestURI();
            url = url.substring(url.indexOf("/", url.indexOf(plugin)));
            return pluginObj.execute(url, serverContext);

        }
        else
            throw new oxydException();
    }


    public IDocument beforeGetDocument(String workspace, String document, ServerContext serverContext)
    {
        return null;
    }

    public Boolean beforeHasRight(String workspace, String document, ServerContext serverContext)
    {
        return null;
    }

    public String beforeLogin(String userName, String pwd, ServerContext serverContext) throws oxydException {
        String key = null;
        Iterator it = pluginsObjects.values().iterator();
        while(it.hasNext())
        {
            IOxydPlugin plugin = (IOxydPlugin) it.next();
            key = plugin.beforeLogin(userName, pwd, serverContext);
            if (key != null)
                break;
        }
        return key;
    }

    public IDocument afterOpenningDocument(String space, String document, IDocument doc, ServerContext serverContext) throws oxydException {
        Iterator it = pluginsObjects.values().iterator();
        while(it.hasNext())
        {
            IOxydPlugin plugin = (IOxydPlugin) it.next();
            doc = plugin.afterOpenningDocument(space, document, doc, serverContext);
        }
        return doc;
    }

    public void afterClosingDocument(IDocument doc, ServerContext serverContext) throws oxydException {
        Iterator it = pluginsObjects.values().iterator();
        while(it.hasNext())
        {
            IOxydPlugin plugin = (IOxydPlugin) it.next();
            plugin.afterClosingDocument(doc, serverContext);
        }
    }
}
