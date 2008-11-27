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
package org.apache.myfaces.custom.skin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.el.ValueExpression;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.parsers.SAXParserFactory;

import org.apache.myfaces.custom.skin.skins.MinimalDesktopSkinExtension;
import org.apache.myfaces.custom.skin.skins.MinimalPdaSkinExtension;
import org.apache.myfaces.custom.skin.skins.MinimalPortletSkinExtension;
import org.apache.myfaces.custom.skin.skins.SimpleDesktopSkin;
import org.apache.myfaces.custom.skin.skins.SimplePdaSkin;
import org.apache.myfaces.custom.skin.skins.SimplePortletSkin;
import org.apache.myfaces.trinidad.logging.SkinLogger;
import org.apache.myfaces.trinidad.skin.Skin;
import org.apache.myfaces.trinidad.skin.SkinAddition;
import org.apache.myfaces.trinidad.skin.SkinFactory;
import org.apache.myfaces.trinidadinternal.config.LazyValueExpression;
import org.apache.myfaces.trinidadinternal.share.io.NameResolver;
import org.apache.myfaces.trinidadinternal.share.xml.ClassParserFactory;
import org.apache.myfaces.trinidadinternal.share.xml.ParseContextImpl;
import org.apache.myfaces.trinidadinternal.share.xml.ParserFactory;
import org.apache.myfaces.trinidadinternal.share.xml.ParserManager;
import org.apache.myfaces.trinidadinternal.share.xml.TreeBuilder;
import org.apache.myfaces.trinidadinternal.share.xml.XMLProvider;
import org.apache.myfaces.trinidadinternal.share.xml.XMLUtils;
import org.apache.myfaces.trinidadinternal.skin.BaseSkinUtils;
import org.apache.myfaces.trinidadinternal.skin.SkinExtension;
import org.apache.myfaces.trinidadinternal.skin.SkinFactoryImpl;
import org.apache.myfaces.trinidadinternal.skin.parse.SkinAdditionNode;
import org.apache.myfaces.trinidadinternal.skin.parse.SkinNode;
import org.apache.myfaces.trinidadinternal.skin.parse.SkinsNode;
import org.apache.myfaces.trinidadinternal.skin.parse.XMLConstants;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility functions for creating Skin objects and SkinExtension objects 
 * from the trinidad-skins.xml file and
 * adding them to the SkinFactory. It also adds SkinAdditions to the Skin objects.
 * @version $Name:  $ ($Revision$) $Date$
 */
public class SkinUtils extends BaseSkinUtils
{

  /**
   * Register the base skins with the SkinFactory. (simple/minimal)
   * Make sure the SkinFactory.getFactory() does not return null before
   * calling this method.
   */
  static public void registerBaseSkins()
  {

    SkinFactory skinFactory = SkinFactory.getFactory();

    // skinFactory should be non-null when this is called since it is
    // initiated in the TrinidadFilterImpl, but in case it isn't do this
    if (skinFactory == null)
    {
      SkinFactory.setFactory(new SkinFactoryImpl());
      skinFactory = SkinFactory.getFactory();
    }

    _registerTrinidadSkins(skinFactory);
  }
  
  /**
   * Register any custom skin extensions (and skin-additions) found in the
   * trinidad-skins.xml file with the SkinFactory.
   * 
   * Make sure the SkinFactory.getFactory() does not return null before
   * calling this method.
   * You should call registerBaseSkins() before calling this method.
   * @param context ServletContext, used to get the trinidad-skins.xml file.
   */
  static public void registerSkinExtensions(
    ExternalContext context)
  {

    SkinFactory skinFactory = SkinFactory.getFactory();

    // skinFactory should be non-null when this is called since it is
    // initiated in the TrinidadFilterImpl, but in case it isn't do this
    if (skinFactory == null)
    {
      SkinFactory.setFactory(new SkinFactoryImpl());
      skinFactory = SkinFactory.getFactory();
    }

    _registerSkinExtensionsAndAdditions(context, skinFactory);

  }

  /**
   * Returns the actual Icon referenced by the ReferenceIcon.
   * @param skin the Skin to use when resolving the ReferenceIcon
   * @param refIcon a ReferenceIcon instance
   * @return icon which is resolved. i.e., it is not a ReferenceIcon.
   */
  /*
   static public Icon resolveReferenceIcon(
     Skin          skin,
     ReferenceIcon refIcon)
   {
     return _resolveReferenceIcon(skin, refIcon, null);
   }
   */

  /**
   * Helper for resolveReferenceIcon which uses a Stack of icon names
   * to detect circular dependencies.
   *
   * @param skin the Skin to use when resolving the ReferenceIcon
   * @param refIcon a ReferenceIcon instance
   * @param referencedIconStack  The stack of reference icon names which have
   *          already been visited.  Used to detect circular dependencies.
   * @return icon which is resolved. i.e., it is not a ReferenceIcon.
   */
  /*
  static private Icon _resolveReferenceIcon(
    Skin          skin,
    ReferenceIcon refIcon,
    Stack<String> referencedIconStack)
  {
    String refName = refIcon.getName();

    // make sure we don't have a circular dependency
    if ((referencedIconStack != null) && referencedIconStack.contains(refName))
    {
      if (_LOG.isWarning())
        _LOG.warning("SKIN_CIRCULAR_INCLUDE_ERROR", refName);
      return null;
    }

    if (referencedIconStack == null)
    {
      referencedIconStack = new Stack<String>();
    }

    referencedIconStack.push(refName);

    Icon icon = skin.getIcon(refName, false);

    if ((icon instanceof ReferenceIcon) && (icon != null))
    {

      return _resolveReferenceIcon(skin,
                                  (ReferenceIcon)icon,
                                  referencedIconStack);

    }

    return icon;
  }*/

  /**
   * Create a SkinExtension off a generic SAX input source, using
   * a custom parsing manager.
   * <p>
   * @param provider
   * @param resolver A NameResolver that can be used to locate
   *                 resources, such as source images for colorized
   *                 icons.
   * @param parserManager the ParserManager to use for parsing
   *                Must  be non-null.
   * @throws NullPointerException when inputStream or parserManager
   *         is null.
   */
  /**
   * 
   * @param provider an XMLProvider implementation.
   * @param resolver A NameResolver that can be used to locate
   *                 resources, such as source images for colorized
   *                 icons.
   * @param inputStream the inputStream. Must be non-null
   * @param parserManager the ParserManager to use for parsing
   *                Must  be non-null.
   * @param configFile The name of the config file we are parsing.
   * @return A SkinsNode object (contains a List of SkinNode and a List of SkinAdditionNode)
   */
  static private SkinsNode _getSkinsNodeFromInputStream(
    XMLProvider        provider,
    NameResolver       resolver,
    InputStream        inputStream,
    ParserManager      parserManager,
    String             configFile
    )
  {
  
    if (inputStream == null)
      throw new NullPointerException(_LOG.getMessage(
        "NO_INPUTSTREAM"));
    if (parserManager == null)
      throw new NullPointerException(_LOG.getMessage(
        "NULL_PARSEMANAGER"));
    SkinsNode skinsNode = null;
    try
    {
      InputSource input = new InputSource();
      input.setByteStream(inputStream);
      input.setPublicId(configFile);

      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setNamespaceAware(true);   
      
      ParseContextImpl context = new ParseContextImpl();

      // Set up the NameResolver if we have one
      if (resolver != null)
        XMLUtils.setResolver(context, resolver);

      // Create the TreeBuilder
      TreeBuilder builder = new TreeBuilder(parserManager,
                                            SkinsNode.class);
      skinsNode = ((SkinsNode)builder.parse(provider, input, context));
   
    }
    catch (IOException ioe)
    {
      _LOG.warning(ioe);
    }
    catch (SAXException saxe)
    {
      _LOG.warning(saxe);
    }
    finally
    {
      try
      {
        inputStream.close();
      }
      catch (IOException ioe)
      {
        // Ignore
        ;
      }
    }
    return skinsNode;
  }

  /**
   * Creates a ParserManager pre-registered witih all
   * the default ParserFactories needed to create SkinExtensions.
   */
  static public ParserManager createDefaultManager()
  {
    ParserManager manager = new ParserManager();

    // Register top-level factory
     _registerFactory(manager, SkinsNode.class, "SkinsNode");
    
    // Register skin node factory and skin addition node factory
    _registerFactory(manager, SkinNode.class, "SkinNode");
    _registerFactory(manager, SkinAdditionNode.class, "SkinAdditionNode");

    return manager;
  }

  // Returns a singleton instance of the default ParserManager
  static private ParserManager _getDefaultManager()
  {
    if (_sManager == null)
      _sManager = createDefaultManager();

    return _sManager;
  }

  // Registers a ParserFactory for the LAF namespace
  static private void _registerFactory(
    ParserManager manager,
    Class<?> expectedType,
    String baseName
    )
  {
    String className = _LAF_PARSE_PACKAGE + baseName + "Parser";
    ParserFactory factory = new ClassParserFactory(className);

    manager.registerFactory(expectedType,
                            XMLConstants.SKIN_NAMESPACE,
                            factory);
  }

  /**
   * register the Trinidad skins: simpleDesktopSkin, simplePdaSkin,
   * and minimalDesktopSkin, minimalPdaSkin, and portlet skins.
   * @param skinFactory
   */
  private static void _registerTrinidadSkins(
    SkinFactory skinFactory)
  {
    // SimpleDesktopSkin is the BASE skin for org.apache.myfaces.trinidad.desktop renderKit
    // SimplePdaSkin is the BASE skin for org.apache.myfaces.trinidad.pda renderKit. By
    // BASE skin, I mean, this is the skin that all SkinExtensions extend
    // from.
    SimpleDesktopSkin simpleDesktopSkin = new SimpleDesktopSkin();
    skinFactory.addSkin(simpleDesktopSkin.getId(), simpleDesktopSkin);

    SimplePdaSkin simplePdaSkin = new SimplePdaSkin();
    skinFactory.addSkin(simplePdaSkin.getId(), simplePdaSkin);
    //portlet skin maps most of our style classes to portlet style classes,
    // so we output portlet style classes.
    // It also clears out the portlet style class definitions.
    SimplePortletSkin simplePortletSkin = new SimplePortletSkin();
    skinFactory.addSkin(simplePortletSkin.getId(), simplePortletSkin);
    
    MinimalDesktopSkinExtension minimalDesktopSkin =
      new MinimalDesktopSkinExtension(simpleDesktopSkin);
    skinFactory.addSkin(minimalDesktopSkin.getId(), minimalDesktopSkin);

    MinimalPdaSkinExtension minimalPdaSkin =
      new MinimalPdaSkinExtension(simplePdaSkin);
    skinFactory.addSkin(minimalPdaSkin.getId(), minimalPdaSkin);
    
    MinimalPortletSkinExtension minimalPortletSkin = 
      new MinimalPortletSkinExtension(simplePortletSkin);
    skinFactory.addSkin(minimalPortletSkin.getId(), minimalPortletSkin);
  }

  /**
   * Parse the trinidad-skins.xml file for SkinExtensions and SkinAdditionNodes and add each
   * SkinExtension to the skinFactory and each SkinAddition to its skin.
   * First find all the trinidad-skins.xml files that are in META-INF directory, and 
   * add those skins and skin additions.
   * Then find the WEB-INF/trinidad-skins.xml file and add those skins and skin additions.
   * The skins are ordered so that the 'extended' skins are registered before the skins that extend
   * them.
   * @param context
   * @param skinFactory
   */
  private static void _registerSkinExtensionsAndAdditions(
    ExternalContext context,
    SkinFactory skinFactory)
  {
    if (context == null)
      return;
     
    // Add META-INF/trinidad-skins.xml skins to skin factory. (sorted first to make sure 
    // we register the most 'base' skins first)
    List<SkinsNode> metaInfSkinsNodeList = _getMetaInfSkinsNodeList(); 
    // Go through each SkinsNode object 
    // (contains List of SkinNodes and List of SkinAdditionNodes)
    // and return a List of the SkinNodes.
    List<SkinNode> metaInfSkinNodes = new ArrayList<SkinNode>();
    for (SkinsNode skinsNode : metaInfSkinsNodeList)
    {
      metaInfSkinNodes.addAll(skinsNode.getSkinNodes());
    }    
    
    List<SkinNode> sortedMetaInfSkinNodes = _sortSkinNodes(skinFactory, metaInfSkinNodes);
    
    for (SkinNode skinNode : sortedMetaInfSkinNodes)
    {
      _addSkinToFactory(skinFactory, skinNode, true);
    }
    
    // Add WEB-INF/trinidad-skins.xml skins to skin factory. (sorted first)    
    SkinsNode webInfSkinsNode = _getWebInfSkinsNode(context);
    if (webInfSkinsNode != null)
    {
      List<SkinNode> webInfSkinNodes = webInfSkinsNode.getSkinNodes();
  
      List<SkinNode> sortedWebInfSkinNodes = _sortSkinNodes(skinFactory, webInfSkinNodes);
      
      // register skins found in webInfSkinNodes
      for (SkinNode skinNode : sortedWebInfSkinNodes)
      {
        _addSkinToFactory(skinFactory, skinNode, false);
      }
    }
    
    // register all the skin additions from META-INF trinidad-skins.xml and WEB-INF
    // trinidad-skins.xml that we have stored in the metaInfSkinsNodeList object and the
    // webInfSkinsNode object
    // skin-additions are additions to a skin, not extensions. They are used by
    // custom component developers that want to add a stylesheet for their components
    // to a particular skin, like the simple skin.
    FacesContext fContext = FacesContext.getCurrentInstance();
    // register skin-additions from META-INF/trinidad-skins.xml files
    _registerMetaInfSkinAdditions(fContext, skinFactory, metaInfSkinsNodeList);

    // register skin-additions from WEB-INF/trinidad-skins.xml file
    if (webInfSkinsNode != null)
    {
      List<SkinAdditionNode> skinAdditionNodeList = webInfSkinsNode.getSkinAdditionNodes();
      _registerSkinAdditions(fContext, skinFactory, skinAdditionNodeList, false);    
    }
    
  }
  
  /**
   * Given the a List of SkinNodes, sort them so that the SkinNodes in such a way so that
   * when we register the skins we make sure that the 'base' skins are registered before
   * skins that extend them.
   * @param skinFactory
   * @param skinNodes
   * @return sorted List of SkinNodes
   */
  private static List<SkinNode> _sortSkinNodes(
    SkinFactory    skinFactory,
    List<SkinNode> skinNodes)
  {
    List<SkinNode> sortedSkinNodes = new ArrayList<SkinNode>();
    List<String>   skinNodesAdded = new ArrayList<String>();
    List<String>   baseSkinIds = new ArrayList<String>();
    for (Iterator<String> i = skinFactory.getSkinIds(); i.hasNext();)
    {
       baseSkinIds.add(i.next());
    }
   
    // first, the skins that don't extend anything
    for (SkinNode skinNode : skinNodes)
    {
      String skinExtends = skinNode.getSkinExtends();

      if (skinExtends == null)
      {
        sortedSkinNodes.add(skinNode);
        skinNodesAdded.add(skinNode.getId());
      }
    }
    
    // second, the skins that extend another skin
    _sortSkinNodesWithExtensions(skinNodes, sortedSkinNodes, skinNodesAdded, baseSkinIds, 0);
      
    return sortedSkinNodes;
    
  }
  
  /**
   * This sorts SkinNodes that have their 'extends' value set, which means the skin
   * extends another skin. The order of our skin nodes matters in this case. We want the 
   * base skin to be registered first.
   * @param skinNodes
   * @param sortedSkinNodes
   * @param skinNodesAdded
   * @param baseSkinIds
   */
  private static void _sortSkinNodesWithExtensions(
    List<SkinNode> skinNodes,
    List<SkinNode> sortedSkinNodes,
    List<String>   skinNodesAdded,
    List<String>   baseSkinIds,
    int            originalLeftOverListSize)
  {
    List<SkinNode> leftOverList = new ArrayList<SkinNode>();
  
    for (SkinNode skinNode : skinNodes)
    {
      String skinExtends = skinNode.getSkinExtends();

      if (skinExtends != null)
      {
        if (skinNodesAdded.contains(skinExtends) ||
            baseSkinIds.contains(skinExtends))
        {
          sortedSkinNodes.add(skinNode);
          skinNodesAdded.add(skinNode.getId());
        }
        else
        { 
          // left over, put in a left-over list
          leftOverList.add(skinNode);
        }  
      }
    } 
    if ((originalLeftOverListSize > 0) && 
         (leftOverList.size() == originalLeftOverListSize))
    {
      // Ok, we are left with skinNodes that cannot be registered because the skin they extend is
      // not in the skinNodesAdded List. The skin they extend might not exist at all, or 
      // there is a circular dependency.
      // So..., just add these to the list. When we register these, they will cause a severe error
      // and the default base skin will be used.
       StringBuffer buffer = new StringBuffer();
       for (SkinNode leftOverNode : leftOverList)
       {
         buffer.append("Skin with id: " + leftOverNode.getId() + 
                      " extends skin with id: " + leftOverNode.getSkinExtends() + "\n");
         sortedSkinNodes.add(leftOverNode);
         skinNodesAdded.add(leftOverNode.getId());

       }
      _LOG.warning("The following skins extend each other in a circular " +
                   "fashion or the skin they extend does not exist.\n" + buffer.toString());
 
    } 
    else  if (leftOverList.size() > 0)   
    {
      _sortSkinNodesWithExtensions(leftOverList, sortedSkinNodes, 
                                   skinNodesAdded, baseSkinIds, leftOverList.size());      
    }
  }
 
  /**
   * Given a skinNode, create a Skin object and 
   * register the SkinExtension object with the skinFactory
   * @param skinFactory
   * @param skinNode
   */
  private static void _addSkinToFactory(
    SkinFactory skinFactory, 
    SkinNode    skinNode,
    boolean     isMetaInfFile)
  {
    // if the renderKitId is not specified,
    // set it to _RENDER_KIT_ID_CORE.
    String renderKitId = skinNode.getRenderKitId();
    String id = skinNode.getId();
    String family = skinNode.getFamily();
    String styleSheetName = skinNode.getStyleSheetName();
    String bundleName = skinNode.getBundleName();
    String translationSourceExpression = 
      skinNode.getTranslationSourceExpression();
    
    if (renderKitId == null)
      renderKitId = _RENDER_KIT_ID_DESKTOP;


    // figure out the base skin.
    Skin baseSkin = null;
    String skinExtends = skinNode.getSkinExtends();
    
    if (skinExtends != null)
      baseSkin = skinFactory.getSkin(null, skinExtends);
    if (baseSkin == null)
    {
      baseSkin = _getDefaultBaseSkin(skinFactory, renderKitId);
      
      if (skinExtends != null)
      {
        _LOG.severe("UNABLE_LOCATE_BASE_SKIN", 
                    new String[]{skinExtends, id, family, renderKitId, baseSkin.getId()});
      }

    }

    // Set the style sheet
    if (styleSheetName != null)
    {
      // If the styleSheetName is in the META-INF/trinidad-skins.xml file, then
      // we prepend META-INF to the styleSheetName if it doesn't begin with '/'.
      // This way we can find the file when we go to parse it later.
      if (isMetaInfFile)
        styleSheetName = _prependMetaInf(styleSheetName);
    }
    // If bundleName and translationSourceExpression are both set, then we 
    // only use the bundleName. An error was already logged during trinidad-skins
    // parsing.


    Skin skin = null;

    // bundle-name takes precedence over translation-source
    if (bundleName != null)
    {
      skin = new SkinExtension(baseSkin,
                               id,
                               family,
                               renderKitId,
                               styleSheetName,
                               bundleName);    
    }
    else
    {
      ValueExpression translationSourceVE = null;
      if (translationSourceExpression != null)
      {
        translationSourceVE = 
          _createTranslationSourceValueExpression(translationSourceExpression);
      }
      
      if (translationSourceVE != null)
      {
        skin = new SkinExtension(baseSkin,
                                 id,
                                 family,
                                 renderKitId,
                                 styleSheetName,
                                 translationSourceVE);         
      }
      else
      {
        skin = new SkinExtension(baseSkin,
                                 id,
                                 family,
                                 renderKitId,
                                 styleSheetName);         
      }

    }

    
    // Create a SkinExtension object and register skin with factory
    skinFactory.addSkin(id, skin);    
  }
  
  private static ValueExpression
  _createTranslationSourceValueExpression(
    String translationSourceExpression)
  {    
      if (translationSourceExpression != null)
      {
        translationSourceExpression = translationSourceExpression.trim();
         
        return LazyValueExpression.createValueExpression(translationSourceExpression, 
                                                         Object.class);
      }
      else
        return null;

  }
  
  /**
   * Get the WEB-INF/trinidad-skins.xml file, parse it, and return a List of SkinsNode objects. 
   * @param context ServletContext used to getResourceAsStream
   * @return List of SkinNodes (skin elements) found in trinidad-skins.xml
   */
  private static SkinsNode _getWebInfSkinsNode(
    ExternalContext context)
  {
    InputStream in = context.getResourceAsStream(_CONFIG_FILE);
    if (in != null)
    {
      SkinsNode webInfSkinsNode = 
        _getSkinsNodeFromInputStream(null, null, in, _getDefaultManager(), _CONFIG_FILE);

      return webInfSkinsNode;
    }
    else
    {
      return null;
    }
  }
 
  /**
   * Get all the META-INF/trinidad-skins.xml files, parse them, and from each file we get
   * a SkinsNode object -- the information inside the &lt;skins&gt; element -- each skin
   * and each skin-addition.
   * @return Each SkinsNode object we get from each META-INF/trinidad-skins.xml file, 
   * in a List<SkinsNode>.
   */
  private static List<SkinsNode> _getMetaInfSkinsNodeList()
  {
        
    List<SkinsNode> allSkinsNodes = new ArrayList<SkinsNode>(); 
    ClassLoader loader = Thread.currentThread().getContextClassLoader();     

    try
    {
  
      Enumeration<URL> urls = loader.getResources(_META_INF_CONFIG_FILE);
      Set<String> urlPaths = new HashSet<String>(16);
      
      while (urls.hasMoreElements())
      {
        URL url = urls.nextElement();
        
        // if url matches one we've already processed, skip it
        boolean successfullyAdded = urlPaths.add(url.getPath());

        if (!successfullyAdded)
        {
          if (_LOG.isFinest())
          {
            _LOG.finest("Skipping skin URL:{0} because it was already processed. " +
              "It was on the classpath more than once.", url);
          }
          // continue to the next url
        }
        else
        {
          _LOG.finest("Processing skin URL:{0}", url);
          InputStream in = url.openStream();
          try
          {
            // parse the config file and register the skin's additional stylesheets.
  
            if (in != null)
            {
              SkinsNode  metaInfSkinsNode = 
                _getSkinsNodeFromInputStream(null, null, in, _getDefaultManager(), 
                                             _META_INF_CONFIG_FILE);
  
              allSkinsNodes.add(metaInfSkinsNode);
              
            }
          }
          catch (Exception e)
          {
           _LOG.warning("ERR_PARSING", url);
           _LOG.warning(e);
          }
          finally
          {
            in.close();
          }     
        }
      }
    }
    catch (IOException e)
    {
      _LOG.severe("ERR_LOADING_FILE", _META_INF_CONFIG_FILE);
      _LOG.severe(e);
    }
    
    return allSkinsNodes;
  } 
  
  private static Skin _getDefaultBaseSkin(
    SkinFactory factory,
    String      renderKitId)
  {

    String baseSkinId = (_RENDER_KIT_ID_PDA.equals(renderKitId)) ?
                          _SIMPLE_PDA_SKIN_ID :
                          _SIMPLE_DESKTOP_SKIN_ID;

    Skin baseSkin = factory.getSkin(null, baseSkinId);

    // It is an error if we were unable to find the base skin
    if (baseSkin == null)
      _LOG.severe(_UNKNOWN_BASE_SKIN_ERROR + baseSkinId);

    return baseSkin;
  }
  
  // register the META-INF skin additions. 
  // It should not matter what order we process skin-additions since they should not 
  // depend upon one another.
  // We sort them by style-sheet-name so the StyleSheetDocumentId will be the same regardless
  // of order. Portals are using the styleSheetDocumentId to determine if the producer's skin
  // matches the consumer's skin, and the order of the skin-additions should not change this.
  private static void _registerMetaInfSkinAdditions(
    FacesContext    fContext,
    SkinFactory     skinFactory,
    List<SkinsNode> metaInfSkinsNodeList)
  {
    List<SkinAdditionNode> skinAdditionNodeList = new ArrayList<SkinAdditionNode>();
    
    for (SkinsNode skinsNode : metaInfSkinsNodeList)
    {
      skinAdditionNodeList.addAll(skinsNode.getSkinAdditionNodes());
    } 
    
    Collections.sort(skinAdditionNodeList);
    _registerSkinAdditions(fContext, skinFactory, skinAdditionNodeList, true);
  }
  
  /**
   * Get the skin id and other information from each SkinAdditionNode and
   * get the skin and register the SkinAddition with the skin
   * @param fContext
   * @param skinFactory
   * @param skinAdditionNodeList
   * @param isMetaInfFile true if the trinidad-skins.xml file is in the META-INF
   * directory.
   */
  private static void _registerSkinAdditions(
    FacesContext fContext,
    SkinFactory  skinFactory,
    List<SkinAdditionNode> skinAdditionNodeList,
    boolean      isMetaInfFile
    )
  {
    for (SkinAdditionNode skinAdditionNode : skinAdditionNodeList)
    {
      String skinId = skinAdditionNode.getSkinId();
      String styleSheetName = skinAdditionNode.getStyleSheetName();
      String resourceBundleName = skinAdditionNode.getResourceBundleName();
      String translationSourceExpression = 
        skinAdditionNode.getTranslationSourceExpression();

      Skin skin = skinFactory.getSkin(fContext, skinId);
      if (skin != null 
          && ((styleSheetName != null) 
              || (resourceBundleName != null)
              || (translationSourceExpression != null)))
      {
        // If the styleSheetName is in the META-INF/trinidad-skins.xml file, then
        // we prepend META-INF to the styleSheetName if it doesn't begin with '/'.
        // This way we can find the file when we go to parse it later.
        if (isMetaInfFile && (styleSheetName != null))
            styleSheetName = _prependMetaInf(styleSheetName);


        SkinAddition addition = null;
        
        if (resourceBundleName != null)
        {
          // create SkinAddition with resourceBundleName 
          addition = new SkinAddition(styleSheetName, resourceBundleName);
        }
        else
        {
          ValueExpression translationSourceVE = null;
          if (translationSourceExpression != null)
          {
            translationSourceVE = 
              _createTranslationSourceValueExpression(translationSourceExpression);
          }
          
          if (translationSourceVE != null)
          {
            // Create a SkinAddition with translationSourceVE 
            addition = new SkinAddition(styleSheetName, translationSourceVE);

          }
          else
          {
            // Create a SkinAddition with stylesheetName only 
            addition = new SkinAddition(styleSheetName);

          }

        }
        
        skin.addSkinAddition(addition);
      }
    }    
  }

  /**
   * Prepend META-INF to the styleSheetName if it doesn't begin with '/'.
   * @param styleSheetName
   * @return String styleSheetName or the styleSheetName prepended with META-INF/
   */
  private static String _prependMetaInf(String styleSheetName)
  {
    if (!(styleSheetName.startsWith("/")))
      return _META_INF_DIR.concat(styleSheetName);
    else
      return styleSheetName;
  }
  
  private SkinUtils() {}

  // The default ParserManager
  static private ParserManager _sManager;

  // Constants

  // Prefix of LAf parsing package
  static private final String _LAF_PARSE_PACKAGE =
    "org.apache.myfaces.trinidadinternal.skin.parse.";


  static private final String _CONFIG_FILE = "/WEB-INF/trinidad-skins.xml";
  static private final String _META_INF_CONFIG_FILE = "META-INF/trinidad-skins.xml";
  static private final String _META_INF_DIR = "META-INF/";
  static private final SkinLogger _LOG = SkinLogger.createSkinLogger(SkinUtils.class);

  // Error messages
  private static final String _UNKNOWN_BASE_SKIN_ERROR =
    "Unable to locate base skin: ";

  static private final String _RENDER_KIT_ID_DESKTOP = "org.apache.myfaces.trinidad.desktop";
  static private final String _RENDER_KIT_ID_PDA = "org.apache.myfaces.trinidad.pda";
  static private final String _SIMPLE_PDA_SKIN_ID = "simple.pda";
  static private final String _SIMPLE_DESKTOP_SKIN_ID = "simple.desktop";

}
