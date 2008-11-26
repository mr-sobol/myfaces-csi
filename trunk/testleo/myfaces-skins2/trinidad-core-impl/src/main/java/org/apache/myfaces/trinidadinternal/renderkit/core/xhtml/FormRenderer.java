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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.validator.Validator;

import org.apache.myfaces.trinidad.logging.TrinidadLogger;
import org.apache.myfaces.trinidad.bean.FacesBean;
import org.apache.myfaces.trinidad.bean.PropertyKey;
import org.apache.myfaces.trinidad.component.UIXForm;
import org.apache.myfaces.trinidad.component.core.CoreForm;
import org.apache.myfaces.trinidad.context.RequestContext;

import org.apache.myfaces.trinidad.context.Agent;
import org.apache.myfaces.trinidadinternal.agent.TrinidadAgent;
import org.apache.myfaces.trinidad.context.FormData;
import org.apache.myfaces.trinidad.context.PartialPageContext;
import org.apache.myfaces.trinidad.context.RenderingContext;
import org.apache.myfaces.trinidadinternal.renderkit.core.CoreResponseStateManager;
import org.apache.myfaces.trinidadinternal.renderkit.uix.SubformRenderer;

// TODO: Remove this class
import org.apache.myfaces.trinidadinternal.share.data.ServletRequestParameters;

/**
 * @version $Name:  $ ($Revision$) $Date$
 */
public class FormRenderer extends XhtmlRenderer
{
  public FormRenderer()
  {
    super(CoreForm.TYPE);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void decode(FacesContext context,
                     UIComponent component)
  {
    Map<String, String> paramMap =
      context.getExternalContext().getRequestParameterMap();

    Object formName = paramMap.get(CoreResponseStateManager.FORM_FIELD_NAME);
    boolean submitted = false;

    if ( formName != null )
      submitted = formName.equals(getClientId(context, component));

    // We use this decode for both our form and UIForm
    if (component instanceof UIForm)
      ((UIForm) component).setSubmitted(submitted);
    else
      ((UIXForm) component).setSubmitted(submitted);
  }

  @Override
  public boolean getRendersChildren()
  {
    return false;
  }

  @Override
  protected void findTypeConstants(FacesBean.Type type)
  {
    super.findTypeConstants(type);

    _usesUploadKey = type.findKey("usesUpload");
    _autoCompleteKey = type.findKey("autoComplete");
    _defaultCommandKey = type.findKey("defaultCommand");
    _onsubmitKey = type.findKey("onsubmit");
    _targetFrameKey = type.findKey("targetFrame");
  }

  @Override
  protected void encodeBegin(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         comp,
    FacesBean           bean) throws IOException
  {
    ResponseWriter rw = context.getResponseWriter();

    String formName = getClientId(context, comp);

    CoreFormData fData = new CoreFormData(formName);
    arc.setFormData(fData);

    if (formName != null)
    {
      // =-=AEW This should get removed in favor of solely using FormData;
      // but keep it around for now
      arc.getProperties().put(XhtmlConstants.FORM_NAME_PROPERTY, formName);
    }

    if (supportsScripting(arc))
    {
      // we depend on the form submission library
      XhtmlUtils.addLib(context, arc, "submitForm()");
      XhtmlUtils.addLib(context, arc, "_submitOnEnter()");

      // if the onSubmit attribute is set, generate the JavaSCript variable
      // with the source code to execute when submitting.  We do this,
      // because the normal onSubmit handler on forms is NOT called if
      // the form is submitted programatically
      String onsubmit = getOnsubmit(bean);

      if (onsubmit != null)
      {
        rw.startElement("script", null);
        renderScriptDeferAttribute(context, arc);

        // Bug #3426092:
        // render the type="text/javascript" attribute in accessibility mode
        renderScriptTypeAttribute(context, arc);

        rw.write("var _");
        rw.write(XhtmlUtils.getJSIdentifier(formName));
        rw.write("_Submit=\"");
        rw.writeText(onsubmit, null);
        rw.write('"');

        rw.endElement("script");
      }
    }


    rw.startElement("form", comp);
    renderId(context, comp);
    renderAllAttributes(context, arc, bean);

    // Render the method.  It's POST if they want file upload, and they
    // can actually upload files otherwise we'll just use whatever they request
    if (getUsesUpload(bean))
    {
      rw.writeAttribute("enctype", "multipart/form-data", "usesUpload");
    }

    rw.writeAttribute("method", "POST", null);

    //only render onkeypress if supports scripting (not in printable mode)
    if (supportsScripting(arc))
    {
      rw.writeAttribute("onkeypress",
                      getFullOnkeypress(context,
                                        comp,
                                        bean,
                                        formName),
                      "onkeypress");
    }

    // render the autocomplete attribute
    if (supportsAutoCompleteFormElements(arc))
    {
      String autocomplete = getAutoComplete(bean);
      if (autocomplete.equalsIgnoreCase(CoreForm.AUTO_COMPLETE_OFF))
      {
        rw.writeAttribute("autocomplete", "off", "autoComplete");
      }
    }

    String viewId = context.getViewRoot().getViewId();
    String action =
      context.getApplication().getViewHandler().getActionURL(context, viewId);
    renderEncodedActionURI(context, "action", action);
    if (supportsTarget(arc))
    {
      rw.writeAttribute("target", getTargetFrame(bean), "targetFrame");
    }
  }

  @Override
  protected void encodeEnd(
    FacesContext        context,
    RenderingContext arc,
    UIComponent         comp,
    FacesBean           bean) throws IOException
  {
    String noJavaScriptSupport = "false";
    ResponseWriter writer = context.getResponseWriter();

    String formName = arc.getFormData().getName();
    PartialPageContext pprContext = arc.getPartialPageContext();

    // Write out the hidden form field that identifies which
    // form is the one being submitted
    writer.startElement("input", null);
    writer.writeAttribute("type", "hidden", null);
    writer.writeAttribute("name", CoreResponseStateManager.FORM_FIELD_NAME, null);
    writer.writeAttribute("value", formName, null);
    writer.endElement("input");
    /*
     * set the hidden parameter noJavaScriptSupport as true for Non-JavaScript
     * browsers
     */
    if( !supportsScripting(arc))
    {
      noJavaScriptSupport = XhtmlConstants.NON_JS_BROWSER_TRUE;
    }
    writer.startElement("input", null);
    writer.writeAttribute("type", "hidden", null);
    writer.writeAttribute("name", XhtmlConstants.NON_JS_BROWSER,null );
    writer.writeAttribute("value", noJavaScriptSupport, null);
    writer.endElement("input");
    // Check to see if this is a partial page render.  If so, we need
    // to push the ID of the postscript onto the partial target stack
    final String postscriptId = _getPostscriptId(arc, formName);
    if (pprContext != null)
    {
      if (!pprContext.isInsidePartialTarget())
      {
        pprContext.addPartialTarget(postscriptId);
      }
    }

    if (postscriptId != null)
    {
      // We wrap all of our values/scripts inside of a span so that
      // they can be easily updated during a partial page render.
      // Note: it is essential that we call context.getResponseWriter()
      // *after* calling _startPartialPostscriptRender().  Otherwise,
      // we'll end up writing to the null output method.
      writer.startElement("span", new CoreForm()
      {
        public String getClientId(FacesContext context)
        {
          return postscriptId;
        }
      });
      writer.writeAttribute("id", postscriptId, null);
    }

    // Include JSF state.
    context.getApplication().getViewHandler().writeState(context);

    // Render any needed values
    //VAC this condition is needed for bug 4526850- It ensures that only
    //state token and form name parameters are overwritten when there is
    //a partial page submission.
    //PH:include condition that browser is not blackberry
    if (!isPDA(arc))
    {
      _renderNeededValues(context, arc);
    }


    // Render any validation scripts
    _renderValidationScripts(context, arc);

    // Render reset calls
    _renderResetCalls(context, arc);

    // Close up our postscript span if we have one
    if (postscriptId != null)
      writer.endElement("span");

    //this condition is needed for bug 4526850- It ensures that only
    //state token and form name parameters are overwritten when there is
    //a partial page submission.
    // (also include blackberry browser in this condition)
    if (isPDA(arc) && pprContext == null)
    {
      //Add hidden elements in the form for enabling PPR on IE Mobile.

      Object domLevel =  arc.getAgent().getCapabilities().get(
                    TrinidadAgent.CAP_DOM);

      if(
         domLevel == null ||
         domLevel == TrinidadAgent.DOM_CAP_NONE ||
         domLevel == TrinidadAgent.DOM_CAP_FORM)
      {
        FormData formData = arc.getFormData();
        if(formData != null)
        {
          boolean isPIE = Agent.PLATFORM_PPC.equalsIgnoreCase(
                       arc.getAgent().getPlatformName());

          // =-=AdamWiner: this isn't correct - these
          // parameters should be added by the components that need
          // them, not globally by the form control
          if (isPIE)
          {
            formData.addNeededValue(XhtmlConstants.SOURCE_PARAM);
            formData.addNeededValue(XhtmlConstants.EVENT_PARAM);
            formData.addNeededValue(XhtmlConstants.PARTIAL_TARGETS_PARAM);
            formData.addNeededValue(XhtmlConstants.PARTIAL_PARAM);
          }
          else
          {
            formData.addNeededValue(XhtmlConstants.SOURCE_PARAM);
            formData.addNeededValue(XhtmlConstants.EVENT_PARAM);
          }
        }
      }

      _renderNeededValues(context, arc);
    }

    // Render submitFormCheck js function --
    // checks if submitForm was rejected because form was incomplete
    // when it was called, and thus calls submitForm again.
    if (pprContext == null)
      _renderSubmitFormCheck(context, arc);


    // Close up the form
    writer.endElement("form");

    // Clear out the form name property
    arc.getProperties().remove(XhtmlConstants.FORM_NAME_PROPERTY);

    // clear out form data:;
    arc.clearFormData();
  }


  /**
   * Returns the inline Style used to render this node.
   */
  @Override
  protected String getInlineStyle(FacesBean bean)
  {
    String inlineStyle = super.getInlineStyle(bean);
    if (inlineStyle == null)
      return "margin:0px";

    return inlineStyle + ";margin:0px";
  }


  /**
   * Render the client ID as both an "id" and a "name"
   */
  @Override
  protected void renderId(
    FacesContext context,
    UIComponent  component) throws IOException
  {
    String clientId = getClientId(context, component);

    context.getResponseWriter().writeAttribute("id", clientId, "id");
    context.getResponseWriter().writeAttribute("name", clientId, "id");
  }


  /**
   * All editable components need IDs.
   */
  @Override
  protected boolean shouldRenderId(
    FacesContext context,
    UIComponent  component)
  {
    return true;
  }

  // Renders reset call code
  private static void _renderResetCalls(
    FacesContext context,
    RenderingContext arc) throws IOException
  {

    // if scripting isn't supported, no need to do the rest
    if (!supportsScripting(arc))
      return;

    //
    // Write the array of reset calls
    //
    CoreFormData fData = (CoreFormData) arc.getFormData();
    Map<String, String> resetCallMap = fData.getResetCalls(false);

    int resetCallCount = (resetCallMap != null)
                            ? resetCallMap.size()
                            : 0;

    if (resetCallCount != 0)
    {
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement("script", null);
      renderScriptDeferAttribute(context, arc);
      // Bug #3426092:
      // render the type="text/javascript" attribute in accessibility mode
      renderScriptTypeAttribute(context, arc);
      writer.writeText("TrPage.getInstance()._addResetCalls('", null);
      writer.writeText(fData.getName(), null);
      writer.writeText("',{", null);
      boolean firstCall = true;

      for (Map.Entry<String, String> entry : resetCallMap.entrySet())
      {
        String clientId = entry.getKey();
        String currCall = entry.getValue();

        if (firstCall)
        {
          firstCall = false;
        }
        else
        {
          // write the separator every time except the first time
          writer.writeText(",", null);
        }

        // write the error format
        // use single quotes since embedded single quotes
        // are automatically escaped
        writer.writeText("'", null);
        writer.writeText(clientId, null);
        writer.writeText("':'", null);
        writer.writeText(XhtmlUtils.escapeJS(currCall), null);
        writer.writeText("'", null);
      }

      writer.writeText("});", null);
      writer.endElement("script");
    }

  }


  // Renders validation code
  // Code is of the form:
  //  - Dependencies, written sequentially
  //  - Validation function, written only on non-PPR requests
  //  - Call to _addValidators function, which takes
  //     - form object
  //     - _Validators array, which is an array of 5*N entries with each 5:
  //     0: clientId,
  //     1: required (0 or 1)
  //     2: requiredFormatIndex (blank if required==0) - index into _Formats
  //     3: converter (or (void 0) if omitted) - index into Validations array
  //     4: validator array - array of integers, each index into Validations
  //     TODO: turn into a Map of clientId to 4 entries
  //     TODO: consider passing "immediate"
  //     - _Validations array (an array of stringified JSON objects)
  //      (TODO: stringifying these is pointless, pass as direct JSON)
  //     - Label map: clientId to label
  //     - _Formats array: now used only for required messages
  //
  private static void _renderValidationScripts(
    FacesContext        context,
    RenderingContext arc
    ) throws IOException
  {
    // if scripting isn't supported, no need to do the rest
    if (!supportsScripting(arc))
      return;

    //
    // Output validation-related JavaScript
    //
    ResponseWriter writer = context.getResponseWriter();
    CoreFormData   fData = (CoreFormData) arc.getFormData();

    // Fix up the form name for use as a Javascript identifier
    String jsID = XhtmlUtils.getJSIdentifier(fData.getName());

    writer.startElement("script", null);
    renderScriptDeferAttribute(context, arc);
    renderScriptTypeAttribute(context, arc);

    //
    // Write the array of client dependencies
    // Whether or not client side validation is enabled,
    // The dependencies may be needed - see bug
    // 4409339 TURNING OFF CLIENT SIDE VALIDATION CAUSES
    //                                ERRORS IN SELECTINPUTCOLOR & DATE
    List<String> clientDependencies = fData.getClientDependencies( false);
    if (clientDependencies != null)
    {
      for (int d = 0; d < clientDependencies.size(); d++)
      {
        writer.writeText(clientDependencies.get(d),null);
      }
    }
    
    RequestContext rc = RequestContext.getCurrentInstance();
    boolean isClientValidationDisabled = 
      rc.getClientValidation() == RequestContext.ClientValidation.DISABLED;

    // Only bother writing out the function when there's no PPR,
    // as the content doesn't change request to request
    if (arc.getPartialPageContext() == null)
    {
      boolean isInline =
        (rc.getClientValidation() == RequestContext.ClientValidation.INLINE);

      //
      // write the validation function for this form
      //
      writer.writeText("function _", null);
      writer.writeText(jsID, null);
      
      writer.writeText("Validator(f,s){return ", null);

      if (isClientValidationDisabled)
      {
        // No client-side validation:  always return true
        writer.writeText("true", null);
      }
      else if (isInline)
      {
        writer.writeText("_validateInline(f,s)", null);
      }
      else
      {
        writer.writeText("_validateAlert(f,s,null,\"", null);
        writer.writeText(XhtmlUtils.escapeJS(
            arc.getTranslatedString(_GLOBAL_FORMAT_KEY)), null);
        writer.writeText("\",\"", null);
      
        writer.writeText(XhtmlUtils.escapeJS(
            arc.getTranslatedString("af_form.SUBMIT_ERRORS")), null);
        writer.writeText("\")", null);
      }
      
      writer.writeText(";}", null);
    }
    
    
    // If no client-side validation, return now
    if (isClientValidationDisabled)
    {
      writer.endElement("script");
      return;
    }

    //
    // Write the array of validation calls
    //
    //
    // Write the array of form validators
    //
    Map<String, List<CoreFormData.ConvertValidate>> validatorInfoMap =
      fData.getFormValidatorsInfo(false);
    
    if (validatorInfoMap != null)
    {
      writer.writeText("_addValidators(\"", null);
      writer.writeText(fData.getName(), null);
      writer.writeText("\",[", null);

      boolean firstFormInfo = true;
      // FIXME: the List here is wrong;  we should only ever have
      // one CoreFormData.ConvertValidate per ID, and anything else is
      // an error
      for (Map.Entry<String, List<CoreFormData.ConvertValidate>> validatorEntry  :
           validatorInfoMap.entrySet())
      {
        String clientId = validatorEntry.getKey();
        List<CoreFormData.ConvertValidate> validatorInfoList =
          validatorEntry.getValue();
      
        for (CoreFormData.ConvertValidate convertValidate : validatorInfoList)
        {
          
          if (firstFormInfo)
          {
            firstFormInfo = false;
          }
          else
          {
            // write the separator every time except the first time
            writer.writeText("],", null);
          }
          
          writer.writeText("\"", null);
          
          // write the element name of the element to be validated
          writer.writeText(clientId, null);
          writer.writeText("\",", null);

          // write out whether or not this element is required
          writer.writeText(convertValidate.required? "1" : "0", null);
          writer.writeText(",", null);
          
          if (convertValidate.requiredFormatIndex != null)
          {
            // write out the index of the required error message
            writer.writeText(convertValidate.requiredFormatIndex, null);
          }
          
          writer.writeText(",", null);
          
          Object converterInfo = convertValidate.converter;
          
          if (converterInfo != null)
          {
            writer.writeText(converterInfo, null);
          }
          else
          {
            writer.writeText("(void 0)", null);
          }
          
          writer.writeText(",[", null);
          
          ArrayList<Integer> validatorInfo = convertValidate.validators;

          if (validatorInfo != null)
          {
            boolean firstValidator = true;
            
            int i = 0;
            while (i < validatorInfo.size())
            {
              if (firstValidator)
              {
                firstValidator = false;
              }
              else
              {
                // write the separator every time except the first time
                writer.writeText(",", null);
              }
              
              // write the validation string for the validater
              writer.writeText(validatorInfo.get(i).toString(), null);
              
              i = i + 1;
            }
          }
        }
      }

      writer.writeText("]],[", null);

      Iterator<String> validationIterator = fData.getValidationIterator();
      if (validationIterator != null)
      {
        boolean firstValidation = true;

        while(validationIterator.hasNext())
        {
          String currValidation = validationIterator.next();
          
          if (firstValidation)
          {
            firstValidation = false;
          }
          else
          {
            // write the separator every time except the first time
            writer.writeText(",", null);
          }
          
          // write the error format
          // use single quotes since embedded single quotes
          // are automatically escaped
          writer.writeText("\'", null);
          writer.writeText(XhtmlUtils.escapeJS(currValidation), null);
          writer.writeText("\'", null);
        }
      }

        
      writer.writeText("],{", null);
      

      //
      // Render the labels used by validated fields in this form
      //
      
      // list of labels used for validation on this form
      List<String> inputList = fData.getValidatedInputList(false);
      
      int inputCount = (inputList != null)
                         ? inputList.size()
                         : 0;
      
      if (inputCount > 0)
      {
        Map<String, String> labelMap = fData.getLabelMap(false);
        
        if (labelMap != null)
        {
          boolean firstLabel = true;
          
          for (int i = 0; i < inputCount; i++)
          {
            String currID = inputList.get(i);
            
            // remove the ID entry to prevent multiple labels from
            // being written
            String currLabel = labelMap.remove(currID);
            
            if (currLabel != null)
            {
              if (firstLabel)
              {
                firstLabel = false;
              }
              else
              {
                // write the separator every time except the first time
                writer.writeText(",", null);
              }
              
              // write the ID of the validated field as the key
              writer.writeText("\'", null);
              writer.writeText(currID, null);
              writer.writeText("\':\'", null);

              // write the label of the validated field as the value
              writer.writeText(XhtmlUtils.escapeJS(currLabel), null);
              writer.writeText("\'", null);
            }
          }
        }
      }
      writer.writeText("},[", null);

      //
      // Render the error format list for this form
      //
      
      // list of error formats used for validation on this form
      Iterator<String> errorFormatIterator = fData.getErrorFormatIterator();

      if (errorFormatIterator != null)
      {
        boolean firstFormat = true;
        
        while(errorFormatIterator.hasNext())
        {
          String currErrorFormat = errorFormatIterator.next();
          
          if (firstFormat)
          {
            firstFormat = false;
          }
          else
          {
            // write the separator every time except the first time
            writer.writeText(",", null);
          }
          
          // write the error format
          writer.writeText("'", null);
          writer.writeText(XhtmlUtils.escapeJS(currErrorFormat), null);
          writer.writeText("'", null);
        }
      }

      writer.writeText("]);", null);
    }

    _renderSubformLists(context, jsID);

    writer.endElement("script");
  }


  private static void _renderSubformLists(
    FacesContext      context,
    String           jsID
    ) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    List<String> subforms =
      SubformRenderer.getSubformList(context, false, false);

    writer.writeText("var ", null);
    writer.writeText(jsID, null);
    writer.writeText("_SF={", null);
    if ((subforms != null) && !subforms.isEmpty())
    {
      List<String> defaultSubforms =
        SubformRenderer.getSubformList(context, true, false);

      Iterator<String> ids = subforms.iterator();
      while (ids.hasNext())
      {
        String id = ids.next();
        writer.writeText("\"", null);
        writer.writeText(id, null);
        writer.writeText("\":", null);
        if ((defaultSubforms != null) && defaultSubforms.contains(id))
          writer.writeText("2", null);
        else
          writer.writeText("1", null);

        if (ids.hasNext())
          writer.writeText(",", null);
      }
    }

    writer.writeText("};", null);
  }


  // render script which will check if submitForm script has been called before
  // the form has rendered in its entirety, and if so, it will re-call
  // submitForm.
  // this script should be rendered at the very end of the form.
  private static void _renderSubmitFormCheck(
    FacesContext        context,
    RenderingContext arc
    ) throws IOException
  {
    // if scripting isn't supported, no need to do the rest
    if (!supportsScripting(arc))
      return;

    ResponseWriter writer = context.getResponseWriter();
    writer.startElement("script", null);
    renderScriptDeferAttribute(context, arc);
    // Bug #3426092:
    // render the type="text/javascript" attribute in accessibility mode
    renderScriptTypeAttribute(context, arc);

    writer.writeText("_submitFormCheck();", null);
    writer.endElement("script");
  }



  /**
   * @param call a function call.
   * "eval(call)" will be called on the client when resetting.
   */
  public static void addResetCall(
    String           clientId,
    String           call
    )
  {
    CoreFormData fData = (CoreFormData)
      RenderingContext.getCurrentInstance().getFormData();
    fData.addResetCall(clientId, call);
  }

 public static void addOnSubmitConverterValidators(
    UIComponent         component,
    Converter           converter,
    Iterator<Validator> validators,
    String              clientId,
    boolean             immediate,
    boolean             required,
    String              requiredMessageKey
    ) throws IOException
  {
    CoreFormData fData = (CoreFormData)
      RenderingContext.getCurrentInstance().getFormData();

    fData.addOnSubmitConverterValidators(component,
                                         converter,
                                         validators,
                                         clientId,
                                         immediate,
                                         required,
                                         requiredMessageKey);
  }



  /**
   * Add a mapping of an input element ID to a label String. If there is a
   * client-side error regarding the form element with the given ID, the given
   * label will be used in the client-side error message.
   * @param targetID the ID of the form element
   * @param label the label that describes the form element
   * <code>targetID</code>
   */
  public static void addLabelMapping(
    String           targetID,
    String           label
    )
  {
    FormData fData = RenderingContext.getCurrentInstance().getFormData();
    fData.addLabel(targetID, label);
  }


  public static int getInputTextCount(
  )
  {
    CoreFormData fData = (CoreFormData) RenderingContext.getCurrentInstance().getFormData();
    return fData.getInputTextCount();
  }

  public static void incrementInputTextCount(
  )
  {
    CoreFormData fData = (CoreFormData) RenderingContext.getCurrentInstance().getFormData();
    fData.incrementInputTextCount();
  }

  // Returns the ID to use for our postscript if PPR is supported
  private static String _getPostscriptId(
    RenderingContext arc,
    String formName
    )
  {
    if (PartialPageUtils.supportsPartialRendering(arc))
      return "tr_" + formName + "_Postscript";

    return null;
  }

  protected String getDefaultCommand(FacesBean bean)
  {
    return toString(bean.getProperty(_defaultCommandKey));
  }

  protected String getOnsubmit(FacesBean bean)
  {
    if (_onsubmitKey == null)
      return null;

    return toString(bean.getProperty(_onsubmitKey));
  }

  protected String getTargetFrame(FacesBean bean)
  {
    return toString(bean.getProperty(_targetFrameKey));
  }


  protected boolean getUsesUpload(FacesBean bean)
  {
    Object o = bean.getProperty(_usesUploadKey);
    if (o == null)
      o = _usesUploadKey.getDefault();

    return Boolean.TRUE.equals(o);
  }


  protected String getFullOnkeypress(
     FacesContext context,
     UIComponent  component,
     FacesBean    bean,
     String       clientId)
  {
    String onKeypress = super.getOnkeypress(bean);

    String defaultCommand = getDefaultCommand(bean);

    String submitFunc = null;

    UIComponent defaultCommandComponent = null;
    if (defaultCommand != null)
    {
      defaultCommandComponent
        = component.findComponent(defaultCommand);
    }

    if (defaultCommandComponent != null && (defaultCommandComponent instanceof ActionSource))
    {
      // Get the true clientId
      String defaultCommandId =
        defaultCommandComponent.getClientId(context);
      int immediate = 1;

      if(((ActionSource) defaultCommandComponent).isImmediate())
      {
        immediate = 0;
      }
      
      //PPR
      Boolean ppr = (Boolean) defaultCommandComponent.getAttributes().get("partialSubmit"); 
      if(ppr != null && ppr)
      {
        submitFunc = "return _submitOnEnter"
            + "(event,'"  + clientId
            + "'," + "'" + defaultCommandId
            + "'," + immediate 
            + "," + true +");";
      }
      //no PPR
      else
      {
        submitFunc = "return _submitOnEnter"
            + "(event,'"  + clientId
            + "'," + "'" + defaultCommandId
            + "'," + immediate 
            + "," + false +");";
      }
    }
    else
    {
      submitFunc = "return _submitOnEnter(event,'" +
                             clientId +
                             "');";
    }

    onKeypress = XhtmlUtils.getChainedJS(onKeypress, submitFunc, true);

    return onKeypress;
  }

  @Override
  protected String getOnkeypress(FacesBean bean)
  {
    // Back out the default keypress, since we need more info
    return null;
  }

  protected String getAutoComplete(FacesBean bean)
  {
    Object o = bean.getProperty(_autoCompleteKey);
    if (o == null)
      o = _autoCompleteKey.getDefault();
    return o.toString();
  }

  private static void _renderHiddenField(
    ResponseWriter writer,
    Object       name,
    Object       value
    ) throws IOException
  {
    writer.startElement("input", null);
    writer.writeAttribute("type", "hidden", null);
    writer.writeAttribute("name", name, null);
    writer.writeAttribute("value", value, null);
    writer.endElement("input");
  }

  /**
   * Render each "needed" FormValue that hasn't already
   * been rendered.  Called by FormRenderer.postrender().
   * @see FormRenderer#encodeEnd(FacesContext,RenderingContext, UIComponent, FacesBean)
   */
  static private void _renderNeededValues(
    FacesContext        context,
    RenderingContext arc
    ) throws IOException
  {
    ResponseWriter writer = context.getResponseWriter();
    CoreFormData fData = (CoreFormData) arc.getFormData();

    if (fData.useCompoundNames())
    {
      // We are rendering compound names instead of rendering
      // hidden fields without values
      _renderHiddenField(writer,
                         ServletRequestParameters.HAS_COMPOUND_NAME,
                         "a");
    }
    else
    {
      int realNeededIndex = 0;
      List<String> neededValues = fData.getNeededValues(false);

      //
      // loop over the list of needed names, creating hidden fields
      // for any that have not already been rendered
      //
      int neededCount = (neededValues != null)
                          ? neededValues.size()
                          : 0;

      if (neededCount > 0)
      {
        // if we support scripting use null as the value for
        // the needed values, as we will be filling them
        // in.  For non-js platforms, use a value that will
        // result in the value being passed to the server
        String neededValue = (supportsScripting(arc))
                                ? null
                                : "a";

        Set<String> renderedValues = fData.getRenderedValues(true);

        for (int i = 0; i < neededCount; i++)
        {
          String currName = neededValues.get(i);

          // if the needed name hasn't been rendered, add it to our
          // list of unrendered elements
          if (!renderedValues.contains(currName))
          {
            // move this item to be the last actually needed item
            neededValues.set(realNeededIndex, currName);
            realNeededIndex++;

            // generate the hidden form field for this needed item
            _renderHiddenField(writer, currName, neededValue);
            fData.addRenderedValue(currName);
          }
        }

        //
        // Output the javascript array of fields that need to be reset
        //
        if (realNeededIndex > 0)
        {
          if (supportsScripting(arc))
          {
            writer.startElement("script", null);
            renderScriptDeferAttribute(context, arc);
            // Bug #3426092:
            // render the type="text/javascript" attribute in accessibility mode
            renderScriptTypeAttribute(context, arc);

            writer.writeText("TrPage.getInstance()._addResetFields('", null);
            writer.writeText(fData.getName(), null);
            writer.writeText("',[\"", null);
            writer.writeText(neededValues.get(0), null);

            for (int i = 1; i < realNeededIndex; i++)
            {
              writer.writeText("\",\"", null);
              writer.writeText(neededValues.get(i), null);
            }

            writer.writeText("\"]);", null);
            writer.endElement("script");
          }
        }
      }
    }
  }


  // key used to indicate whether or not usesUpload is used:
  public static final Object USES_UPLOAD_KEY = new Object();

  private PropertyKey _usesUploadKey;
  private PropertyKey _autoCompleteKey;
  private PropertyKey _defaultCommandKey;
  private PropertyKey _onsubmitKey;
  private PropertyKey _targetFrameKey;

  static private final String _GLOBAL_FORMAT_KEY =
    "af_messages.GLOBAL_MESSAGE_FORMAT";

  // -= Simon Lessard =-
  // FIXME: Nothing in this class is logged as of 2006-08-03
  @SuppressWarnings("unused")
  static private final TrinidadLogger _LOG = TrinidadLogger.createTrinidadLogger(FormRenderer.class);
}
