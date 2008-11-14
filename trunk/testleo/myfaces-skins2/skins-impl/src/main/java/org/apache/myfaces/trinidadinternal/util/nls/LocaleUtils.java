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
package org.apache.myfaces.trinidadinternal.util.nls;

import java.util.Locale;

import org.apache.myfaces.trinidad.context.LocaleContext;

/**
 * Utility class dealing with Locale-related issues, including
 * common direction and alignment constants.
 * <p>
 * @version $Name:  $ ($Revision$) $Date$
 */
public final class LocaleUtils
{


  /**
   * Reading direction constant
   */
  public static final int DIRECTION_DEFAULT     = 0;

  /**
   * Reading direction constant
   */
  public static final int DIRECTION_LEFTTORIGHT = 1;

  /**
   * Reading direction constant
   */
  public static final int DIRECTION_RIGHTTOLEFT = 2;

  
  /**
   * Conversion function to go from LocaleContext to the obsolete
   * reading direction API.
   */
  public static int getReadingDirection(LocaleContext localeContext)
  {
    return localeContext.isRightToLeft()
             ? DIRECTION_RIGHTTOLEFT
             : DIRECTION_LEFTTORIGHT;
  }


  /**
   * Given a locale, returns the default reading direction.
   */
  public static int getReadingDirectionForLocale(
    Locale loc
    )
  {
    if (loc == null)
    {
      loc = Locale.getDefault();
    }

    String language = loc.getLanguage();

    // arabic and hebrew are right-to-left languages.  We treat "iw" as
    // hebrew because "iw" was the old code for Hebrew and the JDK
    // still uses it
    if (language.equals("ar") ||
        language.equals("he") ||
        language.equals("iw"))
    {
      return DIRECTION_RIGHTTOLEFT;
    }
    else
    {
      return DIRECTION_LEFTTORIGHT;
    }
  }


  /**
   * Decodes an IANA string (e.g., en-us) into a Locale object.
   */
  public static Locale getLocaleForIANAString(String ianaString)
  {
    if ((ianaString == null) || "".equals(ianaString))
      return null;

    String language;
    String country = "";
    String variant = "";

    int dashIndex = ianaString.indexOf('-');
    if (dashIndex < 0)
    {
      language = ianaString;
    }
    else
    {
      language = ianaString.substring(0, dashIndex);
      int start = dashIndex + 1;
      dashIndex = ianaString.indexOf('-', start);
      if (dashIndex < 0)
      {
        country = ianaString.substring(start);
      }
      else
      {
        country = ianaString.substring(start, dashIndex);
        variant = ianaString.substring(dashIndex + 1);
      }
    }

    return new Locale(language, country, variant);
  }

}
