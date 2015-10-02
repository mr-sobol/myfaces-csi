# Summary #

The objective of this project is provide a framework for skin JSF components, refactoring the existing trinidad related code. Do this allows other JSF component frameworks like trinidad itself, tomahawk or tobago use it.

# Background #

On year 2007, several efforts where made in this field during GSOC 2007. A discussion about a myfaces skinning module where done here:

http://markmail.org/message/swxy73e3cth5zzaj?q=%5BSkinning%5D+independent+MyFaces+skinning+module

A big question raised on that time where how to separate skin code in order to remain compatibility with trinidad.

# Objectives #

  * Build a single skinning api that can be use by JSF component frameworks like trinidad, tomahawk or tobago, based on existing trinidad skin api.
  * Provide generic (basic support for any JSF framework) and tomahawk support (using extensions filter and modify existing tomahawk renderers to use this skinning api, so this api will be a dependency for tomahawk).

# Restrictions #

  * Any solution proposed must be compatible with trinidad (Read "compatible" as trinidad could use it in the future with little changes).
  * Since this is a new feature proposed for tomahawk, it should be optional use it.

# Benefits #

  * Enhance component set integration of Myfaces component frameworks.
  * Makes easier to users and developers add skinning features to custom JSF components.
  * Reduce the amount of work required to maintain myfaces code.

# Proposed Code #

The code available on http://myfaces-csi.googlecode.com/svn/trunk/testleo/myfaces-skins2/ contains a partial solution for this project. There are some pending issues to be solved, but the structure is settled for discussion.

The guidelines for work on the code are these:

  * Try apply high cohesion low coupling patterns, separating the code in modules.
  * There are some classes like RenderingContext, RequestContext and others that has code related to skinning and other trinidad specific code. It is necessary to decouple it (could be done creating a SkinXXXX or BaseXXX class and doing a small hierarchy, so the changes on trinidad are minimal).
  * Since compatibility with trinidad should be maintained, an alternative copy of trinidad code should be included, so exisiting tests and demos helps to remain it.
  * There is always a comment with a SKINFIX word to check what code changes by some reason on skin api, skin impl or trinidad alternate copy.
  * Trinidad specific code should not be part of skin api or impl. All trinidad specific classes should be moved outside core skin api or impl.

The temporal layout for this project is this:

|shared-api| Utilitary code used by skin-api but not related to it (some parts should be on myfaces commons utils but others could be on skin-api).|
|:---------|:--------------------------------------------------------------------------------------------------------------------------------------|
|shared-impl| Utilitary code used by skin-impl but not related directly to it (for example, code related to parse xml files,...) Maybe this code could be bundled with skin-impl.|

|skin-api| Code that comes from trinidad-api related to skinning.|
|:-------|:------------------------------------------------------|
|skin-impl| Code that comes from trinidad-impl related to skinning but not trinidad specific.|

|trinidad-core-api| Trinidad 1.2.x version that uses skin-api, just to check compatibility and track changes necessary to do so|
|:----------------|:-----------------------------------------------------------------------------------------------------------|
|trinidad-core-impl| Trinidad 1.2.x version that uses skin-api, just to check compatibility and track changes necessary to do so|

|skins-support-shared| Classes required to add skin support, common to other support modules.|
|:-------------------|:----------------------------------------------------------------------|
|tomahawk-support    | Tomahawk support for skinning. Contains tomahawk specific classes to use skin-api and skin-impl with tomahawk. It uses AddResource api to serve css files.|
|generic-support     | Generic support for skinning. Contains specific classes to use skin-api and skin-impl with any component library. It uses SkinResourceServlet to serve css files.|

|skins-example-generic| Demo using generic support.|
|:--------------------|:---------------------------|
|skins-example-tomahawk| Demo using tomahawk support.|
|trinidad-examples    | Copy of trinidad examples module to check that trinidad-core-api and trinidad-core-impl works correctly.|