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
package org.apache.myfaces.trinidadinternal.image.painter;

//
// Dependencies
//
import java.awt.Color;




/**
 * The ColorUtils Class includes class functions for performing operations
 * on colors.
 *
 * @version $Name:  $ ($Revision: 171 $) $Date: 2008-11-14 12:06:34 +0800 (Fri, 14 Nov 2008) $
 */
public class ColorUtils
{
  //
  // Weights used for each RGB component when converting to grayscale.  These
  // are the same weights used in NTSC broadcasting.
  //
  /**
   * NTSC-specified weight to use for red RGB component when converting to
   * luminance.
   */
  public static final double NTSC_WEIGHT_RED   = 0.299;

  /**
   * NTSC-specified weight to use for green RGB component when converting to
   * luminance.
   */
  public static final double NTSC_WEIGHT_GREEN = 0.587;


  /**
   * NTSC-specified weight to use for blue RGB component when converting to
   * luminance.
   */
  public static final double NTSC_WEIGHT_BLUE  = 0.144;


  /**
   * A good amount to modify a base color in order to calculate a lighter
   * color, equal to approximately 200minosity, when calling
   * getShadeColor().  A good darker color can be found by using
   * -DEFAULT_LIGHTER_CHANGE.
   * <p>
   */
  public static final int DEFAULT_LIGHTER_CHANGE = 51;

  /**
   * Returns the approximate luminosity of the color as an integer between 0
   * and 255, where 0 is black and 255 is white. This approximation is
   * based on NTSC but rather than the NTSC contribution weights
   * (.299, .587, .144) for red, green, and blue, this uses (.25, .50, .25)
   * for speed.
   * <p>
   * @param color The color to return the approximate luminance of.
   */
  public static int getApproximateLuminance(
    Color color
    )
  {
    return ((color.getRed()   >> 2) +
            (color.getGreen() >> 1) +
            (color.getBlue()  >> 2));
  }

  /**
   * Given a base color, returns a new shade given a base color and an
   * amount to change.  In order to avoid problems caused by clipping the
   * colors at either end of the luminosity range, shadeColor() uses
   * the base color's luminosity to determine how to modify the change amount
   * passed in.
   * <p>
   * @param baseColor The color to return the new shade of
   * @param change    The proposed amount to change each component of the
   * baseColor.  Depending on the baseColor's luminance, the actual
   * amount the baseColor's components are changed may differ.  The valid range
   * for this value is -255 to 255.
   */
  public static Color shadeColor(
    Color   baseColor, // base color for light and dark colors
    int     change     // amount to change base color
    )
  {
    // make sure that change is within the specified range
    if ((change < -255) || (change > 255))
    {
      throw new IllegalArgumentException();
    }

    // get the approximate luminance of the baseColor for determining
    // how to modify change
    int luminosity = getApproximateLuminance(baseColor);

    // The actual amount we will change each component's value
    int realChange;

    //
    // Dertermine the real amount to change each component depending on
    // the luminance of the base color and whether we're making the color
    // lighter or darker
    //
    if (change > 0)
    {
      //
      // handle case where we're making color lighter
      //
      if (luminosity > _LIGHT_LUMINANCE)
      {
        // base color is a light color, so the "lighter shade" is -50 hange
        // darker than the base color
        realChange = -change >> 1;
      }
      else
      {
        if (luminosity < _DARK_LUMINANCE)
        {
          // base color is a dark color, so the "lighter" shade is +125 0ghter
          // than the base color
          realChange = change + (change >> 2);
        }
        else
        {
          // base color is a medium color, so the "lighter" shade is change
          // lighter than the base color
          realChange = change;
        }
      }
    }
    else
    {
      //
      // handle case where we're making color darker
      //
      if (luminosity < _DARK_LUMINANCE)
      {
        // base color is a dark color, so the "darker" shade is +75 hange
        // lighter than the base color
        realChange  = -change >> 1;
        realChange += realChange >> 1;
      }
      else
      {
        // base color is a medium or light color, so the "darker" shade is
        // change darker than the base color
        realChange = change;
      }
    }

    //
    // calculate and return the new shade
    //
    if (realChange < 0)
    {
      // since we're making each component darker, use an inline method that
      // only checks for underflows
      return new Color(_darkenComponent(baseColor.getRed(),   realChange),
                       _darkenComponent(baseColor.getGreen(), realChange),
                       _darkenComponent(baseColor.getBlue(),  realChange));
    }
    else
    {
      // since we're making each component lighter, use an inline method that
      // only checks for overflows
      return new Color(_lightenComponent(baseColor.getRed(),   realChange),
                       _lightenComponent(baseColor.getGreen(), realChange),
                       _lightenComponent(baseColor.getBlue(),  realChange));
    }
  }


  //
  // Returns the baseComponent changed by change, clipping at the bottom if
  // necessary.
  //
  private static final int _darkenComponent(
    int baseComponent, // base component to change
    int change         // amount to change component
    )
  {
    assert (change <= 0);

    // apply change to component
    int newComponent = baseComponent + change;

    // make sure that the new component is between 0 (no saturation) and
    // 255 (fully saturated), clipping at the low end if it is outside the
    // range
    if (newComponent < 0)
    {
      newComponent = 0;
    }

    return newComponent;
  }


  //
  // Returns the baseComponent changed by change, clipping at the top if
  // necessary
  //
  private static final int _lightenComponent(
    int baseComponent, // base component to change
    int change         // amount to change component
    )
  {
    assert (change >= 0);

    // apply change to component
    int newComponent = baseComponent + change;

    // make sure that the new component is between 0 (no saturation) and
    // 255 (fully saturated), clipping at the high end if it is outside the
    // range
    if (newComponent > 255)
    {
      newComponent = 255;
    }

    return newComponent;
  }

  // Luminance value, above which the color is considered "very light"
  private static final int _LIGHT_LUMINANCE = 229; // 900minance

  // Luminance value, below which the color is considered "very dark"
  private static final int _DARK_LUMINANCE  =  51; // 200minance
}
