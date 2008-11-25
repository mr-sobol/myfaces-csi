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
package org.apache.myfaces.trinidad.component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * ProcessingContext passed to FlattenedComponents and ComponentProcessors representing the
 * current component iteration context.
 * @see ComponentProcessor
 * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessor, UIComponent, Object)
 * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessor, Iterable, Object)
 * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessingContext, ComponentProcessor, UIComponent, Object)
 * @see UIXComponent#processFlattenedChildren(FacesContext, ComponentProcessingContext, ComponentProcessor, Iterable, Object)
 * @see FlattenedComponent
 */
public final class ComponentProcessingContext
{
  ComponentProcessingContext()
  {      
  }
  
  /**
   * Returns the current starting group depth of the ProcessingContext.  The starting depth is only
   * non-zero for the first rendered child inside a group or nested groups.  If two grouping
   * components, such as UIXGroup, are nested immediately inside of each other, the first processed
   * component in the second UIXGroup will see 2 for the start depth.  The second would see 0.
   * @see #getGroupDepth
   */
  public int getStartDepth()
  {
    return _startDepth;
  }
  
  /**
   * Returns the current group depth of the ProcessingContext.  The group depth is equal to the
   * nesting depth of grouping components, such as UIXGroup that the current iteratior has
   * entered.  In contrast to <code>getStartDepth()</code>, all siblings at a particular nesting
   * level see the same group depth.
   * @see #getStartDepth
   */
  public int getGroupDepth()
  {
    return _groupDepth;
  }
  
  /**
   * Increment the grouping and startGroup states.
   * <p>
   * If pushGroup is called, the seubsequent code should be
   * wrapped in a <code>try{}finally{ComponentProcessingContext.popGroup()} block to guarantee
   * that the group is popped correctly in case an exception is thrown.
   * @see #popGroup
   */
  void pushGroup()
  {
    _startDepth++;
    _groupDepth++;
  }
  
  /**
   * Decrement the grouping and startGroup states.
   * <p>
   * If pushGroup is called, the seubsequent code should be
   * wrapped in a <code>try{}finally{ComponentProcessingContext.popGroup()} block to guarantee
   * that the group is popped correctly in case an exception is thrown.
   * @see #pushGroup
   */
  void popGroup()
  {
    _groupDepth--;
    
    if (_startDepth > 0)
      _startDepth--;
  }
  
  /**
   * Called by the object performing the iteration over Component instances to 
   * reset the start depth after the ChildProcessor is called on the first component at the
   * new depth.  When iterating, the iterator should place the call to <code>resetStartDepth</code>
   * in a <code>finally</code> block to guarantee that the reset happens correctly in case
   * an exception is thrown.
   */
  void resetStartDepth()
  {
    _startDepth = 0;
  }
  
  private int _startDepth;
  private int _groupDepth;
}
