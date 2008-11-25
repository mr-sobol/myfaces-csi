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
package org.apache.myfaces.trinidad.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesListener;

import org.apache.myfaces.trinidad.model.RowKeySet;


/**
 * Event that is generated when nodes are expanded or collapsed in a tree
 * component.
 */
public class RowDisclosureEvent extends RowKeySetChangeEvent
{
  /**
   * Creates a new ExpansionEvent
   * @param collapsed the set of rowKeys that have just been collapsed.
   * @param expanded the set of rowKeys that have just been expanded.
   */
  public RowDisclosureEvent(
      UIComponent source, 
      RowKeySet   collapsed, 
      RowKeySet   expanded)
  {
    super(source, collapsed, expanded);
  }

  /**
   * Creates a new ExpansionEvent
   * @param oldSet the set of rowKeys before any changes.
   * @param newSet the set of rowKeys after any changes.
   */
  public RowDisclosureEvent(
      RowKeySet   oldSet, 
      RowKeySet   newSet, 
      UIComponent source)
  {
    super(oldSet, newSet, source);
  }

  @Override
  public void processListener(FacesListener listener)
  {
    ((RowDisclosureListener) listener).processDisclosure(this);
  }

  @Override
  public boolean isAppropriateListener(FacesListener listener)
  {
    return (listener instanceof RowDisclosureListener);
  }
  
  private static final long serialVersionUID = 1L;
}
