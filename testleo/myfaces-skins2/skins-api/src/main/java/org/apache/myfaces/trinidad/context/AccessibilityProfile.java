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
package org.apache.myfaces.trinidad.context;

/**
 * Specifies a set of accessibility-related properties that are applied
 * to the current request.
 * 
 * AccessibilityProfile instances are obtained by the getInstance() 
 * factory method.
 * 
 * AccessibilityProfile instances are immutable.
 */
public class AccessibilityProfile
{
  /**
   * Color contrast values
   */
  public enum ColorContrast
  {
    /**
     * Color contrast value for users who prefer styles/content optimized
     * for high contrast settings.
     */
    HIGH,
    
    /**
     * Color contrast value for users who prefer the default
     * (non-high contrast-optimized) styles/content.
     */
    STANDARD
  }

  /**
   * Font size values.
   * 
   * Note that the font size property is not used to define a specific
   * (eg. pixel-based) font size, but rather to provide a hint that the
   * use may prefer larger fonts.  The physical font size to use is specified
   * by the skin.
   */
  public enum FontSize
  {
    /**
     * Font size value for users who prefer larger font sizes.
     */
    LARGE,
    
    /**
     * Font size value for users who prefer the default (medium) font sizes.
     */
    MEDIUM
  }
  
  /**
   * Returns an AccessibilityProfile instance with the specified properties.
   * @param colorContrast Specifies the user's color contrast preference.  If 
   *   null, defaults to ColorContrast.STANDARD.
   * @param fontSize Specifies the user's font size preference.  If null, 
   *   defaults to FontSize.MEDIUM.
   */
  public static AccessibilityProfile getInstance(
    ColorContrast colorContrast,
    FontSize      fontSize
    )
  {    
    // Note: we could cache and share AccessibilityProfile instances
    // here if that seems useful.
    return new AccessibilityProfile(colorContrast, fontSize);
  }

  /**
   * Returns an AccessiblityProfile instance with the default preferences.
   */
  public static AccessibilityProfile getDefaultInstance()
  {
    return _sDefaultInstance;
  }

  /**
   * Returns the user's preferred color contrast setting.
   */
  public ColorContrast getColorContrast()
  {
    return _colorContrast;
  }
  
  /**
   * Returns the user's preferred font size setting.
   */
  public FontSize getFontSize()
  {
    return _fontSize;    
  }

  /**
   * Convenience method for testing whether high contrast content is required.
   */
  public final boolean isHighContrast()
  {
    return (_colorContrast == ColorContrast.HIGH);
  }
    
  /**
   * Convenience method for testing whether large fonts are required.
   */
  public final boolean isLargeFonts()
  {
    return (_fontSize == FontSize.LARGE);
  }
  
  // No need to support subclassing yet, so keep the constructor private.
  // Clients should use the getInstance() factory method.
  private AccessibilityProfile(
    ColorContrast colorContrast,
    FontSize      fontSize
    )
  {

    _colorContrast = (colorContrast == null) ? ColorContrast.STANDARD : colorContrast;
    _fontSize = (fontSize == null) ? FontSize.MEDIUM : fontSize;
  }

  private final ColorContrast _colorContrast;
  private final FontSize      _fontSize;
  
  // Default instance
  private static final AccessibilityProfile _sDefaultInstance =
    AccessibilityProfile.getInstance(ColorContrast.STANDARD, FontSize.MEDIUM);
}
