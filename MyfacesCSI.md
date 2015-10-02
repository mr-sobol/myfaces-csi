# Introduction #

This project is made as part of a Google Summer of Code 2007.

The project has 3 main objectives:

1. Extends trinidad skins capabilities to non trinidad components (tomahawk of base jsf html component library).

2. Make a tutorial about how to use tiles or facelets + tomahawk.

3. Solve some issues of myfaces.

I made this pages for tasks of documenting the work done.

# Details #

## 1. Extends trinidad skins capabilities to non trinidad components ##

Actually, trinidad-1.0.1 supports combine other non trinidad components. Trinidad has a cool way to do skins, but if you don't use trinidad components, you can't use this feature. You have to define the style css classes in each page of each non trinidad component, but you can define the css classes inside trinidad css or xss file where you create your custom skin.

Seeing that necessity, this project provide a way to add trinidad skins features to non trinidad components.

### Possible uses of this work ###

  1. When you use non trinidad components with trinidad.
  1. When you have a project made with other component library (just html, tomahawk or sandbox components), and you want to add skins facilities.

### Source code ###

The code is available on the svn of the project as the name of myfaces-skins.

It has two subprojects:

  1. core: The main library. It works with trinidad-1.0.2-SNAPSHOT and tomahawk
  1. examples: A copy of myfaces examples, with trinidad skin addition (made probes simpler and faster to me!!). Looks like the original examples, but you can check the components to see that the proper CSS class from trinidad is added to normal components.
  1. examples-sandbox: A copy of myfaces sandbox examples, just for probe purpouses. The idea here is check sandbox components against trinidad for compatibility.

This project is released upon the Apache 2.0 license, and it will be donated to Apache Software Foundation after it is finished.

## Patches required to run ##

The code needs to apply some patches in order to fix some features in tomahawk and trinidad. I have posted these patches in jira of myfaces, but until the commiters of that project accept this changes, I provide this in /patches directory of the svn of this project.

The list of the patches needed are this. Almost all patches submitted has already been included in tomahawk (2007-AGO-19):

  1. **patch1.patch patch11FacetDataScroller.patch**: fix `HtmlCommandLinkRenderer.java` on trinidad-impl project (need to run 

&lt;t:commandSortHeader&gt;

). Based on the discusion that started here, a better solution that my patches where added. Create a link in a programmed fashion way. This patch is no longer needed, because this is already fixed.
  1. **patch2.patch, patch9sortColumnAscending1.patch**: fix `HtmlDataTable.java` on tomahawk (make 

&lt;t:commandSortHeader&gt;

 run good). This problem is not fixed. There is a problem with StateManagerImpl of trinidad, so in order to avoid this patch simply override the StateManager in faces-config.xml file with the StateManager of reference like this:

`<state-manager>org.apache.myfaces.application.jsp.JspStateManagerImpl</state-manager>`

  1. **patch3.patch**: fix `HtmlDataTable.java` on tomahawk (correct ClassCastException). This patch is no longer needed, because this is already fixed.
  1. **patch4.patch**: fix pseudoclasses on trinidad (correct `CSSGenerationUtils`). This patch is no longer needed, because this is already fixed.
  1. **patch5.patch, patch8.patch**: fix `HtmlPanelNavigationMenu.java` on tomahawk (correct ClassCastException). This patch is no longer needed, because this is already fixed.
  1. **patchConstants6.patch**: Add local constans to trinidad (modify `SkinCSSDocumentHandler.java`). This addition was negated, because it is not CSS3.0 compliant, and there is another way to do this through -tr-rule-ref.
  1. **patchJCookMenu7.patch**: Correct jscookmenu javascript to run with trinidad. This patch is no longer needed, because this is already fixed.

## Implementation details ##

Here is a class diagram from the core.

![http://myfaces-csi.googlecode.com/svn/trunk/myfaces-csi/images/classdiagram1.jpg](http://myfaces-csi.googlecode.com/svn/trunk/myfaces-csi/images/classdiagram1.jpg)

The idea is simple. Apply decorator pattern on trinidad `CoreRenderKit` , creating a `SkinRenderKit` class , and create custom classes that extends from `SkinRenderer` that add skinning support for it (this class delegate the rendering task to proper Renderer).

## 2. Tutorial about how to use tiles or facelets + tomahawk ##

In the last time I have found a good documentation about using facelets. Instead of making a tutorial about facelets + tomahawk, I believe that it could be better made 2 examples that use facelets + tomahawk.

Anyway, you can find a good documentation an a example here

https://facelets.dev.java.net/nonav/docs/dev/docbook.html

The examples are in the trunk of the svn, in the folder facelets.

I also made a maven archetype of hello world application that is hosted on

If you want to use it, you have to install the archetype and run something like this:

```

mvn archetype:create -DgroupId=com.prueba.hello -DartifactId=hello-facelets \ 
-DarchetypeGroupId=org.apache.myfaces.maven \
-DarchetypeArtifactId=maven-archetype-myfaces-facelets -DarchetypeVersion=1.0-SNAPSHOT \

```
## 3. Solve some issues of myfaces ##

The patches that I have submitted integrating tomahawk and trinidad complete this objective.

As a request of my mentor I have created a component for trinidad called TableFormLayout, that makes more easy the layout of complex panels. I have created two demos, one simple and the other the same but configured using facelets. The component takes some ideas and code from gridLayout component in tobago. The code is hosted here too.


