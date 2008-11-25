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
package org.apache.myfaces.trinidad.component.core.layout;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.myfaces.trinidad.component.UIXPanel;

/**
 * Unit tests for CorePanelList.
 *
 */
public class CorePanelListTest extends UIXPanelTestCase
{
  /**
   * Creates a new CorePanelListTest.
   *
   * @param testName  the unit test name
   */
  public CorePanelListTest(
    String testName)
  {
    super(testName);
  }
  
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  public static Test suite()
  {
    return new TestSuite(CorePanelListTest.class);
  }

  /**
   * Tests the initial values for the component attributes.
   */
  public void testInitialAttributeValues()
  {
    CorePanelList component = new CorePanelList();
    assertTrue(component.isRendered());
    assertEquals(Integer.MAX_VALUE, component.getRows());
    assertEquals(3, component.getMaxColumns());
  }

  /**
   * Tests the transparency of the component attribute by comparing
   * bean accessor and mutator methods with attribute map accessor
   * and mutator methods.
   */
  public void testAttributeTransparency()
  {
    CorePanelList component = new CorePanelList();

    doTestAttributeTransparency(component, "rendered",
                                Boolean.TRUE, Boolean.FALSE);
    doTestAttributeTransparency(component, "rows",
                                new Integer(1), new Integer(2));
                                
    doTestAttributeTransparency(component, "maxColumns",
                              new Integer(1), new Integer(2));                   
  }

  @Override
  protected UIXPanel createTestComponent()
  {
      return new CorePanelList();
  }
}
