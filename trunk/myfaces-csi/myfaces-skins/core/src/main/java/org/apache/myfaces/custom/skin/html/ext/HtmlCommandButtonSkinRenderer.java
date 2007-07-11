package org.apache.myfaces.custom.skin.html.ext;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.myfaces.custom.skin.AdapterSkinRenderer;
import org.apache.myfaces.trinidad.context.RenderingContext;

public class HtmlCommandButtonSkinRenderer extends AdapterSkinRenderer {

	public HtmlCommandButtonSkinRenderer() {
		super("t", "commandButton");
	}

	@Override
	protected void _addStyleClassesToComponent(FacesContext context,
			UIComponent component, RenderingContext arc) throws IOException {
		_addStyleDisabledReadOnly(context, component, arc);
	}	
}