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
package org.apache.myfaces.trinidadinternal.uinode;

import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.data.BoundValue;

class ValueExpressionBoundValue implements BoundValue
{
  public ValueExpressionBoundValue(ValueExpression expression)
  {
    if (expression == null)
      throw new NullPointerException();

    _expression = expression;
  }

  /**
   * @todo Better way to retrieve FacesContext
   */
  public Object getValue(UIXRenderingContext rContext)
  {
    FacesContext fContext = FacesContext.getCurrentInstance();
    return _expression.getValue(fContext.getELContext());
  }

  private final ValueExpression _expression;
}
