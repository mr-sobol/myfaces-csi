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
package org.apache.myfaces.trinidaddemo.webapp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class SourceCodeServlet extends HttpServlet
{
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
  {
  String webPage = req.getServletPath();
  
  // remove the '*.source' suffix that maps to this servlet
  int source = webPage.indexOf(".source");
  webPage = webPage.substring(0, source);

  //remove "/faces" mapping
  webPage = StringUtils.remove(webPage, "/faces");

  // get the actual file location of the requested resource
  String realPath = getServletConfig().getServletContext().getRealPath(webPage);

  // output an HTML page
  res.setContentType("text/plain");

  // print some html
  ServletOutputStream out = res.getOutputStream();

  // print the file
  InputStream in = null;
  try 
  {
      in = new BufferedInputStream(new FileInputStream(realPath));
      int ch;
      while ((ch = in.read()) !=-1) 
      {
          out.print((char)ch);
      }
  }
  finally {
      if (in != null) in.close();  // very important
  }
}

}