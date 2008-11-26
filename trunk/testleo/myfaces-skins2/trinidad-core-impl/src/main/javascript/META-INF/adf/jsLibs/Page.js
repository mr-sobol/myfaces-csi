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
function TrPage()
{
  this._loadedLibraries = TrPage._collectLoadedLibraries();
  this._requestQueue = new TrRequestQueue(window);
}


/**
 * Get the shared instance of the page object.
 */
TrPage.getInstance = function()
{
  if (TrPage._INSTANCE == null)
    TrPage._INSTANCE = new TrPage();

  return TrPage._INSTANCE;
}

/**
 * Return the shared request queue for the page.
 */
TrPage.prototype.getRequestQueue = function()
{
  return this._requestQueue;
}

/**
 * Post the form for partial postback.  Supports both standard AJAX
 * posts and, for multipart/form posts, IFRAME-based transmission.
 * @param actionForm{FormElement} the HTML form to post
 * @param params{Object} additional parameters to send
 * @param headerParams{Object} HTTP headers to include (ignored if 
 *   the request must be a multipart/form post)
 */
TrPage.prototype.sendPartialFormPost = function(
  actionForm,
  params,
  headerParams)
{
  this.getRequestQueue().sendFormPost(
    this, this._requestStatusChanged,
    actionForm, params, headerParams);
}

TrPage.prototype._requestStatusChanged = function(requestEvent)
{
  if (requestEvent.getStatus() == TrXMLRequestEvent.STATUS_COMPLETE)
  {
    var statusCode = requestEvent.getResponseStatusCode();
    
    // The server might not return successfully, for example if an
    // exception is thrown.  When that happens, a non-200 (OK) status
    // code is returned as part of the HTTP prototcol.
    if (statusCode == 200)
    {
      _pprStopBlocking(window);

      if (requestEvent.isPprResponse())
      {
        var responseDocument = requestEvent.getResponseXML();

        // Though not yet supported, Nokia browser sometimes fails to get
        // XML content. (Currently being investigated.) When that happens,
        // the function should simply return without calling
        // _handlePprResponse rather than crushing the function with null
        // pointer reference.
        // This is a temporary workaround and should be revisited when
        // Nokia browser is officially supported.
        if (responseDocument != null)
        {
          this._handlePprResponse(responseDocument.documentElement);
        }
      }
      else
      {
        // Should log some warning that we got an invalid response
      }
    }
    else if (statusCode >= 400)
    {
      // The RequestQueue logs these for us, so
      // we don't need to take action here.  IMO, that's probably
      // wrong - we should do the handling here
      _pprStopBlocking(window);
    }

  }
}

TrPage.prototype._handlePprResponse = function(documentElement)
{
  var rootNodeName = TrPage._getNodeName(documentElement);
  
  if (rootNodeName == "content")
  {
    // Update the form action
    this._handlePprResponseAction(documentElement);

    var childNodes = documentElement.childNodes;
    var length = childNodes.length;
    
    for (var i = 0; i < length; i++)
    {
      var childNode = childNodes[i];
      var childNodeName = TrPage._getNodeName(childNode);
      
      if (childNodeName == "fragment")
      {     
        this._handlePprResponseFragment(childNode);  
      }
      else if (childNodeName == "script")
      {
        this._handlePprResponseScript(childNode);
      }
      else if (childNodeName == "script-library")
      {
        this._handlePprResponseLibrary(childNode);
      }
    }
  }
  else if (rootNodeName == "redirect")
  {
    var url = TrPage._getTextContent(documentElement);
    // TODO: fix for portlets???
    window.location.href = url;
  }
  else if (rootNodeName == "error")
  {
    var nodeText = TrPage._getTextContent(documentElement);
    // This should not happen - there should always be an error
    // message
    if (nodeText == null)
      nodeText = "Unknown error during PPR";
    alert(nodeText);
  }  
  else if (rootNodeName == "noop")
  {
    // No op
  }
  else
  {
    // FIXME: log an error
    window.location.reload(true);
  }
}

TrPage.prototype._addResetFields = function(formName, resetNames)
{
  // Create the necessary objects
  var resetFields = this._resetFields;
  if (!resetFields)
  {
    resetFields = new Object();
    this._resetFields = resetFields;
  }

  var formReset = resetFields[formName];
  if (!formReset)
  {
    formReset = new Object();
    resetFields[formName] = formReset;
  }

  // Store "name":true in the map for each such item
  for (var i = 0; i < resetNames.length; i++)
  {
    formReset[resetNames[i]] = true;
  }
}

TrPage.prototype._resetHiddenValues = function(form)
{
  var resetFields = this._resetFields;
  if (resetFields)
  {
    var formReset = resetFields[form.getAttribute("name")];
    if (formReset)
    {
      for (var k in formReset)
      {
        var currField = form[k];
        // Code previously used form.elements.currField
        // on PocketIE.  Dunno why the prior form would ever fail.
        if (!currField)
          currField = form.elements.currField;

        if (currField)
          currField.value = '';
      }
    }
  }
}

/**
 * Add reset callbacks for a form:
 * @param formName the name of the form
 * @param callMap a map from clientId to the JS function
 *   that will reset that component
 */
TrPage.prototype._addResetCalls = function(
  formName,
  callMap)
{
  // Create the necessary objects
  var resetCalls = this._resetCalls;
  if (!resetCalls)
  {
    resetCalls = new Object();
    this._resetCalls = resetCalls;
  }

  var formReset = resetCalls[formName];
  if (!formReset)
  {
    formReset = new Object();
    resetCalls[formName] = formReset;
  }

  // NOTE: this map is never pruned.  Ideally, it would be,
  // but in practice this is unlikely to be a major concern
  for (var k in callMap)
  {
    formReset[k] = callMap[k];
  }
}

/**
 * Callback used by Core.js resetForm() function
 * TODO: remove entire Core.js code, move to public TrPage.resetForm()
 * call.
 */
TrPage.prototype._resetForm = function(form)
{
  var resetCalls = this._resetCalls;
  if (!resetCalls)
    return false;
  var formReset = resetCalls[form.getAttribute("name")];
  if (!formReset)
    return false;

  var doReload = false;
  for (var k in formReset)
  {
    var trueResetCallback = unescape(formReset[k]);
    if (eval(trueResetCallback))
      doReload = true;
  }
  
  return doReload;
}

// TODO move to agent code
TrPage._getNodeName = function(element)
{
  var nodeName = element.nodeName;
  if (!nodeName)
    nodeName = element.tagName;
  return nodeName;
}


// Update the form with the new action provided in the response
TrPage.prototype._handlePprResponseAction = function(contentNode)
{
  var action = contentNode.getAttribute("action");

  if (action)
  {
    var doc = window.document;    

    // Replace the form action used by the next postback
    // Particularly important for PageFlowScope which might
    // change value of the pageflow scope token url parameter.    
    // TODO: track submitted form name at client, instead of
    // just updating the first form
    doc.forms[0].action = action;
  }

  // TODO: support Portal
}

// Handles a single fragment node in a ppr response.
TrPage.prototype._handlePprResponseFragment = function(fragmentNode)
{
  var doc = window.document;
  var targetNode;
  var activeNode;
  var refocusId = null;

  // Due to the limitation of DOM implementation of WM6,
  // a special code is needed here.
  // Note: This code segment is a good candidate to be emcapsulated in to
  // either WM6-specific agent or TrPage subclass.
  if (_agent.isWindowsMobile6)
  {
    // Get the first child node in fragmentNote
    var firstFragmenChildNode = fragmentNode.childNodes[0];
    if (!firstFragmenChildNode)
       return;

    var outerHTML = firstFragmenChildNode.data;

    // Windows Mobile 6 requires the element to be a child of
    // document.body to allow setting its innerHTML property.
    tempDiv = doc.createElement("div");
    tempDiv.id = "tempDiv";
    tempDiv.hidden = "true";

    var bodyElement = doc.body;

    // Temporarily append the new DIV element containing
    // the data in the first child of the fragement to body
    bodyElement.appendChild(tempDiv);
    tempDiv.innerHTML = outerHTML;

    var sourceNode = TrPage._getFirstElementWithId(tempDiv);

    var targetNode = _getElementById(doc, sourceNode.id);
    if (!targetNode)
    {
      return;
    }

    activeNode = _getActiveElement();
    if (activeNode && TrPage._isDomAncestorOf(activeNode, targetNode))
      refocusId = activeNode.id;

    // Insert the new element
    targetNode.parentNode.insertBefore(sourceNode, targetNode);

    // Remove the element to be replaced
    // innderHTML needs to be reset to work around the problem
    // of WM6 DOM. removeChild is not sufficient.
    targetNode.innerHTML = "";
    targetNode.parentNode.removeChild(targetNode);

    // Remove the temporary element
    tempDiv.innerHTML = "";
    bodyElement.removeChild(tempDiv);
  }
  else
  {
    // Convert the content of the fragment node into an HTML node that
    // we can insert into the document
    var sourceNode = this._getFirstElementFromFragment(fragmentNode);

    // In theory, all fragments should have one element with an ID.
    // Unfortunately, the PPRResponseWriter isn't that smart.  If
    // someone calls startElement() with the write component, but never
    // passed an ID, we get an element with no ID.  And, even
    // worse, if someone calls startElement() with a <span> that
    // never gets any attributes on it, we actually strip that
    // span, so we can get something that has no elements at all!
    if (!sourceNode)
       return;

    // Grab the id of the source node - we need this to locate the
    // target node that will be replaced
    var id = sourceNode.getAttribute("id");
    // As above, we might get a node with no ID.  So don't crash
    // and burn, just return.
    if (!id)
      return;

    // Find the target node
    targetNode = _getElementById(doc, id);
    activeNode = _getActiveElement();
    if (activeNode && TrPage._isDomAncestorOf(activeNode, targetNode))
      refocusId = activeNode.id;
    // replace the target node with the new source node
    if (targetNode)
      targetNode.parentNode.replaceChild(sourceNode, targetNode);
  }
  // Call all the DOM replace listeners
  var listeners = this._domReplaceListeners;
  if (listeners)
  {
    for (var i = 0; i < listeners.length; i+=2)
    {
      var currListener = listeners[i];
      var currInstance = listeners[i+1];
      if (currInstance != null)
        currListener.call(currInstance, targetNode, sourceNode);
      else
        currListener(targetNode, sourceNode);
    }
  }

  // TODO: handle nodes that don't have ID, but do take the focus?
  if (refocusId)
  {
    activeNode = doc.getElementById(refocusId);
    if (activeNode && activeNode.focus)
    {
      activeNode.focus();
      window._trActiveElement = activeNode;
    }
  }
}


/**
 * Return true if "parent" is an ancestor of (or equal to) "child"
 */
TrPage._isDomAncestorOf = function(child, parent)
{
  while (child)
  {
    if (child == parent)
      return true;
    var parentOfChild = child.parentNode;
    // FIXME: in DOM, are there ever components whose
    // parentNode is themselves (true for window objects at times)
    if (parentOfChild == child)
      break;
    child = parentOfChild;
  }
  
  return false;
}


/**
 * Replaces the a dom element contained in a peer. 
 * 
 * @param newElement{DOMElement} the new dom element
 * @param oldElement{DOMElement} the old dom element
 */
TrPage.prototype.__replaceDomElement = function(newElement, oldElement)
{
  oldElement.parentNode.replaceChild(newElement, oldElement);
}
  
// Extracts the text contents from a rich response fragment node and 
// creates an HTML element for the first element that is found.
TrPage.prototype._getFirstElementFromFragment = function(fragmentNode)
{
  // Fragment nodes contain a single CDATA section
  var fragmentChildNodes = fragmentNode.childNodes;
  // assert((fragmentChildNodes.length == 1), "invalid fragment child count");

  var cdataNode = fragmentNode.childNodes[0];
  // assert((cdataNode.nodeType == 4), "invalid fragment content");
  // assert(cdataNode.data, "null fragment content");

  // The new HTML content is in the CDATA section.
  // TODO: Is CDATA content ever split across multiple nodes?
  var outerHTML = cdataNode.data;

  // We get our html node by slamming the fragment contents into a div.
  var doc = window.document;  
  var div = doc.createElement("div");

  // Slam the new HTML content into the div to create DOM
  div.innerHTML = outerHTML;
  
  return TrPage._getFirstElementWithId(div);
  
}

// Returns the first element underneath the specified dom node
// which has an id.
TrPage._getFirstElementWithId = function(domNode)
{

  var childNodes = domNode.childNodes;
  var length = childNodes.length;
  
  for (var i = 0; i < length; i++)
  {
    var childNode = childNodes[i];
    if (childNode.id)
    {
      if (!_agent.supportsNodeType)
      {
        return childNode;
      }
      // Check for ELEMENT nodes (nodeType == 1) if nodeType is
      // supported
      else if (childNode.nodeType == 1)
      {
        return childNode;
      }
    }
    return TrPage._getFirstElementWithId(childNode);
  }

  return null;
}

TrPage.prototype._loadScript = function(source)
{
  // Make sure we only load each library once
  var loadedLibraries = this._loadedLibraries;
  if (loadedLibraries[source])
    return;
    
  loadedLibraries[source] = true;
  var xmlHttp = new TrXMLRequest();
  xmlHttp.setSynchronous(true);
  xmlHttp.send(source, null);
  if (xmlHttp.getStatus() == 200)
  {
    var responseText = xmlHttp.getResponseText();
    if (responseText)
    {
      if (_agent.isIE)
        window.execScript(responseText);
      else
        window.eval(responseText);
    }
  }

  // Clean to prevent memory leak
  xmlHttp.cleanup();
}

// Handles a single script node in a rich response
TrPage.prototype._handlePprResponseScript = function(scriptNode)
{
  var source = scriptNode.getAttribute("src");
  if (source)
  {
    this._loadScript(source);
  }
  else
  {
    var nodeText = TrPage._getTextContent(scriptNode);
    if (nodeText)
    {
      if (_agent.isIE)
        window.execScript(nodeText);
      else
        window.eval(nodeText);
    }
  }
}

TrPage.prototype._handlePprResponseLibrary = function(scriptNode)
{
  var nodeText = TrPage._getTextContent(scriptNode);
  this._loadScript(nodeText);
}

// TODO: move to agent API
TrPage._getTextContent = function(element)
{
  if (_agent.isIE)
  {
    // NOTE: this only works if it is an element, not some other DOM node
    var textContent = element.innerText;
    if (textContent == undefined)
      textContent = element.text;
        
    return textContent;
  }

  // Safari doesn't have "innerText", "text" or "textContent" - 
  // (at least not for XML nodes).  So sum up all the text children
  if (_agent.isSafari)
  {
    var text = "";
    var currChild = element.firstChild;
    while (currChild)
    {
      var nodeType = currChild.nodeType;
      if ((nodeType == 3) || (nodeType == 4))
        text = text + currChild.data;
      currChild = currChild.nextSibling;
    }

    return text;
  }

  return element.textContent;
}

TrPage._collectLoadedLibraries = function()
{
  if (!_agent.supportsDomDocument)
  {
    // There is not enough DOM support (e.g. document object does not
    // implement essential properties/methods such as body, documentElement
    // and firstChild) and it is not possible to implement this function.
    return null;
  }
  else
  {
    var loadedLibraries = new Object();

  // We use document.getElementsByTagName() to locate all scripts
  // in the page.  In theory this could be slow if the DOM is huge,
  // but so far seems extremely efficient.
    var domDocument = window.document;
    var scripts = domDocument.getElementsByTagName("script");

    if (scripts != null)
    {
      for (var i = 0; i < scripts.length; i++)
      {
        // Note: we use node.getAttribute("src") instead of node.src as
        // FF returns a fully-resolved URI for node.src.  In theory we could
        // fully resolve/normalize all script src values (both here and
        // in rich responses), but this seems like overkill.  Instead, we
        // just use whatever value happens to show up in the HTML src attribute,
        // whether it is fully resolved or not.  In theory this could mean that
        // we could evalute a library an extra time (if it appears once fully
        // resolved and another time as a relative URI), but this seems like
        // an unlikely case which does not warrant extra code.

        var src = scripts[i].getAttribute("src");

        if (src)
        {
          loadedLibraries[src] = true;
        }
      }
    }
    return loadedLibraries;
  }
}

/**
 * Adds a listener for DOM replacement notification.
 * @param {function} listener listener function to add
 * @param {object} instance to pass as "this" when calling function (optional)
 */
TrPage.prototype.addDomReplaceListener = function(listener, instance)
{
  var domReplaceListeners = this._domReplaceListeners;
  if (!domReplaceListeners)
  {
    domReplaceListeners = new Array();
    this._domReplaceListeners = domReplaceListeners;
  }

  domReplaceListeners.push(listener);
  domReplaceListeners.push(instance);
}

/**
* Removes a listener for DOM replace notifications.
* @param {function} listener  listener function to remove
* @param {object} instance to pass as this when calling function
*/
TrPage.prototype.removeDomReplaceListener = function(listener, instance)
{
  // remove the listener/instance combination
  var domReplaceListeners = this._domReplaceListeners;
  var length = domReplaceListeners.length;
  
  for (var i = 0; i < length; i++)
  {
    var currListener = domReplaceListeners[i];
    i++;
    
    if (currListener == listener)
    {
      var currInstance = domReplaceListeners[i];
      if (currInstance === instance)
      {
        domReplaceListeners.splice(i - 1, 2);
        break;
      }
    }
  }
  
  // remove array, if empty
  if (domReplaceListeners.length == 0)
  {
    this._domReplaceListeners = null;
  }
}
 
/**
 * Adds the styleClassMap entries to the existing internal
 * styleClassMap. Styles can then be accessed via the 
 * getStyleClass function.
 * @param styleClassMap() {key: styleClass, ...}
 */
TrPage.prototype.addStyleClassMap = function(styleClassMap)
{
  if (!styleClassMap)
    return;

  if (!this._styleClassMap)
    this._styleClassMap = new Object();

  // Copy key:styleClass pairs to internal map
  for (var key in styleClassMap)
    this._styleClassMap[key] = styleClassMap[key];
}
 
/**
 * Return the styleClass for the given key.
 * @param key(String) Unique key to retrieve the styleClass
 * @return (String) The styleClass, or undefined if not exist
 */
TrPage.prototype.getStyleClass = function(key)
{
  if (key && this._styleClassMap)
  {
    var mapped = this._styleClassMap[key];
    if (mapped)
      return mapped;
  }

  return key;
}

/**
 * Causes a partial submit to occur on a given component.  The specified
 * component will always be validated first (if appropriate), then optionally 
 * the whole form, prior to submission.
 * @param formId(String) Id of the form to partial submit.
 * @param inputId(String) Id of the element causing the partial submit.  If this
 * element fails validation the partial submit will not be performed.
 * @param event(Event) The javascript event object.
 * @param validateForm(boolean) true if the whole form should be validated.
 */
TrPage._autoSubmit = function(formId, inputId, event, validateForm, params)
{
  if (_agent.isIE)
  {
    // in many forms there is a hidden field named "event"
    // Sometimes IE gets confused and sends us that instead of
    // the true event, so...
    if (event["type"] == "hidden")
      event = window.event;
  }

  // If onchange is used for validation, then first validate 
  // just the current input
  var isValid = true;
  if (_TrEventBasedValidation)
    isValid = _validateInput(event, true);

  // Only proceed if the current input is valid
  if (isValid)
  {
    if (!params)
      params = new Object();
    params.event = "autosub";
    params.source = inputId;
  
    _submitPartialChange(formId, validateForm, params);
  }
}
