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

package org.codehaus.oxyd.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.codehaus.oxyd.kernel.oxydException;
import org.dom4j.Element;

import java.io.IOException;

public class Utils {

    public static String getURLContent(String surl) throws oxydException {
        HttpClient client = new HttpClient();

        // create a GET method that reads a file over HTTPS, we're assuming
        // that this file requires basic authentication using the realm above.
        GetMethod get = new GetMethod(surl);

        try {
            // execute the GET
            int status = client.executeMethod(get);

            if (status != 200)
                throw new oxydException(oxydException.MODULE_CLIENT_UTILS, oxydException.ERROR_HTTP_ERROR, "http code: "+ status);

            // print the status and response
            return get.getResponseBodyAsString();
        }
        catch(IOException e)
        {
            throw new oxydException(oxydException.MODULE_CLIENT_UTILS, oxydException.ERROR_HTTP_ERROR, e.getMessage());
        }
        finally {
            // release any connection resources used by the method
            get.releaseConnection();
        }
    }

    public static String getURLContent(String surl, String username, String password) throws IOException, oxydException {
        HttpClient client = new HttpClient();

        // pass our credentials to HttpClient, they will only be used for
        // authenticating to servers with realm "realm", to authenticate agains
        // an arbitrary realm change this to null.
        client.getState().setCredentials(null,
                null,
                new UsernamePasswordCredentials(username, password));

        // create a GET method that reads a file over HTTPS, we're assuming
        // that this file requires basic authentication using the realm above.
        GetMethod get = new GetMethod(surl);

        try {
            // Tell the GET method to automatically handle authentication. The
            // method will use any appropriate credentials to handle basic
            // authentication requests.  Setting this value to false will cause
            // any request for authentication to return with a status of 401.
            // It will then be up to the client to handle the authentication.
            get.setDoAuthentication(true);

            // execute the GET
            int status = client.executeMethod(get);

            if (status != 200)
                throw new oxydException(oxydException.MODULE_CLIENT_UTILS, oxydException.ERROR_HTTP_ERROR, "http code: "+ status);


            return get.getResponseBodyAsString();
        } finally {
            // release any connection resources used by the method
            get.releaseConnection();
        }
    }

     public static String getElementText(Element docel, String name) {
         Element el = docel.element(name);
         if (el==null)
             return "";
         else
             return el.getText();
     }
}
