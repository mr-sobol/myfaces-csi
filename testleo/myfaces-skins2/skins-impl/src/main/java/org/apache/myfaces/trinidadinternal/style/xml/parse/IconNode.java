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
package org.apache.myfaces.trinidadinternal.style.xml.parse;

import org.apache.myfaces.trinidad.skin.Icon;
import org.apache.myfaces.trinidad.logging.TrinidadLogger;

/**
 * Object which represents a single <icon> element.
 *
 * @version $Name:  $ ($Revision$) $Date$
 */
public class IconNode
{
  /**
   * Creates a IconNode
   * @param namespace The namespace of the icon
   * @param name The name of the icon
   * @param icon The Icon instance
   */
  public IconNode(
    String name,
    Icon   icon
    )
  {
    if (name == null)
    {
      throw new NullPointerException(_LOG.getMessage(
        "NULL_NAME"));
    }

    _name = name;
    _icon = icon;
  }


  /**
   * Returns the name of the icon that is defined
   * by this IconNode.
   */
  public String getIconName()
  {
    return _name;
  }

  /**
   * Returns the Icon instance for this IconNode.
   */
  public Icon getIcon()
  {
    return _icon;
  }

  private String      _name;
  private Icon        _icon;
  private static final TrinidadLogger _LOG = TrinidadLogger.createTrinidadLogger(
    IconNode.class);
}
