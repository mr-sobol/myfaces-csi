/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.myfaces.trinidadinternal.config.xmlHttp;

import java.io.IOException;
import java.io.PrintWriter;

import java.lang.reflect.InvocationTargetException;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.jsp.JspException;

import org.apache.myfaces.trinidad.logging.TrinidadLogger;
import org.apache.myfaces.trinidadinternal.application.StateManagerImpl;
import org.apache.myfaces.trinidadinternal.renderkit.core.ppr.XmlResponseWriter;

/**
 * Though a configurator in spirit, at this point it purely exposes
 * Servlet functionality, and is only used to wrap the servlet response.
 * 
 * TODO: support portlets, and make this a true configurator.
 */
public class XmlHttpConfigurator
  /*extends Configurator*/
{
  public XmlHttpConfigurator()
  {
  }

  public static ServletRequest getAjaxServletRequest(ServletRequest request)
  {
    return new XmlHttpServletRequest(request);
  }

  public static void beginRequest(ExternalContext externalContext)
  {
    StateManagerImpl.reuseRequestTokenForResponse(externalContext);
    Object response = externalContext.getResponse();
    if (response instanceof ServletResponse)
    {
      externalContext.setResponse(
         new XmlHttpServletResponse((ServletResponse) response));
    }
  }

  /**
   * Sends a <redirect> element to the server
   */
  static void __sendRedirect(final PrintWriter writer, final String url)
    throws IOException
  {
    XmlResponseWriter rw = new XmlResponseWriter(writer, "UTF-8");
    rw.startDocument();
    // Add another PI indicating that this is a rich response
    // FIXME: this code is duplicated in PPRResponseWriter - fix that
    rw.write("<?Tr-XHR-Response-Type ?>\n");
    rw.startElement("redirect", null);
    rw.writeText(url, null);
    rw.endElement("redirect");
    rw.endDocument();
    rw.close();
  }

  /**
   * Handle a server-side error by reporting it back to the client.
   * TODO: add configuration to hide this in a production
   * environment.
   */
  public static void handleError(ExternalContext ec, 
                                 Throwable t) throws IOException
  {
    String error = _getErrorString();
    _LOG.severe(error, t);

    ServletResponse response = (ServletResponse)ec.getResponse();
    PrintWriter writer = response.getWriter();
    XmlResponseWriter rw = new XmlResponseWriter(writer, "UTF-8");
    rw.startDocument();
    // Add another PI indicating that this is a rich response
    // FIXME: this code is duplicated in PPRResponseWriter - fix that
    rw.write("<?Tr-XHR-Response-Type ?>\n");
    rw.startElement("error", null);
    rw.writeAttribute("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
    rw.writeText(_getExceptionString(t) + _PLEASE_SEE_ERROR_LOG + error, null);
    rw.endElement("error");
    rw.endDocument();
    rw.close();
  }


  static private String _getExceptionString(Throwable t)
  {
    // Unwrap any uninteresting exceptions
    while (_isUninterestingThrowable(t))
    {
      Throwable cause = t.getCause();
      if (cause == null)
        break;
      t = cause;
    }

    String message = t.getMessage();
    if ((message == null) || "".equals(message))
      message = t.getClass().getName();

    return message + "\n\n";
  }

  /**
   * Unwrap a bunch of "uninteresting" throwables
   */
  static private boolean _isUninterestingThrowable(Throwable t)
  {
    // FIXME: add ELException in EE5
    return ((t instanceof ServletException) ||
            (t instanceof JspException) ||
            (t instanceof FacesException) ||
            (t instanceof InvocationTargetException));
  }

  static private String _getErrorString()
  {
    return _PPR_ERROR_PREFIX + _getErrorCount();
  }

  static private synchronized int _getErrorCount()
  {
    return (++_ERROR_COUNT);
  }

  static private final String _PPR_ERROR_PREFIX = "Server Exception during PPR, #";
  // TODO Get this from a resource bundle?
  static private final String _PLEASE_SEE_ERROR_LOG =
    "For more information, please see the server's error log for\n" +
    "an entry beginning with: ";

  static private int _ERROR_COUNT = 0;


  static private final TrinidadLogger _LOG =
    TrinidadLogger.createTrinidadLogger(XmlHttpConfigurator.class);
}
