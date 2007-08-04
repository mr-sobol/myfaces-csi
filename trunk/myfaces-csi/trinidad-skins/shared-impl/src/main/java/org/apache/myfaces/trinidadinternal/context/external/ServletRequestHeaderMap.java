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
import javax.servlet.http.HttpServletRequest;


/**
 * HttpServletRequest headers as Map.
 *
 * @version $Revision: 278654 $ $Date: 2005-09-04 18:32:35 -0600 (Sun, 04 Sep 2005) $
 */
class ServletRequestHeaderMap extends AbstractAttributeMap<String, String>
{
  ServletRequestHeaderMap(final HttpServletRequest httpServletRequest)
  {
    _httpServletRequest = httpServletRequest;
  }

  @Override
  protected String getAttribute(final Object key)
  {
    if (key.toString().equals(key))
    {
      return _httpServletRequest.getHeader(key.toString());
    }
    return null;
  }

  @Override
  protected Enumeration<String> getAttributeNames()
  {
    @SuppressWarnings("unchecked")
    final Enumeration<String> headerNames = _httpServletRequest.getHeaderNames();
    return headerNames;
  }

  private final HttpServletRequest _httpServletRequest;
}
