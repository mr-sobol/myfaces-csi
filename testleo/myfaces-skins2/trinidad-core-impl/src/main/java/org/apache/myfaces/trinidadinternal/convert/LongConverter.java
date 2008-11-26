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
package org.apache.myfaces.trinidadinternal.convert;

import java.util.Collection;
import java.util.Collections;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.convert.ConverterException;

import org.apache.myfaces.trinidad.convert.ClientConverter;
import org.apache.myfaces.trinidad.util.IntegerUtils;


/**
 * <p>Implementation for <code>java.lang.Long</code> values.</p>
 *
 */
public class LongConverter extends javax.faces.convert.LongConverter
                           implements ClientConverter
{

    /**
     * <p>The message identifier of the FacesMessage to be created if
     * the value is greater than Long.MAX_VALUE.
     * The message format string for this
     * message may optionally include a <code>{2}</code> placeholder, which
     * will be replaced by Long.MAX_VALUE.</p>
     */
    public static final String MAXIMUM_MESSAGE_ID =
        "org.apache.myfaces.trinidad.convert.LongConverter.MAXIMUM";

    /**
     * <p>The message identifier of the FacesMessage to be created if
     * the value is less than Long.MIN_VALUE.
     * The message format string for this
     * message may optionally include a <code>{2}</code> placeholder, which
     * will be replaced by Long.MIN_VALUE.</p>
     */
    public static final String MINIMUM_MESSAGE_ID =
        "org.apache.myfaces.trinidad.convert.LongConverter.MINIMUM";

    /**
     * <p>The message identifier of the FacesMessage to be created if
     * the value cannot be converted to an integer
     */
    public static final String CONVERT_MESSAGE_ID =
        "org.apache.myfaces.trinidad.convert.LongConverter.CONVERT";


  @Override
  public Object getAsObject(
    FacesContext context, 
    UIComponent component,
    String value) 
  {
    try
    {
      return super.getAsObject(context, component, value);
    }
    catch(ConverterException ce)
    {
    
      throw ConverterUtils.getIntegerConverterException(context, 
                                                        component, 
                                                        ce,
                                                        value,
                                                        CONVERT_MESSAGE_ID,
                                                        MAXIMUM_MESSAGE_ID,
                                                        _LONG_MAX,
                                                        MINIMUM_MESSAGE_ID,
                                                        _LONG_MIN);
    }     
    
  }


  public String getClientScript(
   FacesContext context,
   UIComponent component)
  {
    return null;
  }
  
  public String getClientLibrarySource(
   FacesContext context)
  {
    return null;
  }

 /**
   * @param context
   * @return
   */
  public String getClientConversion(
    FacesContext context,
    UIComponent component)
  {
    return _getTrLongConverter(context, component, _LONG_MAX, _LONG_MIN);
  }


  public Collection<String> getClientImportNames()
  {
    return _IMPORT_NAMES;
  }

  private static String _getTrLongConverter(
      FacesContext context,
      UIComponent component,
      String maxVal,
      String minVal)
    {
      StringBuilder outBuffer = new StringBuilder(250);

      outBuffer.append("new TrLongConverter(");

      outBuffer.append("null,null,0,");
      outBuffer.append(maxVal);
      outBuffer.append(',');
      outBuffer.append(minVal);
      outBuffer.append(")");

      return outBuffer.toString();
    }

  private static final String _LONG_MIN
    = IntegerUtils.getString(Long.MIN_VALUE);
  private static final String _LONG_MAX
    = IntegerUtils.getString(Long.MAX_VALUE);
  private static final Collection<String> _IMPORT_NAMES = Collections.singletonList( "TrNumberConverter()" );
}
