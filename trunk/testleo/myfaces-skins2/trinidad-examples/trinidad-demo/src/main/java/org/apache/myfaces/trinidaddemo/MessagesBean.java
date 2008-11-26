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
package org.apache.myfaces.trinidaddemo;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessagesBean implements java.io.Serializable
{
  public String createGlobalMessage()
  {
    FacesContext context = FacesContext.getCurrentInstance();

    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_FATAL,
        "Fatal Msg.", "Programatically generated at " + new Date());
    
    context.addMessage(null, msg);

    return null;
  }

  public String createMultipleGlobalMessages()
  {
    FacesContext context = FacesContext.getCurrentInstance();

    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN,
        "Warning Msg.", "Programatically generated at " + new Date());

    context.addMessage(null, msg);
    
    msg = new FacesMessage(FacesMessage.SEVERITY_INFO,
        "Info Msg.", "Programatically generated at " + new Date());

    context.addMessage(null, msg);

    return null;
  }

}
