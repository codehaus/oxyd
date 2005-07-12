/* ====================================================================
 *   Copyright 2005 JÈrÈmi Joslin.
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
package org.codehaus.oxyd.kernel.utils;

import org.dom4j.Element;

public class Utils {

    public static String noaccents(String text) {
        String temp = text;
        String orig = "‡‚‰ÈËÍÎÓÔÙˆ˘˚¸";
        String targ = "aaaeeeeiioouuu";
        for (int i=0;i<orig.length();i++)
            temp = temp.replace(orig.charAt(i), targ.charAt(i));
        return temp;
    }

    public static String getElementText(Element docel, String name) {
         Element el = docel.element(name);
         if (el==null)
             return "";
         else
             return el.getText();
     }

}
