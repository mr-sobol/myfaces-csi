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
package org.apache.myfaces.trinidadinternal.taglib.listener;

import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentClassicTagBase;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.myfaces.trinidad.logging.TrinidadLogger;

/**
 *
 */
public class ResetActionListenerTag extends TagSupport
{
  @Override
  public int doStartTag() throws JspException
  {
    UIComponentClassicTagBase tag = UIComponentClassicTagBase.getParentUIComponentClassicTagBase(pageContext);
    if (tag == null)
    {
      throw new JspException(_LOG.getMessage(
        "RESETACTIONLISTENER_MUST_INSIDE_UICOMPONENT_TAG"));
    }

    // Only run on the first time the tag executes
    if (!tag.getCreated())
      return SKIP_BODY;

    UIComponent component = tag.getComponentInstance();
    if (!(component instanceof ActionSource))
    {
      throw new JspException(_LOG.getMessage(
        "RESETACTIONlISTENER_MUST_INSIDE_UICOMPONENT_TAG"));
    }

    ResetActionListener listener = new ResetActionListener();

    ((ActionSource) component).addActionListener(listener);

    return super.doStartTag();
  }

  @Override
  public void release()
  {
    super.release();
  }
  private static final TrinidadLogger _LOG = TrinidadLogger.createTrinidadLogger(
    ResetActionListenerTag.class);
}
