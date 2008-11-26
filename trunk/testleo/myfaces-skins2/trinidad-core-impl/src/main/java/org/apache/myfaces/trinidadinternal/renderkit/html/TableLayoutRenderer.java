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
package org.apache.myfaces.trinidadinternal.renderkit.html;

import java.io.IOException;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.component.html.HtmlTableLayout;

import org.apache.myfaces.trinidad.context.RenderingContext;

import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.OutputUtils;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.XhtmlRenderer;

public class TableLayoutRenderer extends XhtmlRenderer
{
  public TableLayoutRenderer()
  {
    super(HtmlTableLayout.TYPE);
  }
  
  @Override
  protected void findTypeConstants(FacesBean.Type type)
  {
    super.findTypeConstants(type);
    _widthKey = type.findKey("width");
    _halignKey = type.findKey("halign");
    _cellSpacingKey = type.findKey("cellSpacing");
    _cellPaddingKey = type.findKey("cellPadding");
    _borderWidthKey = type.findKey("borderWidth");
    _summaryKey = type.findKey("summary");
  }

  @Override
  public boolean getRendersChildren()
  {
    return true;
  }

  @Override
  protected void encodeAll(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         component,
    FacesBean           bean) throws IOException
  {
    ResponseWriter rw = context.getResponseWriter();
    rw.startElement("table", component);
    renderId(context, component);
    renderAllAttributes(context, arc, bean);
    renderHAlign(context, arc, getHalign(bean));
    // TODO: if TABLES_CAP_ADVANCED and TABLES_CAP_ADVANCED_ATTRS
    // are both false, don't render cell padding, cell spacing, or border
    OutputUtils.renderLayoutTableAttributes(context,
                                            arc,
                                            getCellPadding(bean),
                                            getCellSpacing(bean),
                                            getBorderWidth(bean),
                                            getWidth(bean),
                                            getSummary(bean));



    encodeAllChildren(context, component);

    rw.endElement("table");
  }

  protected Object getWidth(FacesBean bean)
  {
    return bean.getProperty(_widthKey);
  }

  protected Object getHalign(FacesBean bean)
  {
    return bean.getProperty(_halignKey);
  }

  protected Object getCellSpacing(FacesBean bean)
  {
    Object o = bean.getProperty(_cellSpacingKey);
    if (o == null)
      o = 0;
    return o;
  }

  protected Object getCellPadding(FacesBean bean)
  {
    Object o = bean.getProperty(_cellPaddingKey);
    if (o == null)
      o = 0;
    return o;
  }

  protected Object getBorderWidth(FacesBean bean)
  {
    Object o = bean.getProperty(_borderWidthKey);
    if (o == null)
      o = 0;
    return o;
  }

  protected Object getSummary(FacesBean bean)
  {
    Object o = bean.getProperty(_summaryKey);
    // Because table layout is for layout, default to an empty
    // string for summary
    if (o == null)
      o = "";
    return o;
  }


  private PropertyKey _widthKey;
  private PropertyKey _halignKey;
  private PropertyKey _cellSpacingKey;
  private PropertyKey _cellPaddingKey;
  private PropertyKey _borderWidthKey;
  private PropertyKey _summaryKey;
}
