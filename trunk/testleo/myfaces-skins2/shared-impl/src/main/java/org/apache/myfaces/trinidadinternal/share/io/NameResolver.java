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
package org.apache.myfaces.trinidadinternal.share.io;

import java.io.IOException;

/**
 * NameResolvers are responsible for converting string names
 * into InputStreamProviders, which encapsulate a remote file.
 * Implementations exist that support using URLs, Files, Class
 * resources, and the Servlet API to locate files, but other APIs
 * may be substituted.
 * <p>
 * In some cases, the resolved target file may have need to
 * locate support files of its own.  Since those support files should
 * be looked for relative to the target file, NameResolver supports
 * creating new relative NameResolvers.
 * <p>
 * Developers writing their own NameResolver should strongly
 * consider subclassing BaseNameResolver.
 * <p>
 * @see org.apache.myfaces.trinidadinternal.share.xml.XMLUtils#getResolver
 * @see org.apache.myfaces.trinidadinternal.share.xml.XMLUtils#setResolver
 * <p>
 * @version $Name:  $ ($Revision$) $Date$
 */
public interface NameResolver
{
  /**
   * Given a name, returns an InputStreamProvider.  This
   * function should never return null - if the target
   * cannot be resolved, throw an IOException.
   * @param name the name of the target
   */
  public InputStreamProvider getProvider(String name) throws IOException;

  /**
   * Return the new NameResolver that should be used to resolve
   * names relative to a given name.  This function should never
   * return null - if the target cannot be resolved, return a
   * resolver that can only support absolute names.
   * @param name the name of the target
   */
  public NameResolver        getResolver(String name);
}
