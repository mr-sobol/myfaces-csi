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
package org.apache.myfaces.trinidadinternal.renderkit.core.xhtml;

import java.io.IOException;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;

import org.apache.myfaces.trinidad.logging.TrinidadLogger;

import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.render.CoreRenderer;
import org.apache.myfaces.trinidad.render.TypedRenderer;

import org.apache.myfaces.trinidadinternal.agent.TrinidadAgent;
import org.apache.myfaces.trinidad.context.PartialPageContext;
import org.apache.myfaces.trinidad.context.RenderingContext;
import org.apache.myfaces.trinidadinternal.util.FormattedTextParser;
import org.apache.myfaces.trinidadinternal.webapp.TrinidadFilterImpl;

/**
 *  @todo Move "supportsStyleAttributes()", etc., architecture
 */
public class XhtmlRenderer extends CoreRenderer
                           implements TypedRenderer, Cloneable
{
  public static final String TRANSPARENT_GIF = "t.gif";

  protected XhtmlRenderer(FacesBean.Type type)
  {
    findTypeConstants(type);
  }

  /**
   * Clone a Renderer instance with a new Type.
   */
  public Renderer cloneWithType(FacesBean.Type type)
  {
    try
    {
      XhtmlRenderer that = (XhtmlRenderer) clone();
      that.findTypeConstants(type);
      return that;
    }
    catch (CloneNotSupportedException cnse)
    {
      _LOG.severe(cnse);
      return null;
    }
  }

  protected void findTypeConstants(FacesBean.Type type)
  {
    _shortDescKey = type.findKey("shortDesc");
    _styleClassKey = type.findKey("styleClass");
    _inlineStyleKey = type.findKey("inlineStyle");
    _onclickKey = type.findKey("onclick");
    _ondblclickKey = type.findKey("ondblclick");
    _onkeydownKey = type.findKey("onkeydown");
    _onkeyupKey = type.findKey("onkeyup");
    _onkeypressKey = type.findKey("onkeypress");
    _onmousedownKey = type.findKey("onmousedown");
    _onmousemoveKey = type.findKey("onmousemove");
    _onmouseoutKey = type.findKey("onmouseout");
    _onmouseoverKey = type.findKey("onmouseover");
    _onmouseupKey = type.findKey("onmouseup");

    _partialTriggersKey = type.findKey("partialTriggers");
  }

  /**
   * Returns true if the agent supports the Script module.
   * <p>
   * See section 5.16 of xhtml modularization
   */
  static public boolean supportsScripting(RenderingContext arc)
  {
    Object scriptingSpeed = arc.getAgent().getCapabilities().get(
            TrinidadAgent.CAP_SCRIPTING_SPEED);

    return ((scriptingSpeed != null) &&
            (TrinidadAgent.SCRIPTING_SPEED_CAP_NONE != scriptingSpeed));
  }


  static public boolean supportsEditing(RenderingContext arc)
  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_EDITING);
    return !Boolean.FALSE.equals(cap);
  }

  public static boolean supportsAdvancedForms(
    RenderingContext arc)
  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_ADVANCED_FORMS);
    return !Boolean.FALSE.equals(cap);
  }

  /**
   * See section 5.14 of xhtml modularization.
   */
  public static boolean supportsIntrinsicEvents(
    RenderingContext arc)
  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_INTRINSIC_EVENTS);
    return !Boolean.FALSE.equals(cap);
  }

  /**
   * Returns true if the style attributes should be rendered for this node.
   * <p>
   * Clients should override this method if the the user agent
   * doesn't support style attributes.
   * <p>
   * See section 5.18 of xhtml modularization
   */
  public static boolean supportsStyleAttributes(
          RenderingContext arc
          )
  {
    return (arc.getAgent().getCapabilities().get(TrinidadAgent.CAP_STYLE_ATTRIBUTES) !=
            TrinidadAgent.STYLES_NONE);
  }

  static public boolean supportsNavigation(RenderingContext arc)
  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_NAVIGATION);
    return !Boolean.FALSE.equals(cap);
  }

  /**
   * Returns true if the agent supports the text presentation module.
   * <p>
   * See section 5.4.1 of xhtml modularization.
   */
  public static boolean supportsTextPresentation(
          RenderingContext arc
          )
  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_TEXT_PRESENTATION);
    return !Boolean.FALSE.equals(cap);
  }
  static public boolean supportsAccessKeys(RenderingContext arc)
  {
    // In screen reader mode, disable access keys.  Despite
    // the name, they are currently considered an accessibility
    // liability
    if (isScreenReaderMode(arc))
      return false;

    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_ACCESS_KEYS);
    return !Boolean.FALSE.equals(cap);
  }

  static public final boolean supportsDisabledFormElements(RenderingContext arc)
  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_DISABLED_FORM_ELEMENTS);
    return !Boolean.FALSE.equals(cap);

  }

  static public final boolean supportsReadonlyFormElements(RenderingContext arc)
  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_READONLY_FORM_ELEMENTS);
    return !Boolean.FALSE.equals(cap);

  }

  static public final boolean supportsAutoCompleteFormElements(
     RenderingContext arc)
  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_AUTO_COMPLETE_FORM_ELEMENTS);
    return !Boolean.FALSE.equals(cap);
  }

  static public final boolean supportsSeparateWindow(
    RenderingContext arc)
  {
    return XhtmlUtils.supportsSeparateWindow(arc.getAgent());
  }

  /**
   * Returns true if the agent supports setting the target
   * attribute of other elements.
   * <p>
   * See section 5.12 of xhtml modularization.
   */
  static public final boolean supportsTarget(
     RenderingContext arc)

  {
    Object cap = arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_TARGET);
    return !Boolean.FALSE.equals(cap);
  }



  //
  // END OF AGENT CAPABILITY CONVENIENCE METHODS
  //


  @Override
  protected boolean skipDecode(FacesContext context)
  {
    // =-=AEW HACK!  When executing a "dialog return" from the filter,
    // we've generally saved off the original parameters such that
    // decoding again isn't a problem.  But we can run into some problems:
    //  (1) A component that doesn't know about ReturnEvents:  it'll
    //    decode again, thereby firing the event again that launched
    //    the dialog (and you go right back to the dialog)
    //  (2) The component does know about ReturnEvents, but
    //      someone launches a dialog in response to the ReturnEvent,
    //      after setting the value of an input field.  But since
    //      we've still saved off the original parameters,
    //      now we're back in
    // The best fix would really be somehow skipping the Apply Request
    // Values phase altogether, while still queueing the ReturnEvent
    // properly.  But how the heck is that gonna happen?
    return TrinidadFilterImpl.isExecutingDialogReturn(context);
  }


  /**
   * Returns true if the component can skip its own rendering;
   * true if PPR is on for this request, is not currently active,
   * and this component not a target.  Note that if
   * the component is not a leaf, then you'd still have to
   * render the children.
   * <p>
   * Call this overload if you have the clientId already.
   */
  protected boolean canSkipRendering(
    RenderingContext rc,
    String           clientId)
  {
    PartialPageContext ppc = rc.getPartialPageContext();
    if ((ppc == null) ||
        ppc.isInsidePartialTarget() ||
        ppc.isPartialTarget(clientId))
      return false;

    return true;
  }

  /**
   * Returns true if the component can skip its own rendering;
   * true if PPR is on for this request, is not currently active,
   * and this component not a target.  Note that if
   * the component is not a leaf, then you'd still have to
   * render the children.
   * <p>
   * Call this overload if you don't have the clientId already.
   */
  protected boolean canSkipRendering(
    FacesContext     context,
    RenderingContext rc,
    UIComponent      component)
  {
    PartialPageContext ppc = rc.getPartialPageContext();
    if ((ppc == null) ||
        ppc.isInsidePartialTarget())
      return false;

    String clientId = component.getClientId(context);
    if (ppc.isPartialTarget(clientId))
      return false;

    return true;
  }

  /**
   * Returns true if the component should render an ID.  Components
   * that deliver events should always return "true".
   * @todo Profile and possibly optimize.
   */
  @Override
  protected boolean shouldRenderId(
    FacesContext context,
    UIComponent  component)
  {
    // If there's partial triggers, always render an ID if possible
    if (getPartialTriggers(getFacesBean(component)) != null)
    {
      return true;
    }

    return super.shouldRenderId(context, component);
  }

  /**
   * Render the main renderer-specific attributes:  "title", "class", "style",
   * and all the Javascript attributes. This will render style attributes.
   * @todo Since this is non-final, it can become difficult to
   * re-divide its functionality in a subclass.  Make it final???
   */
  protected void renderAllAttributes(
    FacesContext        context,
    RenderingContext arc,
    FacesBean           bean) throws IOException
  {
    renderAllAttributes(context, arc, bean, true);
  }


  /**
   * Render the main renderer-specific attributes:  "title", "class", "style",
   * and all the Javascript attributes. Takes a boolean to determine if
   * renderStyleAttributes should be called, which renders "class", "style"
   */
  protected void renderAllAttributes(
    FacesContext        context,
    RenderingContext arc,
    FacesBean           bean,
    boolean             renderStyleAttrs) throws IOException
  {
    renderShortDescAttribute(context, arc, bean);
    renderEventHandlers(context, bean);
    if (renderStyleAttrs)
      renderStyleAttributes(context, arc, bean);

  }
  /**
   * Renders the inline style attribute for the specified node
   */
  public static void renderInlineStyleAttribute(
    FacesContext        context,
    RenderingContext arc,
    String              style
    ) throws IOException
  {
    if (style != null)
    {
      if (supportsStyleAttributes(arc))
      {
        ResponseWriter writer = context.getResponseWriter();
        writer.writeAttribute("style", style, null);
      }

    }
  }

  protected void renderInlineStyle(
    FacesContext        context,
    RenderingContext arc,
    FacesBean           bean) throws IOException
  {
    String style = getInlineStyle(bean);
    if (style != null)
    {
      context.getResponseWriter().writeAttribute("style",
                                                 style,
                                                 "inlineStyle");
    }
  }

  protected void renderShortDescAttribute(
    FacesContext        context,
    RenderingContext arc,
    FacesBean           bean) throws IOException
  {
    String shortDesc = getShortDesc(bean);
    if (shortDesc != null)
      context.getResponseWriter().writeAttribute("title",
                                                 shortDesc,
                                                 "shortDesc");
  }

  protected void renderStyleAttributes(
    FacesContext        context,
    RenderingContext arc,
    FacesBean           bean) throws IOException
  {
    renderStyleAttributes(context, arc, bean, getDefaultStyleClass(bean));
  }

  protected String getDefaultStyleClass(FacesBean bean)
  {
    return null;
  }

  /**
   * When there's a default style class pass it in to this method
   */
  protected void renderStyleAttributes(
    FacesContext        context,
    RenderingContext    arc,
    FacesBean           bean,
    String              defaultStyleClass) throws IOException
  {
    String styleClass = getStyleClass(bean);
    List<String> parsedStyleClasses = OutputUtils.parseStyleClassList(styleClass);

    if (defaultStyleClass != null)
    {
      if(styleClass!= null )
      {
        // If we've got both a defaultStyleClass and a styleClass,
        // build up an array containing each - and if the styleClass
        // is really a list of styleClasses, break it apart so it
        // can be compressed correctly
        int styleCount = (parsedStyleClasses == null) ? 1 : parsedStyleClasses.size();
        String[] styleClasses = new String[1 + styleCount];
        if (parsedStyleClasses != null)
        {
          for (int i = 0; i < styleCount; i++)
            styleClasses[i] = parsedStyleClasses.get(i);
        }
        else
        {
          styleClasses[0] = styleClass;
        }

        styleClasses[styleCount] = defaultStyleClass;

        renderStyleClasses(context,
                           arc,
                           styleClasses);
      }
      else
      {
        renderStyleClass(context, arc, defaultStyleClass);
      }
    }
    else if (styleClass != null)
    {
      if (parsedStyleClasses == null)
      {
        styleClass = arc.getStyleClass(styleClass);
        context.getResponseWriter().writeAttribute("class",
                                                   arc.getStyleClass(styleClass),
                                                   "styleClass");
      }
      else
      {
        renderStyleClasses(context,
                           arc,
                           parsedStyleClasses.toArray(
                                   new String[parsedStyleClasses.size()]));
      }
    }

    String style = getInlineStyle(bean);
    if (style != null)
    {
      context.getResponseWriter().writeAttribute("style",
                                                 style,
                                                 "inlineStyle");
    }
  }


  /**
   * Render all the Javascript attributes.
   */
  protected void renderEventHandlers(
    FacesContext context,
    FacesBean    bean) throws IOException
  {
    ResponseWriter rw = context.getResponseWriter();
    rw.writeAttribute("onclick", getOnclick(bean),  "onclick");
    rw.writeAttribute("ondblclick", getOndblclick(bean),  "ondblclick");
    rw.writeAttribute("onkeydown", getOnkeydown(bean),  "onkeydown");
    rw.writeAttribute("onkeyup", getOnkeyup(bean),  "onkeyup");
    rw.writeAttribute("onkeypress", getOnkeypress(bean),  "onkeypress");
    rw.writeAttribute("onmousedown", getOnmousedown(bean),  "onmousedown");
    rw.writeAttribute("onmousemove", getOnmousemove(bean),  "onmousemove");
    rw.writeAttribute("onmouseout", getOnmouseout(bean),  "onmouseout");
    rw.writeAttribute("onmouseover", getOnmouseover(bean),  "onmouseover");
    rw.writeAttribute("onmouseup", getOnmouseup(bean),  "onmouseup");
  }


  protected static void renderHAlign(
    FacesContext        context,
    RenderingContext arc,
    Object              hAlign) throws IOException
  {
    if (hAlign != null)
    {
      boolean rtl = arc.isRightToLeft();

      if ("start".equals(hAlign))
      {
        hAlign = (rtl)
                  ? "right"
                  : "left";
      }
      else if ("end".equals(hAlign))
      {
        hAlign = (rtl)
                  ? "left"
                  : "right";
      }

      context.getResponseWriter().writeAttribute("align", hAlign, null);
    }
  }

  //
  // FORMATTED TEXT
  //


  final protected void renderPossiblyFormattedText(
    FacesContext context,
    Object       textValue
    ) throws IOException
  {
    if (textValue != null)
    {
      String textStr = textValue.toString();
      if (_isTextFormatted(textStr))
        _getFormattedTextParser().writeFormattedText(context, textStr);
      else
        context.getResponseWriter().writeText(textStr, null);
    }
  }

  final protected void renderFormattedText(
    FacesContext context,
    Object       textValue) throws IOException
  {
    if (textValue != null)
    {
      String textStr = textValue.toString();
      _getFormattedTextParser().writeFormattedText(context, textStr);
    }
  }

  private boolean _isTextFormatted(String textStr)
  {
    // =-=AEW Should we support "<HTML>" (caps)?
    return textStr.startsWith("<html>");
  }

  /**
   * @todo Move to AdfRenderingContext???
   */
  private FormattedTextParser _getFormattedTextParser()
  {
    return XhtmlFormattedText.getFormattedTextParser();
  }

  //
  // SPACERS AND TRANSPARENT IMAGES
  //

  static public String getAbsoluteImageUri(
     FacesContext        context,
     RenderingContext arc,
     String              imagePath)
  {
    return getBaseImageUri(context, arc) + imagePath;
  }

  /**
   * @todo GET FROM REAL SOURCE?
   * @todo Cache concatentation
   */
  static protected String getBaseImageUri(
    FacesContext        context,
    RenderingContext arc)
  {
    String contextUri = context.getExternalContext().getRequestContextPath();
    return contextUri + "/adf/images/";
  }

  /**
   * Renders an icon.  If the the specified iconUri is a relative URI,
   * it is appended to the value of the base Image URI.
   * This method may only be called for decorative icons - icons
   * that are purely visual.
   */
  protected final void renderDecorativeIcon(
    FacesContext        context,
    RenderingContext arc,
    String              iconUri,
    Object              width,
    Object              height,
    Object              id,
    Object              altText
    ) throws IOException
  {
    renderDecorativeIcon(context, arc, iconUri, width, height,
                         id, altText, null);
  }

  protected final void renderDecorativeIcon(
    FacesContext        context,
    RenderingContext arc,
    String              iconUri,
    Object              width,
    Object              height,
    Object              id,
    Object              altText,
    UIComponent         comp
    ) throws IOException
  {
    // Convert iconUri to an absolute uri
    String absoluteUri = getAbsoluteImageUri(context, arc, iconUri);

    if ((altText == null) && !isInaccessibleMode(arc))
      altText = "";

    OutputUtils.renderImage(context, arc, absoluteUri,
                            width, height, id, altText, comp);
  }


  /**
   * Renders a vertical spacer for a specified non-null height.
   */
  protected final void renderVerticalSpacer(
    FacesContext        context,
    Object              height,
    Object              id,
    UIComponent         comp
    ) throws IOException
  {
    if (height != null)
    {
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement("div", comp);
      writer.writeAttribute("id", id, null);
      String heightString = height.toString();
      if (heightString.length() != 0)
      {
        //pu: CSS mandates specifying units, a crude sanity check if height
        //  does not already contain units, if yes treat it as pixels.
        boolean isUnitsNotSpecified =
          Character.isDigit(heightString.charAt(heightString.length()-1));
        if (isUnitsNotSpecified)
          heightString += "px";
        writer.writeAttribute("style", "margin-top:" + heightString, null);
      }
      writer.endElement("div");
    }
  }

  /**
   * Renders a spacer.
   */
  protected final void renderSpacer(
    FacesContext        context,
    RenderingContext arc,
    String              width,
    String              height
    ) throws IOException
  {
    renderTransparent(context, arc, width, height, false, null);
  }


  /**
   * Renders a transparent gif using a script to save space.
   */
  protected final void renderTransparent(
    FacesContext        context,
    RenderingContext arc,
    String              width,
    String              height,
    boolean             needsQuoting,
    Object              id
    ) throws IOException
  {
    Counter counter =
      (Counter) getRenderingProperty(arc, _SCRIPT_SPACER_COUNT);
    if (counter == null)
    {
      counter = new Counter();
      setRenderingProperty(arc, _SCRIPT_SPACER_COUNT, counter);
    }
    int count = counter.count++;

    // do not use the script spacer, if we have already rendered an enormous
    // number of spacers. bug 3786394:
    boolean useScript =
      ((count < 800)
       && (TrinidadAgent.SCRIPTING_SPEED_CAP_FAST ==
           arc.getAgent().getCapabilities().get(TrinidadAgent.CAP_SCRIPTING_SPEED)));
    _renderTransparent(context, arc, width, height, needsQuoting, id, useScript);
  }
  
  /**
   * This method evaluates the property of the specified bean if it supports 
   * the specified property key and if the current value is <code>null</code>, 
   * then evaluate the default value.
   * <p>
   * If the bean does not support the specified key, this method returns 
   * <code>null</code>. Unsupported keys occur when the bean's type its type 
   * returned <code>null</code> from <code>findKey</code> when 
   * <code>findTypeConstants</code> method was called.
   * </p>
   * 
   * @param bean the property value holder.
   * @param key  the key associated to the property to evaluate.
   * 
   * @return <code>null</code> if key is <code>null</code>, the current 
   *         property value in the bean for the specified key if it was 
   *         set, or the default value if it wasn't.
   * 
   * @see #findTypeConstants(org.apache.myfaces.trinidad.bean.FacesBean.Type)
   */
  protected Object resolveProperty(FacesBean bean, PropertyKey key)
  {
    return resolveProperty(bean, key, true);
  }
  
  /**
   * This method evaluates the property of the specified bean if it supports 
   * the specified property key and if the current value is <code>null</code>, 
   * then evaluate the default value if and only if <code>checkDefault</code> 
   * is <code>true</code>.
   * <p>
   * If the bean does not support the specified key, this method returns 
   * <code>null</code>. Unsupported keys occur when the bean's type its type 
   * returned <code>null</code> from <code>findKey</code> when 
   * <code>findTypeConstants</code> method was called.
   * </p>
   * 
   * @param bean         the property value holder.
   * @param key          the key associated to the property to evaluate.
   * @param checkDefault a flag to tell the method to look for the default value 
   *                     if no value was explicitely set.
   * 
   * @return <code>null</code> if key is <code>null</code>, the current 
   *         property value in the bean for the specified key if it was 
   *         set, or the default value if it wasn't and checkDefault is 
   *         <code>true</code>.
   */
  protected Object resolveProperty(FacesBean bean, PropertyKey key, boolean checkDefault)
  {
    if (key == null)
    {
      return null;
    }
    
    Object value = bean.getProperty(key);
    if (value == null && checkDefault)
    {
      value = key.getDefault();
    }
    
    return value;
  }

  private static final class Counter
  {
    public int count = 0;
  }

  /**
   * @param useScript use javascript to render the spacer. if this is false,
   * then an html IMG tag will be used.
   * @todo fixup call to addLib
   */
  private void _renderTransparent(
    FacesContext        context,
    RenderingContext arc,
    String              width,
    String              height,
    boolean             needsQuoting,
    Object              id,
    boolean             useScript
    ) throws IOException
  {
    PartialPageContext pContext = arc.getPartialPageContext();

    // cannot use t() in MarlinCore.js on a partial rendering pass
    // just render the icon.
    if (!useScript || (pContext != null))
    {
      renderDecorativeIcon(context, arc, TRANSPARENT_GIF,
                           width, height, id, null);
    }
    else
    {
      // IE has fast javascript, so render has a js function call
      ResponseWriter writer = context.getResponseWriter();

      if (getRenderingProperty(arc,
                               _TRANSPARENT_FUNCTION_WRITTEN_KEY) == null)
      {
        // determine the transparent image's URI
        String transparentURI = getAbsoluteImageUri(context, arc, TRANSPARENT_GIF);

        setRenderingProperty(arc,
                             _TRANSPARENT_FUNCTION_WRITTEN_KEY,
                             Boolean.TRUE);

        // make sure the transparent image function is loaded
        XhtmlUtils.addLib(context, arc, "t()");

        writer.startElement("script", null);
        // Bug #3426092:
        // render the type="text/javascript" attribute in accessibility mode
        renderScriptTypeAttribute(context, arc);

        writer.writeAttribute("id", id, null);

        // store transparentURI as javascript variable
        // which is used in t()
        writer.write("var _tURL=\"" + transparentURI + "\";");

        // store accessibility mode as javascript variable
        // which is used in t()
        writer.write("var _axm");
        if (!isInaccessibleMode(arc))
          writer.write("=1");
        writer.write(";");
      }
      else
      {
        writer.startElement("script", null);
        // Bug #3426092:
        // render the type="text/javascript" attribute in accessibility mode
        renderScriptTypeAttribute(context, arc);
        writer.writeAttribute("id", id, null);
      }

      // write a reference to the transparent gif function
      writer.write("t(");

      if ((width != null) || (height != null))
      {
        String widthParam = "void 0";

        if (width != null)
        {
          widthParam = width;

          if (needsQuoting)
          {
            writer.write("'");
          }
        }

        writer.write(widthParam);

        if (needsQuoting && (width != null))
        {
          writer.write("'");
        }

        if (height != null)
        {
          writer.write(",");

          if (needsQuoting)
          {
            writer.write("'");
          }

          writer.write(height);

          if (needsQuoting)
          {
            writer.write("'");
          }
        }
      }

      writer.write(")");

      writer.endElement("script");
    }
  }

  //
  // JAVASCRIPT RENDERING
  //
  /**
   * Renders the "defer" attribute for a script element.
   * In order to support partial page rendering, scripts must
   * be rendered using the "defer" attribute to ensure that
   * they are executed in the appropriate context.  However,
   * some browsers (eg. IE) do not reliably load scripts
   * which are deferred.  This method detects whether the
   * target agent can handle the defer attribute correctly and
   * only renders this attribute when as appropriate.
   * <p>
   * Note: ResponseWriter.startElement("script", null) must be called
   * before calling this method.
   */
  public static void renderScriptDeferAttribute(
    FacesContext        context,
    RenderingContext arc) throws IOException
  {
    // At the moment we only render the defer attribute if
    // we are in the partial rendering pass.  This is to
    // avoid the "object expected" JavaScript errors that we
    // see (bug 2251656).  We need to do further browser
    // testing to determine whether we might enable deferred
    // scripts in more cases.

    // Note that we only want to defer scripts once we are actually
    // inside the body content - and writing content to the
    // ScriptBufferingResponseWriter.  Otherwise, we run into bug
    // 2466017.
    if (false)
      context.getResponseWriter().writeAttribute("defer", Boolean.TRUE, null);
  }

  /**
   * Checks whether in inaccessible mode, and if not, renders "type" attribute
   * for a script element.
   * <p>
   *
   * Note: ResponseWriter.startElement("script", null) must be called
   * before calling this method.
   */
  public static void renderScriptTypeAttribute(
    FacesContext        context,
    RenderingContext arc) throws IOException
  {
    if (!isInaccessibleMode(arc))
    {
      context.getResponseWriter().writeAttribute("type",
                                                 _ACCESSIBILITY_SCRIPT_TYPE,
                                                 null);
    }
  }



  //
  // ATTRIBUTE HOOKS
  //
  protected String getShortDesc(FacesBean bean)
  {
    return toString(bean.getProperty(_shortDescKey));
  }

  protected String getStyleClass(FacesBean bean)
  {
    return toString(bean.getProperty(_styleClassKey));
  }

  protected String getInlineStyle(FacesBean bean)
  {
    return toString(bean.getProperty(_inlineStyleKey));
  }

  protected String getOnclick(FacesBean bean)
  {
    if (_onclickKey == null)
      return null;

    return toString(bean.getProperty(_onclickKey));
  }

  protected String getOndblclick(FacesBean bean)
  {
    if (_ondblclickKey == null)
      return null;

    return toString(bean.getProperty(_ondblclickKey));
  }

  protected String getOnkeydown(FacesBean bean)
  {
    if (_onkeydownKey == null)
      return null;

    return toString(bean.getProperty(_onkeydownKey));
  }

  protected String getOnkeyup(FacesBean bean)
  {
    if (_onkeyupKey == null)
      return null;

    return toString(bean.getProperty(_onkeyupKey));
  }

  protected String getOnkeypress(FacesBean bean)
  {
    if (_onkeypressKey == null)
      return null;

    return toString(bean.getProperty(_onkeypressKey));
  }

  protected String getOnmousedown(FacesBean bean)
  {
    if (_onmousedownKey == null)
      return null;

    return toString(bean.getProperty(_onmousedownKey));
  }

  protected String getOnmousemove(FacesBean bean)
  {
    if (_onmousemoveKey == null)
      return null;

    return toString(bean.getProperty(_onmousemoveKey));
  }

  protected String getOnmouseout(FacesBean bean)
  {
    if (_onmouseoutKey == null)
      return null;

    return toString(bean.getProperty(_onmouseoutKey));
  }

  protected String getOnmouseover(FacesBean bean)
  {
    if (_onmouseoverKey == null)
      return null;

    return toString(bean.getProperty(_onmouseoverKey));
  }

  protected String getOnmouseup(FacesBean bean)
  {
    if (_onmouseupKey == null)
      return null;

    return toString(bean.getProperty(_onmouseupKey));
  }

  protected String[] getPartialTriggers(FacesBean bean)
  {
    if (_partialTriggersKey == null)
    {
      return null;
    }
    else
    {
      return (String[])bean.getProperty(_partialTriggersKey);
    }
  }

  /**
   * Returns true if the agent supports the "onclick" JS Handler in an "input" 
   * HTML element of type "image"
   */
  static public boolean supportsOnClickOnImgInput(RenderingContext arc)
  {
    Object cap = arc.getAgent().getCapabilities().get(
                      TrinidadAgent.CAP_ONCLICK_IMG_INPUT);
    return !Boolean.FALSE.equals(cap);
  }
  
  private PropertyKey _shortDescKey;
  private PropertyKey _styleClassKey;
  private PropertyKey _inlineStyleKey;
  private PropertyKey _onclickKey;
  private PropertyKey _ondblclickKey;
  private PropertyKey _onkeydownKey;
  private PropertyKey _onkeyupKey;
  private PropertyKey _onkeypressKey;
  private PropertyKey _onmousedownKey;
  private PropertyKey _onmousemoveKey;
  private PropertyKey _onmouseoutKey;
  private PropertyKey _onmouseoverKey;
  private PropertyKey _onmouseupKey;
  private PropertyKey _partialTriggersKey;

  // The value bound to the type attribute in script tags in accessibilty mode.
  private static final String _ACCESSIBILITY_SCRIPT_TYPE = "text/javascript";

  // Key to look up the number of times we have rendered the script spacer:
  private static final Object _SCRIPT_SPACER_COUNT = new Object();

  // Rendering context key for transparent gif
  // This MUST REMAIN THE SAME VALUE as
  //                 XhtmlLafRenderer.__TRANSPARENT_URL_KEY = "_t.gif";
  private static final String _TRANSPARENT_FUNCTION_WRITTEN_KEY = "_t.gif";

  private static final TrinidadLogger _LOG = TrinidadLogger.createTrinidadLogger(XhtmlRenderer.class);
}
