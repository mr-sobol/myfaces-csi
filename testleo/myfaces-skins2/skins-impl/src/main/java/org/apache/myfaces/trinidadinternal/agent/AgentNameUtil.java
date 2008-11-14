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

import org.apache.myfaces.trinidad.context.Agent;


/**
 * An utility class that maps agent/platform name strings to AdFacesAgent
 * Application constants and vice versa
 *
 */
public class AgentNameUtil
{
  /**
   * utility method to get AdfFacesAgent application constant (int) from agent name strings
   *
   * @param agentName
   * @return
   */
  public static int getAgent(String agentName)
  {
    if (TrinidadAgent.AGENT_NETSCAPE.equals(agentName))
    {
      return TrinidadAgent.APPLICATION_NETSCAPE;
    }

    if (TrinidadAgent.AGENT_IE.equals(agentName))
    {
      return TrinidadAgent.APPLICATION_IEXPLORER;
    }

    if (TrinidadAgent.AGENT_GECKO.equals(agentName))
    {
      return TrinidadAgent.APPLICATION_GECKO;
    }

    if (TrinidadAgent.AGENT_ELAINE.equals(agentName))
    {
      return TrinidadAgent.APPLICATION_WEB_CLIPPING;
    }

    if (TrinidadAgent.AGENT_ICE_BROWSER.equals(agentName))
    {
      return TrinidadAgent.APPLICATION_ICE;
    }

    if (TrinidadAgent.AGENT_PIXO.equals(agentName))
    {
      return TrinidadAgent.APPLICATION_PIXO;
    }

    if ((TrinidadAgent.AGENT_NETFRONT.equals(agentName)) ||
        (TrinidadAgent.AGENT_WEBPRO.equals(agentName)))
    {
      return TrinidadAgent.APPLICATION_NET_FRONT;
    }

    if (TrinidadAgent.AGENT_WEBKIT.equals(agentName))
    {
      return TrinidadAgent.APPLICATION_SAFARI;
    }
    
    if(TrinidadAgent.AGENT_BLACKBERRY.equals(agentName))
    {
      return TrinidadAgent.APPLICATION_BLACKBERRY;
    }

    return TrinidadAgent.APPLICATION_UNKNOWN;
  }

  /**
   * utility method to get AdfFacesAgent application constant (int) from platform/os name strings
   *
   * @param platformName
   * @return
   */
  public static int getPlatform(String platformName)
  {
    if (TrinidadAgent.PLATFORM_WINDOWS.equals(platformName))
    {
      return TrinidadAgent.OS_WINDOWS;
    }

    if (TrinidadAgent.PLATFORM_MACOS.equals(platformName))
    {
      return TrinidadAgent.OS_MACOS;
    }

    if (TrinidadAgent.PLATFORM_IPHONE.equals(platformName))
    {
      return TrinidadAgent.OS_IPHONE;
    }

    if (TrinidadAgent.PLATFORM_LINUX.equals(platformName))
    {
      return TrinidadAgent.OS_LINUX;
    }

    if (TrinidadAgent.PLATFORM_SOLARIS.equals(platformName))
    {
      return TrinidadAgent.OS_SOLARIS;
    }

    if (TrinidadAgent.PLATFORM_PALM.equals(platformName))
    {
      return TrinidadAgent.OS_PALM;
    }

    if (TrinidadAgent.PLATFORM_PPC.equals(platformName))
    {
      return TrinidadAgent.OS_PPC;
    }
    
    if (TrinidadAgent.PLATFORM_BLACKBERRY.equals(platformName))
    {
        return TrinidadAgent.OS_BLACKBERRY;
    }

    return TrinidadAgent.OS_UNKNOWN;
  }

  /**
   * utility method to get AdfFacesAgent application constant (int) from Agent type objects
   *
   * @param otype
   * @return
   */
  public static int getAgentType(Object otype)
  {
    if (otype == Agent.TYPE_PDA)
    {
      return TrinidadAgent.TYPE_PDA;
    }

    if (otype == Agent.TYPE_PHONE)
    {
      return TrinidadAgent.TYPE_PHONE;
    }

    //Default to desktop (This is UIX 2.2 logic)
    return TrinidadAgent.TYPE_DESKTOP;
  }

  /**
   * utility method to get agent name string from AdfFacesAgent application constant (int)
   *
   * @param agentId
   * @return
   */
  public static String getAgentName(int agentId)
  {
    switch (agentId) {
      case TrinidadAgent.APPLICATION_UNKNOWN:
        return null;
      case TrinidadAgent.APPLICATION_NETSCAPE:
        return TrinidadAgent.AGENT_NETSCAPE;
      case TrinidadAgent.APPLICATION_IEXPLORER:
        return TrinidadAgent.AGENT_IE;
      case TrinidadAgent.APPLICATION_GECKO:
        return TrinidadAgent.AGENT_GECKO;
      case TrinidadAgent.APPLICATION_WEB_CLIPPING:
        return TrinidadAgent.AGENT_ELAINE;
      case TrinidadAgent.APPLICATION_ICE:
        return TrinidadAgent.AGENT_ICE_BROWSER;
      case TrinidadAgent.APPLICATION_PIXO:
        return TrinidadAgent.AGENT_PIXO;
      case TrinidadAgent.APPLICATION_NET_FRONT:
        return TrinidadAgent.AGENT_NETFRONT;
      case TrinidadAgent.APPLICATION_SAFARI:
        return TrinidadAgent.AGENT_WEBKIT;
      case TrinidadAgent.APPLICATION_BLACKBERRY:
        return TrinidadAgent.AGENT_BLACKBERRY;
       default:
        return null;
    }
  }



  /**
   * utility method to get platform name string from AdfFacesAgent application constant (int)
   *
   * @param platformId
   * @return
   */
  public static String getPlatformName(int platformId)
  {
    switch (platformId) {
      case TrinidadAgent.OS_UNKNOWN:
        return null;
      case TrinidadAgent.OS_WINDOWS:
        return TrinidadAgent.PLATFORM_WINDOWS;
      case TrinidadAgent.OS_MACOS:
        return TrinidadAgent.PLATFORM_MACOS;
      case TrinidadAgent.OS_IPHONE:
        return TrinidadAgent.PLATFORM_IPHONE;
      case TrinidadAgent.OS_LINUX:
        return TrinidadAgent.PLATFORM_LINUX;
      case TrinidadAgent.OS_SOLARIS:
        return TrinidadAgent.PLATFORM_SOLARIS;
      case TrinidadAgent.OS_PALM:
        return TrinidadAgent.PLATFORM_PALM;
      case TrinidadAgent.OS_PPC:
        return TrinidadAgent.PLATFORM_PPC;
      case TrinidadAgent.OS_BLACKBERRY:
        return TrinidadAgent.PLATFORM_BLACKBERRY;
      default:
        return null;
    }
  }

  /**
   * utility method to get type obejct from AdfFacesAgent application constant (int)
   *
   * @param type
   * @return
   */
  public Object getType (int type) {
    switch (type) {
      case TrinidadAgent.TYPE_DESKTOP:
        return null;
      case TrinidadAgent.TYPE_PDA:
        return Agent.TYPE_PDA;
      case TrinidadAgent.TYPE_PHONE:
        return Agent.TYPE_PHONE;
      case TrinidadAgent.TYPE_VOICE:
      default:
        return Agent.TYPE_UNKNOWN;
    }
  }

}
