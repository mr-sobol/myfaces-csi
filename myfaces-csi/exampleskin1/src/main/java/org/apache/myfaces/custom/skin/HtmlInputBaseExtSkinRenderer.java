package org.apache.myfaces.custom.skin;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.trinidad.context.RenderingContext;

public class HtmlInputBaseExtSkinRenderer extends GenericSkinRenderer {

	/**
	 * The log factory used to debug messages
	 */
	private static final Log log = LogFactory.getLog(HtmlInputBaseExtSkinRenderer.class);	
		
	@Override
	public void addStyleClassesToComponent(FacesContext context,
			UIComponent component, RenderingContext arc) throws IOException {
		// TODO Auto-generated method stub
		this.encodeHtmlInputBaseExt(context, component, arc);
	}

	/**
	 * Apply the following css class style attributes:
	 * 
	 * disabledClass
	 * displayValueOnlyStyleClass
	 * enabledClass
	 * styleClass
	 * 
	 * @param context
	 * @param component
	 * @param arc
	 * @throws IOException
	 */
	public void encodeHtmlInputBaseExt(FacesContext context,
			UIComponent component, RenderingContext arc) throws IOException {

		this.encodeGenericComponent(context, component, arc);

		String baseStyleClass = "af|"
				+ StringUtils.replaceChars(component.getClass().getName(), '.',
						'_');

		String displayValueOnlyStyleClass = baseStyleClass + "::displayValueOnly";		

		renderStyleClass(component, context, arc, displayValueOnlyStyleClass,
				"displayValueOnlyStyleClass");		
	}	
}
