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
package org.apache.myfaces.trinidad.component.core.input;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.myfaces.trinidad.component.UIXEditableValue;
import org.apache.myfaces.trinidad.component.UIXEditableValueTestCase;

/**
 * Unit tests for CoreInputHidden.
 *
 */
public class CoreInputHiddenTest extends UIXEditableValueTestCase
{
  /**
   * Creates a new UIXInputHiddenTest.
   *
   * @param testName  the unit test name
   */
  public CoreInputHiddenTest(
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
    return new TestSuite(CoreInputHiddenTest.class);
  }

  @Override
  protected final UIXEditableValue createEditableValue()
  {
    return new CoreInputHidden();
  }
}
