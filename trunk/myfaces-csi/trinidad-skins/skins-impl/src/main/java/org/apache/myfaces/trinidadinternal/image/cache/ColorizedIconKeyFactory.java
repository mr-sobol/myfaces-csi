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
package org.apache.myfaces.trinidadinternal.image.cache;

import java.util.Map;

import org.apache.myfaces.trinidadinternal.image.ImageContext;
import org.apache.myfaces.trinidadinternal.image.ImageConstants;

/**
 * CacheKeyFactory implementation for colorized icon keys
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/image/cache/ColorizedIconKeyFactory.java#0 $) $Date: 10-nov-2005.19:06:02 $
 */
public class ColorizedIconKeyFactory implements CacheKeyFactory
{
  /**
   * Implementation of CacheKeyFactory.getCacheKey().
   */
  public Object getCacheKey(ImageContext context, Map<Object, Object> properties)
  {
    if (properties.get(ImageConstants.DARK_COLOR_KEY) != null)
      return new CoreColorizedIconKey(context, properties);

    return new AccentColorizedIconKey(context, properties);
  }
}
