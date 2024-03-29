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
package org.codehaus.oxyd.kernel.auth;

import org.codehaus.oxyd.kernel.document.IDocument;

import java.util.*;

public class        User {
    String      login;
    List        openDocuments;
    Map         params;
    List        waitingMessage;

    public class Message {
        String workspace;
        String document;
        String msg;

        public Message(String workspace, String document, String msg){
            this.workspace = workspace;
            this.document = document;
            this.msg = msg;
        }

        public String getWorkspace() {
            return workspace;
        }

        public String getDocument() {
            return document;
        }

        public String getMsg() {
            return msg;
        }
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public User(String login) {
        setLogin(login);
    }

    public List getOpenDocuments() {
        return openDocuments;
    }

    public void addMessage(String workspaceName, String documentName, String msg)
    {
        if (waitingMessage == null)
            waitingMessage = new ArrayList();
        Message oMsg = new Message(workspaceName, documentName, msg);
        waitingMessage.add(oMsg);
    }

    public List getwaitingMessage(String workspaceName, String documentName)
    {
        List res = new ArrayList();
        if (waitingMessage == null)
            return res;
        Iterator it = waitingMessage.iterator();

        while (it.hasNext())
        {
            Message message = (Message) it.next();
            if (message.getWorkspace() == workspaceName && message.getDocument() == documentName)
            {
                res.add(message.getMsg());
                waitingMessage.remove(message);
            }
        }

        return res;
    }

    public void addOpenDocument(IDocument doc) {
        if (openDocuments == null)
            openDocuments = new ArrayList();
        if (!openDocuments.contains(doc))
            openDocuments.add(doc);
    }


    public void removeOpenDocument(IDocument doc) {
        if (openDocuments.contains(doc))
            openDocuments.remove(doc);
    }

    public void logout()
    {
        Iterator it = openDocuments.iterator();
        while (it.hasNext())
        {
            ((IDocument)it.next()).removeUser(this);
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final User user = (User) o;

        if (login != null ? !login.equals(user.login) : user.login != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (login != null ? login.hashCode() : 0);
        result = 29 * result + (openDocuments != null ? openDocuments.hashCode() : 0);
        return result;
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
