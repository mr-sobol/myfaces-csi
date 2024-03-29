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
package org.apache.myfaces.trinidadinternal.context.external;

import java.util.Enumeration;

import javax.servlet.ServletContext;


/**
 * ServletContext init parameters as Map.
 *
 * @version $Revision$ $Date$
 */
public class ServletInitParameterMap extends AbstractAttributeMap<String, String>
{
  public ServletInitParameterMap(final ServletContext servletContext)
  {
    _servletContext = servletContext;
  }

  @Override
  protected String getAttribute(final Object key)
  {
    if (key.toString().equals(key))
    {
      return _servletContext.getInitParameter(key.toString());
    }
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  protected Enumeration<String> getAttributeNames()
  {
    return _servletContext.getInitParameterNames();
  }

  final ServletContext _servletContext;
}
