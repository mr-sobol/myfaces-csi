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
package org.apache.myfaces.trinidadinternal.application;

import java.io.IOException;
import java.util.Locale;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

public class NullViewHandler extends ViewHandler
{
  @Override
  public Locale calculateLocale(FacesContext context)
  {
    return null;
  }

  @Override
  public String calculateRenderKitId(FacesContext context)
  {
    return null;
  }
  
  @Override
  public UIViewRoot createView(FacesContext context, String viewId)
  {
    return null;
  }

  @Override
  public String getActionURL(FacesContext context, String viewId)
  {
    return viewId;
  }

  @Override
  public String getResourceURL(FacesContext context, String path)
  {
    return path;
  }

  @Override
  public void renderView(FacesContext context, UIViewRoot viewToRender)
        throws IOException, FacesException
  {
  }

  @Override
  public UIViewRoot restoreView(FacesContext context, String viewId)
  {
    return null;
  }

  @Override
  public void writeState(FacesContext context) throws IOException
  {
  }
}
