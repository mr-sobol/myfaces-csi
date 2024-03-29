package org.apache.myfaces.custom.skin;

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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlMessage;
import javax.faces.component.html.HtmlMessages;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlSelectManyCheckbox;
import javax.faces.component.html.HtmlSelectManyListbox;
import javax.faces.component.html.HtmlSelectManyMenu;
import javax.faces.component.html.HtmlSelectOneListbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.trinidad.component.UIXComponent;
import org.apache.myfaces.trinidad.context.RenderingContext;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.OutputUtils;
import org.apache.myfaces.trinidadinternal.renderkit.core.xhtml.SkinSelectors;
import org.apache.myfaces.component.html.ext.HtmlGraphicImage;

/**
 * This class encapsulate Renderers of other RenderKits and add trinidad
 * skinning features. It implements a Decorator pattern style
 * 
 * @author Leonardo Uribe
 */
public class GenericSkinRenderer extends SkinRenderer {

	/**
	 * The log factory used to debug messages
	 */
	private static final Log log = LogFactory.getLog(GenericSkinRenderer.class);

	/**
	 * Constructor
	 * 
	 * Save its delegate renderer
	 * 
	 * @param delegate
	 */
	public GenericSkinRenderer(Renderer delegate) {
		super(delegate);
	}
	
	public GenericSkinRenderer(){		
	}

	@Override
	public void addStyleClassesToComponent(FacesContext context, 
			UIComponent component, RenderingContext arc)
			throws IOException {

		// The task here is first check if the component is appropiate to
		// skinning (not inherit from UIXComponent) and has a styleClass
		// property
		// to do it.

		log.info("SkinRenderer:" + component.toString() + " "
				+ this._delegate.toString());

		if (UIXComponent.class.isAssignableFrom(component.getClass())) {
			// Nothing because is a trinidad component.
		} else {
			// Check if is a basic component
			if ("javax.faces.component.html".equals(component.getClass()
					.getPackage().getName())) {
				// 1. get a Rendering Context
				/*
				RenderingContext arc = RenderingContext.getCurrentInstance();
				if (arc == null)
					throw new IllegalStateException(("NO_RENDERINGCONTEXT"));
				*/
				/*
				if (HtmlMessage.class.isAssignableFrom(component.getClass())) {
					this.encodeHtmlMessage(context, component, arc);
				} else if (HtmlMessages.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlMessage(context, component, arc);
				} else if (HtmlSelectOneListbox.class
						.isAssignableFrom(component.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (HtmlSelectOneMenu.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (HtmlSelectOneRadio.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (HtmlSelectManyCheckbox.class
						.isAssignableFrom(component.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (HtmlSelectManyListbox.class
						.isAssignableFrom(component.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (HtmlSelectManyMenu.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else 
				
				if (HtmlPanelGrid.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlPanelGrid(context, component, arc);
				} else if (HtmlDataTable.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlDataTable(context, component, arc);
				} else
				*/	
				 {
					this.encodeGenericComponent(context, component, arc);
				}
				// TODO: add dataTable
				// TODO: column en JSF 1.2 has another properties
			}
			// Check if is a tomahawk component
			if ("org.apache.myfaces.component.html.ext".equals(component.getClass()
					.getPackage().getName())) {
				//RenderingContext arc = RenderingContext.getCurrentInstance();
				//if (arc == null)
				//	throw new IllegalStateException(("NO_RENDERINGCONTEXT"));
				/*
				if (org.apache.myfaces.component.html.ext.HtmlMessage.class.isAssignableFrom(component.getClass())) {
					this.encodeHtmlMessage(context, component, arc);
				} else if (HtmlMessages.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlMessage(context, component, arc);
				} else 
				
				if (org.apache.myfaces.component.html.ext.HtmlSelectOneListbox.class
						.isAssignableFrom(component.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (org.apache.myfaces.component.html.ext.HtmlSelectOneMenu.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (org.apache.myfaces.component.html.ext.HtmlSelectOneRadio.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (org.apache.myfaces.component.html.ext.HtmlSelectManyCheckbox.class
						.isAssignableFrom(component.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (org.apache.myfaces.component.html.ext.HtmlSelectManyListbox.class
						.isAssignableFrom(component.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else if (org.apache.myfaces.component.html.ext.HtmlSelectManyMenu.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlSelectOneOrMany(context, component, arc);
				} else
				
				 if (org.apache.myfaces.component.html.ext.HtmlPanelGrid.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlPanelGrid(context, component, arc);
				} else if (org.apache.myfaces.component.html.ext.HtmlDataTable.class.isAssignableFrom(component
						.getClass())) {
					// the same that previous
					this.encodeHtmlDataTable(context, component, arc);
				} else 
				*/{
					this.encodeGenericComponent(context, component, arc);
				}
			}						
		}
	}




	public static List<String> parseStyleClassListComma(String styleClass) {
		if (styleClass == null)
			return null;

		// If there's no spaces, it's just a single class - return
		// AdamWiner: should we care about all Unicode whitspace?
		// This will catch 99.9% of cases, and this code needs to be
		// fast
		int spaceIndex = styleClass.indexOf(',');
		if (spaceIndex < 0)
			return null;

		// Iterate through the string and build up the split list
		// AdamWiner: Regex split() would be a lot less code, but
		// it doesn't automatically trim empty strings.
		int prevSpaceIndex = 0;
		List<String> styleClasses = new ArrayList<String>();
		do {
			if (spaceIndex > prevSpaceIndex)
				styleClasses.add(styleClass.substring(prevSpaceIndex,
						spaceIndex));
			prevSpaceIndex = spaceIndex + 1;
			spaceIndex = styleClass.indexOf(',', prevSpaceIndex);
		} while (spaceIndex >= 0);

		if (prevSpaceIndex < styleClass.length())
			styleClasses.add(styleClass.substring(prevSpaceIndex));

		return styleClasses;
	}

	/*
	 * This method look if the component has 3 common methods:
	 * 
	 * 1. getStyleClass
	 * 2. isReadonly
	 * 3. isDisabled
	 * 
	 * And associate to properly css classes
	 * 
	 */
	public void encodeGenericComponent(FacesContext context,
			UIComponent component, RenderingContext arc) throws IOException {

		log.info("Component class" + component.getClass().getName());

		// 2. the skin class for this component looks like this:
		// af|javax_faces_component_html_HtmlXXX::class

		String contentStyleClass = component.getClass().getName();

		Map<String, String> m = arc.getSkin().getStyleClassMap(arc);

		String baseStyleClass = "af|"
				+ StringUtils.replaceChars(contentStyleClass, '.', '_');

		Method method;
		// Check it has a getStyleClass property
		contentStyleClass = null;
		try {
			method = component.getClass().getMethod("getStyleClass",
					(Class[]) null);
			contentStyleClass = baseStyleClass + "::class";
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			// Nothing happends
			// e.printStackTrace();
		}

		int otherStyles = 0;

		// Its necesary to add other style properties like
		// p_AFReadOnly and p_AFDisabled
		Map attributes = component.getAttributes();
		String styleClass = (String) attributes.get("styleClass");
		String disabledStyleClass = null;
		String readOnlyStyleClass = null;

		try {
			method = component.getClass().getMethod("isReadonly",
					(Class[]) null);
			if ((Boolean) method.invoke(component, (Object[]) null)) {
				readOnlyStyleClass = SkinSelectors.STATE_READ_ONLY;
				otherStyles += 1;
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			// Nothing happends
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// Nothing happends
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// Nothing happends
			e.printStackTrace();
		}

		try {
			method = component.getClass().getMethod("isDisabled",
					(Class[]) null);
			if ((Boolean) method.invoke(component, (Object[]) null)) {
				disabledStyleClass = SkinSelectors.STATE_DISABLED;
				otherStyles += 1;
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			// Nothing happends
			// e.printStackTrace();
		} catch (InvocationTargetException e) {
			// Nothing happends
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// Nothing happends
			e.printStackTrace();
		}

		List<String> parsedStyleClasses = OutputUtils
				.parseStyleClassList(styleClass);
		int userStyleClassCount;
		if (parsedStyleClasses == null)
			userStyleClassCount = (styleClass == null) ? 0 : 1;
		else
			userStyleClassCount = parsedStyleClasses.size();

		String[] styleClasses = new String[userStyleClassCount + 3];
		int i = 0;
		if (parsedStyleClasses != null) {
			while (i < userStyleClassCount) {
				styleClasses[i] = parsedStyleClasses.get(i);
				i++;
			}
		} else if (styleClass != null) {
			styleClasses[i++] = styleClass;
		}

		styleClasses[i++] = contentStyleClass;
		styleClasses[i++] = disabledStyleClass;
		styleClasses[i++] = readOnlyStyleClass;

		// 3. set the property styleClass, setting it.
		if (otherStyles > 0) {
			renderStyleClasses(component, context, arc, styleClasses);
		} else {
			renderStyleClass(component, context, arc, contentStyleClass);
		}
	}
}
