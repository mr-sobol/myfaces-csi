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
package org.apache.myfaces.trinidadinternal.renderkit.core.xhtml;

import javax.el.ValueExpression;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;

import org.apache.myfaces.trinidadinternal.convert.ConverterUtils;

abstract public class ValueRenderer extends XhtmlRenderer
{
  protected ValueRenderer(FacesBean.Type type)
  {
    super(type);
  }

  @Override
  protected void findTypeConstants(FacesBean.Type type)
  {
    super.findTypeConstants(type);
    _converterKey = type.findKey("converter");
    _valueKey = type.findKey("value");
  }

  protected String getConvertedString(
    FacesContext context,
    UIComponent  component,
    FacesBean    bean)
  {
    Object value = getValue(bean);
    Converter converter = getConverter(bean);
    // If there's no explicitly set converter, and the value is non-null
    // and not a String, try to get a default converter
    if ((converter == null) &&
        (value != null) &&
        !(value instanceof String))
      converter = getDefaultConverter(context, bean);

    if (converter != null)
    {
      return converter.getAsString(context, component, value);
    }

    return toString(value);
  }


  protected Converter getDefaultConverter(
    FacesContext context,
    FacesBean    bean)
  {
    ValueExpression expression = getValueExpression(bean);
    if (expression == null)
      return null;

    Class<?> type = expression.getType(context.getELContext());
    return ConverterUtils.createConverter(context, type);
  }

  protected Object getValue(FacesBean bean)
  {
    return bean.getProperty(_valueKey);
  }

  /**
   * Returns the ValueExpression for the "value" property.
   */
  protected ValueExpression getValueExpression(FacesBean bean)
  {
    return bean.getValueExpression(_valueKey);
  }

  protected Converter getConverter(FacesBean bean)
  {
    return (Converter) bean.getProperty(_converterKey);
  }

  private PropertyKey _valueKey;
  private PropertyKey _converterKey;
}
