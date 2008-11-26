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


/**
* TrXMLRequest class is a low-level XML HTTP Request
* wrapper
**/
/**
* Default constructor. Creates an asynchronous XML HTTP request
**/
function TrXMLRequest()
{
  this.isSynchronous = false;
  this.callback = null;
  this._state = TrXMLRequest.UNINITIALIZED;
  this.headers = new Object();
  this.xmlhttp = TrXMLRequest._createXmlHttpRequest();
}


/**
* Request state constants. See getCompletionState()
**/
TrXMLRequest.UNINITIALIZED = 0;
TrXMLRequest.LOADING = 1;
TrXMLRequest.LOADED = 2;
TrXMLRequest.INTERACTIVE = 3;
TrXMLRequest.COMPLETED = 4;

/**
* Specifies whether the request should be synchronous
* Parameters: isSynch - true if request should be synchronous,
* false otherwise
**/
TrXMLRequest.prototype.setSynchronous = 
function (isSynch)
{
  this.isSynchronous = isSynch;
};

/**
* Registers request callback for asynchronous requests
* The callback will be called each time the state of the
* request changes (see getCompletionState())
* The callback should have the following siganture:
* <void> function (<TrXMLRequest>request)
**/
TrXMLRequest.prototype.setCallback = 
function (_callback)
{
  this.callback = _callback;
};

/**
* Returns request's completion state (see Request state
* constants)
**/
TrXMLRequest.prototype.getCompletionState =
function()
{
  return this._state;
};

/**
 * Returns the HTTP response status.  For example, 200 is OK.
 */
TrXMLRequest.prototype.getStatus =
function()
{
  return this.xmlhttp.status;
}

/**
* Returns the response as an XML document
* Note: this method will block if the the request is asynchronous and
* has not yet been completed
**/
TrXMLRequest.prototype.getResponseXML = 
function()
{
  return this.xmlhttp.responseXML;
}

/**
* Returns the response as text
* Note: this method will block if the the request is asynchronous and
* has not yet been completed
**/
TrXMLRequest.prototype.getResponseText = 
function()
{
  return this.xmlhttp.responseText;
}

/**
* Sends an XML HTTP request
* Parameters: 
* url - destination URL
* content - XML document or string that should be included in the request's body
**/
TrXMLRequest.prototype.send =
function(url, content)
{
  var xmlhttp = this.xmlhttp;
  if (!this.isSynchronous)
  {
    var cb = new Function("arguments.callee.obj.__onReadyStateChange();");
    cb.obj = this;
    xmlhttp.onreadystatechange  = cb;
  }
  
  var method = content ? "POST" : "GET";
  xmlhttp.open(method, url, !this.isSynchronous);
  for (var name in this.headers)
    xmlhttp.setRequestHeader(name, this.headers[name]);

  // Set some header to indicate the request initiated from
  // the Trinidad XHR request
  // =-ags This needs to be revisited
  xmlhttp.setRequestHeader("Tr-XHR-Message", "true");

  xmlhttp.send(content);
  if (this.isSynchronous)
  {
    this._state = xmlhttp.readyState;
  }
}


TrXMLRequest.prototype.getResponseHeader = 
function(name)
{

  return this.xmlhttp.getResponseHeader(name);
}

TrXMLRequest.prototype.getAllResponseHeaders = 
function()
{
  return this.xmlhttp.getAllResponseHeaders();
}

TrXMLRequest.prototype.setRequestHeader = 
function(name, value)
{
  this.headers[name] = value;
}

TrXMLRequest._createXmlHttpRequest = function()
{
  var xmlhttp;

  if (window.XMLHttpRequest)
  {
    xmlhttp = new XMLHttpRequest();
  }
  else if (window.ActiveXObject)
  {
    try
    {
      xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e)
    {
      try
      {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
      }
      catch (e)
      {
      }
    }
  }
  return xmlhttp;
}


TrXMLRequest.prototype.__onReadyStateChange =
function()
{
  this._state = this.xmlhttp.readyState;
  if (this.callback)
    this.callback(this);
}

TrXMLRequest.prototype.cleanup =function()
{
  this.callback = null
  delete this.xmlhttp;
}


