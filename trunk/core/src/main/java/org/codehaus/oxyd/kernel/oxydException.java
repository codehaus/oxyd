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
package org.codehaus.oxyd.kernel;

import java.text.MessageFormat;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.StringWriter;


public class oxydException extends Exception {
    private int         module;
    private int         code;
    private String      message;
    private Throwable   exception;
    private Object[]    args;

    public static final int MODULE_DOCUMENT_IMPL = 1;
    public static final int MODULE_DOCUMENT_TEXT_IMPL = 2;
    public static final int MODULE_ACTION = 3;
    public static final int MODULE_WORKSPACE = 4;
    public static final int MODULE_ACTION_MANAGER = 5;
    public static final int MODULE_CLIENT_UTILS = 6;
    public static final int MODULE_CLIENT_ACTION = 7;
    public static final int MODULE_AUTH_SERVICE = 8;
    public static final int MODULE_XWIKI_STORE = 9;
    public static final int MODULE_HIBERNATE_STORE = 10;
    public static final int MODULE_PLUGIN = 11;

    public static final int ERROR_BLOCK_LOCKED = 1001;
    public static final int ERROR_BLOCK_NOT_LOCKED = 1002;
    public static final int ERROR_ALREADY_EXIST = 1004;
    public static final int ERROR_WORKSPACE_NOT_EXIST = 1005;
    public static final int ERROR_DOCUMENT_NOT_EXIST = 1006;
    public static final int ERROR_BLOCK_NOT_EXIST = 1007;
    public static final int ERROR_XML_ERROR = 1008;
    public static final int ERROR_COMMAND_NOT_FOUND = 1009;
    public static final int ERROR_HTTP_ERROR= 1010;
    public static final int ERROR_INVALID_USERNAME_OR_PASSWORD = 1011;
    public static final int ERROR_INVALID_KEY = 1012;
    public static final int ERROR_UNKNOWN= 1013;
    public static final int ERROR_DOCUMENT_NOT_OPEN= 1014;
    public static final int ERROR_SYSTEM_LOCK= 1015;
    public static final int ERROR_CANNOT_CLOSE_DOCUMENT= 1016;
    public static final int ERROR_CANNOT_DELETE_DOCUMENT= 1017;


    public static final int ERROR_NOT_IMPLEMENTED = 9000;

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public oxydException()
    {

    }

    public oxydException(int module, int code, String message, Throwable e, Object[] args)
    {
        setCode(code);
        setModule(module);
        setMessage(message);
    }

    public oxydException(int module, int code, String message, Throwable e)
    {
        setCode(code);
        setModule(module);
        setMessage(message);
    }


    public oxydException(int module, int code, String message)
    {
        setCode(code);
        setModule(module);
        setMessage(message);
    }

    public oxydException(int module, int code)
    {
        setCode(code);
        setModule(module);
    }



    public String getMessage()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Error number ");
        buffer.append(getCode());
        buffer.append(" in ");
        buffer.append(getModuleName());
        buffer.append(": ");

        if (message!=null)
        {
            if (args==null)
                buffer.append(message);
            else
            {
                MessageFormat msgFormat = new MessageFormat (message);
                try
                {
                    buffer.append(msgFormat.format(args));
                }
                catch (Exception e)
                {
                    buffer.append("Cannot format message " + message + " with args ");
                    for (int i = 0; i< args.length ; i++)
                    {
                        if (i!=0)
                            buffer.append(",");
                        buffer.append(args[i]);
                    }
                }
            }
        }

        if (exception!=null) {
             buffer.append("\nWrapped Exception: ");
             buffer.append(exception.getMessage());
        }
        return buffer.toString();
    }

    private String getModuleName() {
        return ""+getModule();
    }

    public String getFullMessage()
    {
        StringBuffer buffer = new StringBuffer(getMessage());
        buffer.append("\n");
        buffer.append(getStackTraceAsString());
        return buffer.toString();
    }

    public void printStackTrace(PrintWriter s) {
        if (exception!=null) {
            exception.printStackTrace(s);
        }
    }

    public void printStackTrace(PrintStream s) {
        if (exception!=null) {
            exception.printStackTrace(s);
        }
    }

    public String getStackTraceAsString() {
        StringWriter swriter = new StringWriter();
        PrintWriter pwriter = new PrintWriter(swriter);
        printStackTrace(pwriter);
        pwriter.flush();
        return swriter.getBuffer().toString();
    }

    public String getStackTraceAsString(Throwable e) {
        StringWriter swriter = new StringWriter();
        PrintWriter pwriter = new PrintWriter(swriter);
        e.printStackTrace(pwriter);
        pwriter.flush();
        return swriter.getBuffer().toString();
    }

}
