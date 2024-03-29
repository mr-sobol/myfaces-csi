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
package org.apache.myfaces.trinidadinternal.image.xml.parse;

import org.apache.myfaces.trinidadinternal.share.xml.ParserFactory;
import org.apache.myfaces.trinidadinternal.share.xml.ParserManager;
import org.apache.myfaces.trinidadinternal.share.xml.NodeParser;
import org.apache.myfaces.trinidadinternal.share.xml.ParseContext;

import org.apache.myfaces.trinidadinternal.image.util.MapArea;


/**
 * Factory for creating ImageMapParsers
 * @version $Name:  $ ($Revision: 171 $) $Date: 2008-11-14 12:06:34 +0800 (Fri, 14 Nov 2008) $
 */
public class ImageMapParserFactory implements ParserFactory
{
  /**
   * Gets the shared ImageMapParserFactory instance.
   */
  public static ImageMapParserFactory sharedInstance()
  {
    return _sInstance;
  }

  /**
   * Registers this factory on a ParserManager.
   */
  public void registerSelf(ParserManager manager, String namespace)
  {
    // We support parsing of both a single ImageProviderRequest as 
    // well as a set of ImageProviderRequests.
    manager.registerFactory(MapArea[].class, namespace, this);
  }

  /**
   * Returns the parser used for creating ImageProviderRequest
   * instances with the given namespace and local name.
   */
  public NodeParser getParser(
    ParseContext context,                                
    String       namespaceURI,
    String       localName)
  {
    return new ImageMapParser();
  }

  private ImageMapParserFactory() {}

  private static final ImageMapParserFactory _sInstance = 
    new ImageMapParserFactory();
}
