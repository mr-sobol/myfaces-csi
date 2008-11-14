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
package org.apache.myfaces.trinidadinternal.style;

import java.util.Iterator;

/**
 * A Style object defines a set of visual (or aural) style properties.
 * The Style interface exposes two method for retrieving properties:
 * getProperty() and getParsedProperty().  getProperty()
 * takes a String property name and returns a String property value.
 * getParsedProperty() takes a key object (eg. FONT_WEIGHT_KEY) and
 * returns the parsed, typed, Java object which represents the
 * property value.  The type of the returned object is key-specific,
 * eg. for FONT_WEIGHT_KEY, an java.lang.Integer is returned.
 * For BACKGROUND_KEY, a java.awt.Color is returned.
 *
 * @version $Name:  $ ($Revision$) $Date$
 */
public interface Style
{
  /**
   * Key for obtaining the Color object which corresponds to the
   * style's "background-color" property.
   */
  public static final ParsedPropertyKey BACKGROUND_KEY =
    new ParsedPropertyKey("background", 0);

  /**
   * Key for obtaining the Color object which corresponds to the
   * style's "color" property.
   */
  public static final ParsedPropertyKey FOREGROUND_KEY =
    new ParsedPropertyKey("foreground", 1);

  /**
   * Key for obtaining the list of font family names for this style.
   * The list is implemented as an oracle.bali.share.collection.ImmutableArray
   * of String objects, sorted from most preferable to least preferable.
   */
  public static final ParsedPropertyKey FONT_FAMILIES_KEY =
    new ParsedPropertyKey("families", 2);

  /**
   * This key is passed to Style.parseProperty() to obtain the font
   * size for a Style.  The font size value returned from
   * parseProperty() is an Integer object which indicates the font size
   * in point units.
   */
  public static final ParsedPropertyKey FONT_SIZE_KEY =
    new ParsedPropertyKey("size", 3);

  /**
   * This key is passed to Style.parseProperty() to obtain the font
   * style for a Style.  The font style value returned from
   * parseProperty() is one of the FONT_STYLE constants defined by
   * the Style interface.
   */
  public static final ParsedPropertyKey FONT_STYLE_KEY =
    new ParsedPropertyKey("style", 4);

  /**
   * This key is passed to Style.parseProperty() to obtain the font
   * weight for a Style.  The font weight value returned from
   * parseProperty() is one of the FONT_WEIGHT constants defined by
   * the Style interface.
   */
  public static final ParsedPropertyKey FONT_WEIGHT_KEY =
    new ParsedPropertyKey("weight", 5);

  /**
   * This key is passed to Style.parseProperty() to obtain the text antialias
   * style for a Style object.  The text antialias value returned from
   * parseProperty() is a Boolean object.  A returned value of Boolean.TRUE
   * indicates that text rendered using the Style should be antialiased.
   */
  public static final ParsedPropertyKey TEXT_ANTIALIAS_KEY =
    new ParsedPropertyKey("antialias", 6);

  /**
   * Constant for plain font style.
   */
  public static final Object PLAIN_FONT_STYLE  = 0;

  /**
   * Constant for italic font style.
   */
  public static final Object ITALIC_FONT_STYLE = 1;

  /**
   * Constant for plain font weight.
   */
  public static final Object PLAIN_FONT_WEIGHT = 0;

  /**
   * Constant for bold font weight.
   */
  public static final Object BOLD_FONT_WEIGHT  = 1;

  /**
   * Returns the names of the properties defined by this style.
   */
  // -= Simon Lessard =-
  // FIXME: This should be changed to <String> once the issues 
  //        with ArrayMap are fixed. ATM (2006-08-04) ArrayMap 
  //        have huge problem working with anything but Object
  public Iterator<Object> getPropertyNames();

  /**
   * Returns the value of the property with the specified name.
   *
   * @param name The property name for the property to return
   */
  public String getProperty(String name);

  /**
   * Returns a parsed Java object corresponding to the specified
   * property key.
   * <p>
   * The type of object returned is dependent on the type of
   * property requested.  For example, when the FONT_SIZE_KEY is
   * requested, an java.lang.Integer object is
   * returned.  When BACKGROUND_KEY or FOREGROUND_KEY is requested
   * a java.awt.Color is returned.  Null is returned if no value
   * is defined for the specified property.
   *
   * @param One of the KEY constants (eg. FONT_SIZE_KEY, BACKGROUND_KEY,
   *  etc...) defined by the Style interface.
   * @param throws PropertyParseException Thrown if the property value
   *   can not be parsed.
   */
  public Object getParsedProperty(ParsedPropertyKey key)
    throws PropertyParseException;

  /**
   * Converts the style to a String suitable for use as an inline style
   * attribute value.
   */
  public String toInlineString();
}
