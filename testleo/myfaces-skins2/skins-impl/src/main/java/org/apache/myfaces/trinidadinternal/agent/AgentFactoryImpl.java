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
package org.apache.myfaces.trinidadinternal.agent;

import java.util.Collections;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.myfaces.trinidad.context.Agent;
import org.apache.myfaces.trinidad.logging.TrinidadLogger;


/**
 * Trinidad implementation of AgentFactory.
 */
public class AgentFactoryImpl implements AgentFactory
{


  public Agent createAgent(Map<String, String> headerMap)
  {
    // this method primarily exists for use during testing
    
    AgentImpl agent = new AgentImpl();
    _populateAgentImpl(headerMap,agent);
    return agent;
  }

  @SuppressWarnings("unchecked")
  public Agent createAgent(FacesContext facesContext)
  {
    AgentImpl agent = new AgentImpl();

    // Get the RequestHeaderMap to help populate the agent
    Map<String, String> headerMap;
    if (facesContext != null)
    {
      headerMap = facesContext.getExternalContext().getRequestHeaderMap();
    }
    else
    {
      headerMap = Collections.emptyMap();
    }

    //TODO: Add declarative and extensible means for populating AgentImpl object
    // the RequestHeaderMap helps populate the agent
    _populateAgentImpl(headerMap, agent);

    return agent;
  }

  // The headerMap is the RequestHeaderMap from the externalContext. It is
  // consulted to correctly populate the agent
  private void _populateAgentImpl(Map<String, String> headerMap, AgentImpl agent)
  {
    
    String userAgent = headerMap.get("User-Agent");

    if ((userAgent != null) && userAgent.startsWith("PTG"))
    {
      _populateIaswAgentImpl(userAgent,
                             headerMap.get(_IASW_DEVICE_HINT_PARAM),agent);
      return;
    }

    String accept = headerMap.get("Accept");

    // See if the agent wants WML - if so, we're talking WAP.
    if ((accept != null) &&
        accept.regionMatches(true, 0, "vnd.wap.wml", 0, 11))
    {
      _populateWAPAgentImpl(agent);
      return;
    }

    if (userAgent == null)
    {
      _populateUnknownAgentImpl(null, agent);
      return;
    }
    
    //the useragent string for telnet and PDA design time will start with
    //OracleJDevMobile because in each of these cases we know we have an
    //exact match in the device repository for the agent name.  This is
    //because the jdev design time and ITS runtime have access to the same
    //device repository as the Trinidad runtime
    //The PDA DT useragent string will be: OracleJDevMobile/PDA/[agentName]
    //The telnet DT and RT useragent string will be:
    //OracleJDevMobile/ITS/[agentName]
    if (userAgent.startsWith("OracleJDevMobile"))
    {
      _populateJDevMobileAgentImpl(userAgent,agent);
      return;
    }
    if (userAgent.startsWith("OracleITS"))
    {
      _populateTelnetAgentImpl(userAgent,agent);
      return;
    }
    if (userAgent.startsWith("Pixo-Browser"))
    {
       _populatePixoAgentImpl(userAgent,agent);
       return;
    }

    if (userAgent.startsWith("ICE Browser"))
    {
       _populateIceAgentImpl(userAgent,agent);
       return;
    }

    // I need to search for more than just "WebPro" because ie's header can be
    // altered to include "FunWebProducts" in which case ie desktop was being
    // mapped to a pda
    // see bug 3362116
    if (userAgent.indexOf("NetFront/3.") != -1)
    {
      _populateNetFrontAgentImpl(userAgent,agent);
      return;
    }

    // Web Pro
    //userAgent = "Mozilla/4.76 (compatible; MSIE 6.0; U; Windows 95; PalmSource; PalmOS; WebPro; Tungsten Proxyless 1.1 320x320x16)";
    if ( (userAgent.indexOf( "WebPro") != -1 &&
          userAgent.indexOf("Palm")!= -1)||
         userAgent.indexOf("Blazer/3.") != -1)
    {
      _populatePalmWebBrowserProAgentImpl(userAgent,agent);
      return;
    }

/*  //Commenting - Confirm this pattern fornBlazer before uncommenting
    //and remove the Blazer 3.0 check in the previous if
    if (((userAgent.indexOf("Blazer/4.") != -1) ||
        (userAgent.indexOf("Blazer 3.") != -1)) &&
        ((userAgent.indexOf("Palm") != -1)))
    {
      return _getPalmBlazerAgentEntry(userAgent);
    }
*/

    //PPC 02
    //userAgent = "Mozilla/2.0 (compatible; MSIE 3.02; Windows CE; PPC; 240x320)";
    // PPC 03
    //userAgent = "Mozilla/4.0 (compatible; MSIE 4.01; Windows CE; PPC; 240x320)";
    if (userAgent.indexOf("Windows CE") != -1)
    {
      // for PocketPC and Windows Mobile, try to grab the header UA-pixels to
      // determine width/height
      String uaPixels = headerMap.get("UA-pixels");
      _populatePocketPCAgentImpl(userAgent,uaPixels,agent);
      return;
    }

    // This needs to be before check for mozilla!
    if ((userAgent.indexOf("PalmOS") != -1) ||
        (userAgent.indexOf("Blazer") != -1) ||
        (userAgent.indexOf("Xiino") != -1))
    {
      _populatePalmAgentImpl(userAgent,agent);
      return;
    }

    if ((userAgent.indexOf("AppleWebKit") != -1) ||
        (userAgent.indexOf("Safari") != -1))
    {
      _populateSafariAgentImpl(userAgent,agent);
      return;
    }

    if (userAgent.indexOf("Gecko/") != -1)
    {
      _populateGeckoAgentImpl(userAgent,agent);
      return;
    }
    // must check for gecko before checking for mozilla:
    else if (userAgent.startsWith("Mozilla"))
    {
      _populateMozillaAgentImpl(userAgent,agent);
      return;
    }

    if (userAgent.startsWith("Opera"))
    {
        _populateOperaAgentImpl(userAgent,agent);
        return;
    }
    
    if(userAgent.startsWith("BlackBerry"))
    {
        _populateBlackberryAgentImpl(userAgent,agent);
        return;
    }

    _populateUnknownAgentImpl(userAgent, agent);
  }

  private void _populateUnknownAgentImpl(String userAgent, AgentImpl agent)
  {
    // Log warning message that we are setting the agent entry to unknown attributes
    _LOG.warning("UNKNOWN_AGENT_ATTRIBUTES_CREATE_WITH_UNKNOWN", userAgent);
    agent.setAgentEntryToNULL();
    agent.setAgent(_UNKNOWN);
    agent.setType(_UNKNOWN);
    agent.setAgentVersion(_UNKNOWN);
    agent.setPlatform(_UNKNOWN);
    agent.setPlatformVersion(_UNKNOWN);
    agent.setMakeModel(_UNKNOWN);
  }

  //populates the agent entry for DT access for either Telnet or PDA
  //for jdev mobile there are two user agent strings possible:
  //1. OracleJDevMobile_PDA(DeviceName:[name of device])
  //2. OracleJDevMobile_ITS(DeviceName:[name of device])
  private void _populateJDevMobileAgentImpl(String agent,AgentImpl agentObj)
  {
    //the form of JDEVMobile user agent string will be:
    //OracleJDevMobile_[PDA or ITS]/[version](DeviceName:[device name];[capability1]:[capability 1 value];...)

    boolean returnUnknownAgentObj = false;
    int itsIndex = agent.indexOf("ITS");
    int pdaIndex = agent.indexOf("PDA");
    int versionStartIndex = -1;
    if (itsIndex > -1)
    {
      agentObj.setType(Agent.TYPE_TELNET);
      versionStartIndex = "OracleJDevMobile_ITS".length()+1;
    }
    else if (pdaIndex > -1)
    {

      agentObj.setType(Agent.TYPE_PDA);
      versionStartIndex = "OracleJDevMobile_PDA".length()+1;
    }
    else
    {
      returnUnknownAgentObj = true;
    }
    //Now find the name of the device
    if (!returnUnknownAgentObj){
      int versionEndIndex = agent.indexOf("(");
      String version = agent.substring(versionStartIndex,versionEndIndex);
      agentObj.setAgentVersion(version);
      //parse agentName
      int agentNameStartIndex = agent.indexOf(":",versionEndIndex) +1;
      //find end of agentName (ie. when we see a semicolon
      int agentNameEndIndex = agentNameStartIndex;
      for (;agent.charAt(agentNameEndIndex)!=';' &&
          agent.charAt(agentNameEndIndex) !=')';
          agentNameEndIndex++);
      String agentName = agent.substring(agentNameStartIndex,agentNameEndIndex);
      agentObj.setAgent(agentName);
      if (agent.charAt(agentNameEndIndex) == ')')
        return;
      //now parse remaining request specific capabilities
      int capabilityNameStartIndex;
      int capabilityNameEndIndex;
      int capabilityValueStartIndex;
      int capabilityValueEndIndex = agentNameEndIndex;
      while(agent.charAt(capabilityValueEndIndex)!= ')')
      {
        capabilityNameStartIndex = capabilityValueEndIndex + 1;
        capabilityNameEndIndex = agent.indexOf(":",capabilityNameStartIndex);
        String capabilityName = agent.substring(capabilityNameStartIndex,capabilityNameEndIndex);
        capabilityValueStartIndex = capabilityNameEndIndex +1;
        capabilityValueEndIndex = agent.indexOf(";",capabilityValueStartIndex);
        if (capabilityValueEndIndex == -1)
        {
          capabilityValueEndIndex = agent.indexOf(")",capabilityValueEndIndex);
        }
        String capabilityValue = agent.substring(capabilityValueStartIndex,capabilityValueEndIndex);
        agentObj.__addRequestCapability(CapabilityKey.getCapabilityKey(capabilityName,true),capabilityValue);
      }
    }
    if (returnUnknownAgentObj)
    {
      _populateUnknownAgentImpl(agent, agentObj);
    }
  }

  private void _populateTelnetAgentImpl(String agent,AgentImpl agentObj)
  {
    //the form of an ITS user agent will be
    //OracleITS/[version](DeviceName:[device name];[capability1]:[capability 1 value];...)
    agentObj.setType(Agent.TYPE_TELNET);
    int versionStartIndex = "OracleITS".length() +1;
    int versionEndIndex = agent.indexOf("(");
    String version = agent.substring(versionStartIndex,versionEndIndex);
    agentObj.setAgentVersion(version);
    //parse agentName
    int agentNameStartIndex = agent.indexOf(":",versionEndIndex) +1;
    //find end of agentName (ie. when we see a semicolon
    int agentNameEndIndex = agentNameStartIndex;
    for (;agent.charAt(agentNameEndIndex)!=';' &&
          agent.charAt(agentNameEndIndex) !=')';
          agentNameEndIndex++);
    String agentName = agent.substring(agentNameStartIndex,agentNameEndIndex);
    agentObj.setAgent(agentName);
    if (agent.charAt(agentNameEndIndex) == ')')
      return;
    //now parse remaining request specific capabilities
    int capabilityNameStartIndex;
    int capabilityNameEndIndex;
    int capabilityValueStartIndex;
    int capabilityValueEndIndex = agentNameEndIndex;
    while(agent.charAt(capabilityValueEndIndex)!= ')')
    {
      capabilityNameStartIndex = capabilityValueEndIndex + 1;
      capabilityNameEndIndex = agent.indexOf(":",capabilityNameStartIndex);
      String capabilityName = agent.substring(capabilityNameStartIndex,capabilityNameEndIndex);
      capabilityValueStartIndex = capabilityNameEndIndex +1;
      capabilityValueEndIndex = agent.indexOf(";",capabilityValueStartIndex);
      if (capabilityValueEndIndex == -1)
      {
        capabilityValueEndIndex = agent.indexOf(")",capabilityValueEndIndex);
      }
      String capabilityValue = agent.substring(capabilityValueStartIndex,capabilityValueEndIndex);
      agentObj.__addRequestCapability(CapabilityKey.getCapabilityKey(capabilityName,true),capabilityValue);
    }
  }

  /**
   * populates data from a PocketPC IE request
   */
  private void _populatePocketPCAgentImpl(String agent,String uaPixels,AgentImpl agentObj)
  {
    int start = agent.indexOf("MSIE");
    String version = null;

    if (start > -1)
    {
      version = _getVersion(agent, start + "MSIE".length());
    }
    agentObj.setType(Agent.TYPE_PDA);
    agentObj.setAgent(Agent.AGENT_IE);
    agentObj.setAgentVersion(version);
    agentObj.setPlatform(Agent.PLATFORM_PPC);
    
    if(uaPixels != null && uaPixels.length() > 0)
    {
      // UA-pixels is defined as <width>x<height>
      // UA-pixels was proposed here
      // http://www.watersprings.org/pub/id/draft-mutz-http-attributes-00.txt
      // and it is used by Pocket IE and IE Mobile; see
      // http://blogs.msdn.com/iemobile/archive/2006/08/03/Detecting_IE_Mobile.aspx
      Integer width = null;
      Integer height = null;

      String[] parts = uaPixels.split("x");
      if(parts.length == 2)
      {
        try
        {
          width = new Integer(parts[0]);
          height = new Integer(parts[1]);
        }
        catch(NumberFormatException ex)
        {
          _LOG.fine(ex);
        }
      }
      
      if(width != null && height != null)
      {
        agentObj.__addRequestCapability(TrinidadAgent.CAP_WIDTH,width);
        agentObj.__addRequestCapability(TrinidadAgent.CAP_HEIGHT,height);
      }
      else
      {
        _LOG.fine("When creating the Agent, the UA-pixels value \"{0}\" could not be parsed.", uaPixels);
      }
    }
  }
  
    /**
     * populates data from a Blackberry browser request
     */
    private void _populateBlackberryAgentImpl(String agent,AgentImpl agentObj)
    {
      // the  User-Agent string for the BlackBerry Browser starts with
      // BlackBerry<model>/<version> where <model> is the BlackBerry
      // model, e.g. for the BlackBerry browser 4.1.0 on
      // the BlackBerry 8700 device, the User-Agent string begins with
      // BlackBerry8700/4.1.0
      
      int start = agent.indexOf("BlackBerry");
      
      String version = null;
      String makeModel = null;

      if (start > -1)
      {
        // find the first /, which occurs before the version number
        int slashLoc = agent.indexOf('/',start);
        if(slashLoc > -1)
        {
            // see the note above about how the User-Agent starts with
            // BlackBerry<model>/<version>; this returns the
            // BlackBerry<model> (e.g. BlackBerry8700), which we will
            // use as the Agent hardwareMakeModel
            makeModel = agent.substring(start,slashLoc);
            
            // _getVersion assumes the location of the slash is passed in,
            // and starts looking for the version at the NEXT character
            version = _getVersion(agent, slashLoc);
        }
      }
      
      // note that the agent and platform are both BLACKBERRY
      // this is because it is the BlackBerry Browser running on the
      // BlackBerry device
      agentObj.setType(Agent.TYPE_PDA);
      agentObj.setAgent(Agent.AGENT_BLACKBERRY);
      agentObj.setAgentVersion(version);
      agentObj.setPlatform(Agent.PLATFORM_BLACKBERRY);
      agentObj.setMakeModel(makeModel);
    }

  /**
   * returns the data for the Palm NetFront browser request
   */
  private void _populateNetFrontAgentImpl(String agent, AgentImpl agentObj)
  {
    agentObj.setType(Agent.TYPE_PDA);
    agentObj.setAgent(TrinidadAgent.AGENT_NETFRONT);

    int start = agent.indexOf("NetFront/");

    if (start > -1)
    {
      agentObj.setAgentVersion(_getVersion(agent, start + 8));
    }

    int paren = agent.indexOf('(');

    if (paren >= 0)
    {
      // try to determine the OS
      if (agent.indexOf("Windows CE", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_PPC);
      }
/*
      //Embedded Linux is not be same as Linux
      //so leave it as unknown
      else if (agent.indexOf("Embedix", paren) > 0)
      {
        entry._platform = Agent.PLATFORM_LINUX;
      }
*/
      else if (agent.indexOf("Palm", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_PALM);
      }
    }

  }

  /**
   * returns the data for the Palm Web Pro browser request
   */
  private void _populatePalmWebBrowserProAgentImpl(String agent,AgentImpl agentObj)
  {
    agentObj.setType(Agent.TYPE_PDA);
    agentObj.setAgent(TrinidadAgent.AGENT_WEBPRO);

    int start = agent.indexOf("WebPro/");

    if (start > -1)
    {
      agentObj.setAgentVersion(_getVersion(agent, start + 6));
    }

    agentObj.setPlatform(Agent.PLATFORM_PALM);

  }

  /**
   * returns the data for the Palm blazer browser request
   */
/* //comment for now
  private AgentEntry _getPalmBlazerAgentEntry(String agent)
  {
    AgentEntry entry = new AgentEntry();
    entry._type = TYPE_PDA;
    entry._agent = AdfFacesAgent.AGENT_BLAZER;

    //balzer 3 has "Blazer 3..." and 4.0 has Blazer 4/....
    int start = agent.indexOf("Blazer");

    if (start > -1)
    {
      entry._agentVersion = _getVersion(agent, start + 6);
    }

    entry._platform = Agent.PLATFORM_PALM;

    return entry;
  }
*/

  /**
   * returns the AgentEntry for ias wireless
   */
  private void _populateIaswAgentImpl(String agent, String wirelessType,AgentImpl agentObj)
  {
    // map device hint to agent type
    if (wirelessType == null)
    {
      _populateUnknownAgentImpl(agent, agentObj);
      return;
    }

    String version = _getVersion(agent, agent.indexOf('/'));
    agentObj.setType(Agent.TYPE_PHONE);
    agentObj.setAgent(TrinidadAgent.AGENT_PTG);
    agentObj.setAgentVersion(version);
  }

  /**
   * returns the AgentEntry for the Palm
   */
  private void _populatePalmAgentImpl(String userAgent,AgentImpl agentObj)
  {
    agentObj.setType(Agent.TYPE_PDA);

    if (userAgent.indexOf("Blazer") != -1)
      agentObj.setAgent(TrinidadAgent.AGENT_BLAZER);
    else if (userAgent.indexOf("Xiino") != -1)
      agentObj.setAgent(TrinidadAgent.AGENT_XIINO);

    agentObj.setPlatform(Agent.PLATFORM_PALM);

  }

  /**
   * returns the AgentEntry for the Ice brwoser
   */
  private void _populateIceAgentImpl(String agent,AgentImpl agentObj)
  {
    int slashIndex = agent.indexOf('/');
    agentObj.setType(Agent.TYPE_DESKTOP);
    agentObj.setAgent(TrinidadAgent.AGENT_ICE_BROWSER);
    agentObj.setAgentVersion(_getVersion(agent, slashIndex));
    agentObj.setPlatform(_getJavaOS(agent, slashIndex));
  }

  /**
   * returns the AgentEntry for the Pixo Microbrowser
   */
  private void  _populatePixoAgentImpl(String agent,AgentImpl agentObj)
  {
    agentObj.setType(Agent.TYPE_PHONE);
    agentObj.setAgent(TrinidadAgent.AGENT_PIXO);
    agentObj.setAgentVersion(_getVersion(agent, agent.indexOf('/')));
  }

  /**
   * Returns an AgentEntry for a WML client.
   */
  private void _populateWAPAgentImpl(AgentImpl agentObj)
  {
    //TODO: Add generic wmlbrowser when wml browsers are supported
    // Generic WML support
    agentObj.setType(Agent.TYPE_PHONE);
  }

  /**
   * Returns an AgentEntry for the browsers that use the Gecko Layout Engine.
   */
  private void _populateGeckoAgentImpl(String agent,AgentImpl agentObj)
  {
   //Identifying an Gecko Based agent as Gecko (and not Mozilla, Netscape, Firefox)
    //could be an issue
    //E.g User-Agent String
    //Mozilla/5.0 (Windows; U; Win 9x 4.90; en-US; rv:1.0.1) Gecko/20020823 Netscape/7.0
    //For Gecko based agents
    //    - Gecko uses Date string as a version number
    //    - Mozilla uses the revision number as the version number rv:x.x.x
    //    - Each vendor has a version number (like firefox/1.0,  Netscape/7.0)
    // Mozilla revision : Gecko Version
    //           1.0.1  : 20020826
    //           1.1    : 20020826
    //           1.2.1  : 20021130

    //Currently (in UIX 2.2 base agent Impl)
    //  - All Gecko Based agents are identified as Gecko
    //  - But PPR capability is determined is based on the Mozilla version number (rv:x.x.x)
    //  - For Major version , for Gecko, always "1" is returned
    //so stricly speaking all capabilities are not based on the layout engine.

    //New Impl.
    //  - Still using Gecko as the identifier for all gecko based agents
    //  - Still returning date string as version number of Gecko. But this can get messy to use
    //    by the applications. The version number could change everyday and may not be the same for
    //    different platforms.  (An alternate option is to return version number of Mozilla
    //    that would be equivalent to current browser for Gecko-based browsers)
    //  - But assumes PPR Support in all Gecko versions.

    //Change 2008-05-13:
    // We need the rv to support @agent versioning in CSS as the date makes no sense,
    // so look for the rv:, not the Gecko build date
    
    agentObj.setType(Agent.TYPE_DESKTOP);
    agentObj.setAgent(Agent.AGENT_GECKO);

    int start = agent.indexOf("rv:");
    if (start >= 0)
    {
      agentObj.setAgentVersion(_getVersion(agent, start + 2));
    }
    else
    {
      int geckoIndex = agent.indexOf("Gecko/");
      agentObj.setAgentVersion(agent.substring(geckoIndex+6, // skip over 'Gecko/'
              geckoIndex+14)); // always 8 chars length
    }

    int paren = agent.indexOf('(');

    if (paren >= 0)
    {
      // try to determine the OS
      if (agent.indexOf("Win", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_WINDOWS);
      }
      else if (agent.indexOf("Mac", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_MACOS);
      }
      else if (agent.indexOf("Linux", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_LINUX);
      }
      else if (agent.indexOf("Sun", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_SOLARIS);
      }
    }
  }
  
  /**
   * Returns an AgentEntry for the Opera browser.
   */
  
  private void _populateOperaAgentImpl(String agent,AgentImpl agentObj)
  {
    agentObj.setType(Agent.TYPE_DESKTOP);
    agentObj.setAgent(Agent.AGENT_GECKO);
    
    int operaIndex = agent.indexOf("Opera/");
    int firstSpace = agent.indexOf(" ");
    if (operaIndex >= 0 && firstSpace >=0 )
    {
        agentObj.setAgentVersion(agent.substring(operaIndex + 6,firstSpace));
    }

    int paren = agent.indexOf('(');

    if (paren >= 0)
    {
      // try to determine the OS
      if (agent.indexOf("Win", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_WINDOWS);
      }
      else if (agent.indexOf("Mac", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_MACOS);
      }
      else if (agent.indexOf("Linux", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_LINUX);
      }
      else if (agent.indexOf("Sun", paren) > 0)
      {
        agentObj.setPlatform(Agent.PLATFORM_SOLARIS);
      }
    }
  }

  /**
   * returns the AgentEntry for Safari
   */
  private void _populateSafariAgentImpl(String agent, AgentImpl agentObj)
  {
    int start = agent.indexOf("AppleWebKit");

    if (start < 0)
    {
      start = agent.indexOf("Safari");
    }

    if (start >= 0)
    {
      start = agent.indexOf('/', start);
    }

    if (agent.indexOf("iPhone") > 0)
    {
      // At the moment, all known iPhone and iPod touch agent strings contain "iPhone"
      agentObj.setPlatform(Agent.PLATFORM_IPHONE);
    }
    else if (agent.indexOf("Win") > 0)
    {
      // At the moment, this includes Safari and Google Chrome
      agentObj.setPlatform(Agent.PLATFORM_WINDOWS);
    }
    else if (agent.indexOf("Linux") > 0)
    {
      // At the moment, this includes Android
      agentObj.setPlatform(Agent.PLATFORM_LINUX);
    }
    else if (agent.indexOf("Mac") > 0)
    {
      // At the moment, this includes Safari
      agentObj.setPlatform(Agent.PLATFORM_MACOS);
    }

    String version = _getVersion(agent, start);
    agentObj.setType(Agent.TYPE_DESKTOP);
    agentObj.setAgent(Agent.AGENT_WEBKIT);
    agentObj.setAgentVersion(version);
  }

  /**
   * Returns an AgentEntry for the "Mozilla" family of browsers - which
   * most at least pretend to be.
   */
  private void _populateMozillaAgentImpl(String agent,AgentImpl agentObj)
  {
    int paren = agent.indexOf('(');
    agentObj.setType(Agent.TYPE_DESKTOP); //Is this default realli okay??? These days Mobile agents also use Mozilla/xx.xx

    // No section to qualify the agent;  assume Mozilla/Netscape
    if (paren == -1)
    {
      agentObj.setAgent(TrinidadAgent.AGENT_NETSCAPE);
      agentObj.setAgentVersion(_getVersion(agent, agent.indexOf('/')));
    }
    else
    {
      paren = paren + 1;

      boolean isJDevVE = agent.indexOf("JDeveloper", paren) > 0;
      boolean isJDevJSVE = agent.indexOf("JDeveloper JS", paren) > 0;

      if (agent.startsWith("compatible", paren))
      {
        int ieIndex = agent.indexOf("MSIE", paren);

        if (ieIndex < 0)
        {
          // check for Palm
          int palmIndex = agent.indexOf("Elaine", paren);

          if (palmIndex > 0)
          {
            agentObj.setType(Agent.TYPE_PDA);
            agentObj.setAgent(TrinidadAgent.AGENT_ELAINE);
            agentObj.setAgentVersion(_getVersion(agent, palmIndex));
            agentObj.setPlatform(Agent.PLATFORM_PALM);
          }
        }
        else
        {
          agentObj.setAgent(Agent.AGENT_IE);
          agentObj.setAgentVersion(_getVersion(agent, ieIndex + 4));
        }
      }
      else
      {
        agentObj.setAgent(TrinidadAgent.AGENT_NETSCAPE);
        agentObj.setAgentVersion(_getVersion(agent, agent.indexOf('/')));
      }

      // try to determine the OS, if unknown
      if (agentObj.getPlatformName() == null)
      {
        // Hack: treat the JDeveloper agent as Windows,
        // so that we assume IE 6.0 Windows capabilities
        if ((agent.indexOf("Win", paren) > 0) || isJDevVE)
        {
          agentObj.setPlatform(Agent.PLATFORM_WINDOWS);
        }
        else if (agent.indexOf("Mac", paren) > 0)
        {
          agentObj.setPlatform(Agent.PLATFORM_MACOS);
        }
        else if (agent.indexOf("Linux", paren) > 0)
        {
          agentObj.setPlatform(Agent.PLATFORM_LINUX);
        }
        else if (agent.indexOf("Sun", paren) > 0)
        {
          agentObj.setPlatform(Agent.PLATFORM_SOLARIS);
        }
      }

      if (isJDevVE)
      {
        agentObj.__addRequestCapability(TrinidadAgent.CAP_IS_JDEV_VE,
                                        Boolean.TRUE);
        if (isJDevJSVE)
        {
          agentObj.__addRequestCapability(TrinidadAgent.CAP_IS_JDEV_JAVASCRIPT_VE,
                                          Boolean.TRUE);
        }
          
      }
    }
  }


  /**
   * Returns the version contained within a string starting
   * at a certain location.  The version will contain only numeric
   * parts separated by dots (.).
   * @param base the string to pull the version from
   * @param start the index of the character BEFORE the version
   * @return the version string
   */
  private String _getVersion(String base, int start)
  {
    // start should be the character BEFORE the version portion
    // (typically a slash character after the agent name)
    
    if (start < 0)
    {
      return null;
    }

    int end = base.length();
    start = start + 1;

    for (int i = start; i < end; i++)
    {
      // Find the last non-numeric character; that'll
      // mark the end of the version
      char ch = base.charAt(i);

      if ((ch != '.') && ((ch < '0') || (ch > '9')))
      {
        return base.substring(start, i);
      }
    }

    return base.substring(start);
  }

  /**
   * Parse the OS string returned from java.System.
   * <p/>
   * Currently, only checks for Windows
   */
  private String _getJavaOS(String base, int start)
  {
    if (start < 0)
    {
      return null;
    }

    // check for Windows
    if (base.regionMatches(start, "Windows", 0, base.length() - start))
    {
      return Agent.PLATFORM_WINDOWS;
    }

    return null;
  }

  static final private String _IASW_DEVICE_HINT_PARAM = "X-Oracle-Device.Class";
  static final private String _UNKNOWN = "unknown";
  static final private TrinidadLogger _LOG = TrinidadLogger.createTrinidadLogger(AgentFactoryImpl.class);

}
