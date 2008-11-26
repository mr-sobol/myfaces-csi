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

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.component.core.input.CoreSelectManyListbox;


public class SelectManyListboxRenderer extends InputLabelAndMessageRenderer
{

  public SelectManyListboxRenderer()
  {
    super(CoreSelectManyListbox.TYPE);
  }  
  
  protected SelectManyListboxRenderer(FacesBean.Type type)
  {
    super(type);
  }
  
  @Override
  protected void findTypeConstants(FacesBean.Type type)
  {
    super.findTypeConstants(type);
    _simpleSelectManyListbox = new SimpleSelectManyListboxRenderer(type);
  }

  @Override
  protected String getRootStyleClass(FacesBean bean)  
  {
    return "af|selectManyListbox";
  }
  
  @Override
  protected String getDefaultLabelValign(FacesBean bean)
  {
    return "top";
  }

  @Override
  protected FormInputRenderer getFormInputRenderer()
  {
    return _simpleSelectManyListbox;
  }  

  private SimpleSelectManyListboxRenderer _simpleSelectManyListbox;
}
