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
package org.apache.myfaces.trinidadinternal.ui.laf.base.xhtml;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

import org.apache.myfaces.trinidadinternal.ui.UIXRenderingContext;
import org.apache.myfaces.trinidadinternal.ui.UINode;


/**
 * @version $Name:  $ ($Revision$) $Date$
 * @deprecated This class comes from the old Java 1.2 UIX codebase and should not be used anymore.
 */
@Deprecated
public class StackLayoutRenderer extends XhtmlLafRenderer
{  
  @Override
  protected void renderBetweenIndexedChildren(
    UIXRenderingContext context,
    UINode           node,
    int              nextIndex
    ) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();

    // get the child about to be rendered
    UINode nextChild = node.getIndexedChild( context, nextIndex);


    // if the next child about to be rendered is not visible to the user
    // then don't render anything
    // =-=AEW This doesn't catch formValues inside of contextPopping nodes
    if (!nextChild.getNodeRole(context).satisfiesRole(
                                      USER_INVISIBLE_ROLE))
    {
      writer.startElement("div", null);

      UINode separatorChild = getNamedChild(context, node, SEPARATOR_CHILD);

      if (separatorChild != null)
      {
        renderNamedChild(context, node, separatorChild, SEPARATOR_CHILD);
      }

      writer.endElement("div");
    }
  }

  @Override
  protected String getElementName(
    UIXRenderingContext context,
    UINode           node
    )
  {
    return "span";
  }
}
