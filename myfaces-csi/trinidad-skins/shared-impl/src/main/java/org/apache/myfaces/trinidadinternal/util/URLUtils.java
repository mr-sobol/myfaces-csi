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
package org.apache.myfaces.trinidadinternal.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;

import java.net.UnknownServiceException;
import java.net.URL;
import java.net.URLConnection;

public class URLUtils
{
  private URLUtils()
  {
  }


  static public long getLastModified(URL url) throws IOException
  {
    if ("file".equals(url.getProtocol()))
    {
      String externalForm = url.toExternalForm();
      // Remove the "file:"
      File file = new File(externalForm.substring(5));

      return file.lastModified();
    }
    else
    {
      URLConnection connection = url.openConnection();
      long modified = connection.getLastModified();
      try
      {
        InputStream is = connection.getInputStream();
        if (is != null)
          is.close();
      }
      // If the connection doesn't support getInputStream(),
      // or there's an IOException (Tomcat throws an exception
      // on directory views, for example), that's OK.
      catch (UnknownServiceException use)
      {
      }
      catch (IOException ioe)
      {
      }

      return modified;
    }
  }
}
