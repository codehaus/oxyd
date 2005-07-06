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

import java.io.*;

public class Utils {

    public static String getResourceContent(String name, ServerContext serverContext) throws IOException {
        InputStream is = serverContext.getServletContext().getResourceAsStream(name);
        return Utils.getFileContent(new InputStreamReader(is));
    }

    public static String getFileContent(Reader reader) throws IOException {
        StringBuffer content = new StringBuffer();
        BufferedReader fr = new BufferedReader(reader);
        String line;
        line = fr.readLine();
        while (true) {
            if (line==null) {
                fr.close();
                return content.toString();
            }
            content.append(line);
            content.append("\n");
            line = fr.readLine();
        }
    }
}
