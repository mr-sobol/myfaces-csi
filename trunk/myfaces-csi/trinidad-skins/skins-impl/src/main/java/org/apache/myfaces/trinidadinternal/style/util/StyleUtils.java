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
package org.apache.myfaces.trinidadinternal.style.util;

import java.util.regex.Pattern;

/**
 * Generic style utilities.
 *
 * @version $Name:  $ ($Revision: adfrt/faces/adf-faces-impl/src/main/java/oracle/adfinternal/view/faces/style/util/StyleUtils.java#0 $) $Date: 10-nov-2005.18:58:52 $
 */
public class StyleUtils
{

  public static final String RTL_CSS_SUFFIX = ":rtl";
  public static final String LTR_CSS_SUFFIX = ":ltr";


  /**
   * Convert the characters that should not be in a selector
   * e.g., the | that is in the namespace to an _ so the css class name
   * is valid.
   * @param input String to convert.
   * @return the input string with all "|" converted to "_" and all "::" to "_"
   */
  public static String convertToValidSelector(String selector)
  {
    
    if (selector == null) return null;
    selector = selector.replace('|', '_');
    // This is the code that's actually getting called by String.replaceAll();
    // this code is way too heavy.
    if (selector.indexOf("::") > 0)
      selector = _DOUBLE_COLON_PATTERN.matcher(selector).replaceAll("_");

    return selector;
  }

  static private final Pattern _DOUBLE_COLON_PATTERN = Pattern.compile("::");
}
