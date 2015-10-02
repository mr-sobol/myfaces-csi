# Introduction #

This is a set of tables describing how selectors are implemented in the project

# Details #

The table conventions are these:

  * **Component**: Reference the component name in a jsp or jspx file.
  * **Selector Base Name**: the name used in the css skin file. For easy of use, its the same component name but replace `|` by `*`.
  * **Properties**: The property in what add default css selector. For easy of use, its name generally is the same css property name of the component minus `Class` suffix (styleClass --> `::style`).
  * **Extends**: The global selectors that inherits css styling attributes. The words in italic are attributes defined. If you found a selector with a parentesis, it denotes that this selector apply for this property.

Some components use the classes p\_AFDisabled and  p\_AFReadonly inside :style property.

## JSF HTML Skin Selectors ##

| **Component**     | **Selector Base Name** | **Properties** | **Extends** |
|:------------------|:-----------------------|:---------------|:------------|
| h:commandButton   | h|commandButton        | ::style        |`AFDefaultFont, p_AFDisabled, p_AFReadonly` |
| h:commandLink     | h|commandLink          | ::style        |`AFDefaultFont, AFLinkForeground(::style:link), AFActiveLinkForeground(::style:active), AFVisitedLinkForeground(::style:visited)`  |
| h:dataTable       | h|datatable            | ::style, ::footer, ::header, ::row| _border-collapse:collapse_ |
| h:form            | h|form                 | ::style        |             |
| h:graphicImage    | h|graphicImage         | ::style        |             |
| h:inputSecret     | h|inputSecret          | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:inputText       | h|inputText            | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:inputTextarea   | h|inputTextarea        | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:message         | h|message              | ::style, ::fatal, ::info, ::error, ::warn | `AFDefaultFont(All properties), AFVeryDarkForeground(::style), .AFHeaderLevelOne(::error), AFErrorTextForeground(::error), AFDarkForeground(::info), MessageBoxMargin(::info) `|
| h:messages        | h|messages             | ::style, ::fatal, ::info, ::error, ::warn | `AFDefaultFont(All properties), AFVeryDarkForeground(::style), .AFHeaderLevelOne(::error), AFErrorTextForeground(::error), AFDarkForeground(::info), MessageBoxMargin(::info) `|
| h:outputLabel     | h|outputLabel          | ::style, ::disabled, ::required  |`AFLabel(::style), AFRequiredLabel(::disabled), AFLabelDisabled(::required) `|
| h:outputLink      | h|outputLink           | ::style        |`AFDefaultFont, AFLinkForeground(::style:link), AFActiveLinkForeground(::style:active), AFVisitedLinkForeground(::style:visited)`  |
| h:outputText      | h|outputText           | ::style        |`AFDefaultFont, AFTextForeground`|
| h:panelGrid       | h|panelGrid            | ::style, ::footer, ::header, ::row | `AFDefaultFont` |
| h:panelGroup      | h|panelGroup           | ::style        | `AFDefaultFont` |
| h:selectBooleanCheckbox   | h|selectBooleanCheckbox | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:selectManyCheckbox   | h|selectManyCheckbox   | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:selectManyListbox   | h|selectManyListbox    | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:selectManyMenu   | h|selectManyMenu       | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:selectOneListbox   | h|selectOneListbox     | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:selectOneMenu   | h|selectOneMenu        | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| h:selectOneRadio   | h|selectOneRadio       | ::style, ::required   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|

Notes:

1. h:graphicImage can load images or icons referenced in a css skin file using selectors ending with -icon or -icon:rtl like this:

In CSS skin file:

```
.xxx-icon
{
  content:url(/images/logo_mini.jpg);	
}
```

In JSP or JSPX file:

```
<h:graphicImage url="xxx-icon"/>
```

2. h|outputLabel try to look the component referenced by `for` attribute, if this component has attributes disabled or readonly set to true, it apply ::disabled and
::readonly styles to styleClass attribute.

## Tomahawk Extended HTML Selectors ##

This components are direct extensions of JSF HTML component library. In general, some components here have user and role support.

| **Component**     | **Selector Base Name** | **Properties** | **Extends** |
|:------------------|:-----------------------|:---------------|:------------|
| t:commandButton   | t|commandButton        | ::style        |`AFDefaultFont, p_AFDisabled, p_AFReadonly` |
| t:commandLink     | t|commandLink          | ::style        |`AFDefaultFont, AFLinkForeground(::style:link), AFActiveLinkForeground(::style:active), AFVisitedLinkForeground(::style:visited)`  |
| t:dataTable       | t|datatable            | ::style, ::footer, ::header, ::row, ::rowGroup| _border-collapse:collapse_ |
| t:graphicImage    | t|graphicImage         | ::style        |             |
| t:inputSecret     | t|inputSecret          | ::style, ::required, ::displayValueOnly | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:inputText       | t|inputText            | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:inputTextarea   | t|inputTextarea        | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:message         | t|message              | ::style, ::fatal, ::info, ::error, ::warn | `AFDefaultFont(All properties), AFVeryDarkForeground(::style), .AFHeaderLevelOne(::error), AFErrorTextForeground(::error), AFDarkForeground(::info), MessageBoxMargin(::info) `|
| t:messages        | t|messages             | ::style, ::fatal, ::info, ::error, ::warn | `AFDefaultFont(All properties), AFVeryDarkForeground(::style), .AFHeaderLevelOne(::error), AFErrorTextForeground(::error), AFDarkForeground(::info), MessageBoxMargin(::info) `|
| t:outputLabel     | t|outputLabel          | ::style, ::disabled, ::required  |`AFLabel(::style), AFRequiredLabel(::disabled), AFLabelDisabled(::required) `|
| t:outputLink      | t|outputLink           | ::style        |`AFDefaultFont, AFLinkForeground(::style:link), AFActiveLinkForeground(::style:active), AFVisitedLinkForeground(::style:visited)`  |
| t:outputText      | t|outputText           | ::style        |`AFDefaultFont, AFTextForeground`|
| t:panelGrid       | t|panelGrid            | ::style, ::footer, ::header, ::row | `AFDefaultFont` |
| t:panelGroup      | t|panelGroup           | ::style        | `AFDefaultFont` |
| t:selectBooleanCheckbox   | t|selectBooleanCheckbox | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:selectManyCheckbox   | t|selectManyCheckbox   | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:selectManyListbox   | t|selectManyListbox    | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:selectManyMenu   | t|selectManyMenu       | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:selectOneListbox   | t|selectOneListbox     | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:selectOneMenu   | t|selectOneMenu        | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:selectOneRadio   | t|selectOneRadio       | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|

Notes:

1. t:graphicImage works the same as h:graphicImage.

2. t:outputLabel works the same as h:outputLabel.

## Tomahawk Custom Selectors ##

This components are contained in the package org.apache.myfaces.custom and are components contributed by developers.

| **Component**     | **Selector Base Name** | **Properties** | **Extends** |
|:------------------|:-----------------------|:---------------|:------------|
| t:collapsiblePanel | t|collapsiblePanel     | ::style        | `AFDefaultFont` |
| t:column          | t|column               | ::style, ::footer, ::header |             |
| t:columns         | t|columns              | ::style, ::footer, ::header |             |
| t:commandNavigation | t|commandNavigation    | ::style        |             |
| t:commandNavigation2 | t|commandNavigation2   | ::style        |             |
| t:commandSortHeader | t|commandSortHeader    | ::style        |             |
| t:dataList        | t|dataList             | ::style, ::item |             |
| t:dataScroller    | t|dataScroller         | ::style, ::fastf, ::fastr, ::first, ::last, ::next, ::previous, ::paginatorActiveColumn, ::paginatorColumn, ::paginatorTable |             |
| t:inputCalendar   | t|inputCalendar        | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:inputDate       | t|inputDate            | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:inputFileUpload | t|inputFileUpload      | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:inputHtml       | t|inputHtml            | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, AFRequiredContent(::required)`|
| t:inputTextHelp   | t|inputTextHelp        | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:jscookMenu      | t|jscookMenu           | ::theme        |             |
| t:newspaperTable  | t|newspaperTable       | ::style, ::header, ::footer, ::body, ::rowGroup, ::row |             |
| t:panelLayout     | t|panelLayout          | ::style, ::header, ::footer, ::navigation |             |
| t:panelNavigation | t|panelNavigation      | ::style, ::item, ::active, ::open, ::separator |             |
| t:panelNavigation2 | t|panelNavigation      | ::style, ::item, ::active, ::open, ::separator, ::disabled |             |
| t:panelTabbedPane | t|panelTabbedPane      | ::style, ::activeTab, ::activeSub, ::disabledTab, ::inactiveSub, ::next, ::paginatorActiveColumn |             |
| t:popup           | t|popup                | ::style        |             |
| t:schedule        | t|schedule             | ::background, ::column, ::content, ::date, ::day, ::entry, ::even, ::foreground, ::free, ::gutter, ::header, ::holiday, ::hours, ::inactiveDay, ::minutes, ::month, ::selected, ::selectedEntry, ::subtitle, ::text, ::title, ::uneven, ::week |             |
| t:selectOneCountry   | t|selectOneCountry     | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:selectOneLanguage   | t|selectOneLanguage    | ::style, ::required, ::displayValueOnly   | `AFFormControlContent, p_AFDisabled, p_AFReadonly, AFRequiredContent(::required)`|
| t:swapImage       | t|swapImage            | ::style        |             |
| t:tree            | t|tree                 | ` ::style, ::node, ::selectedNode, ::header, ::footer, ::icon, ::line-icon:rtl, ::noLine-icon:rtl, ::nodeOpen-icon:rtl, nodeOpenFirst-icon:rtl, ::nodeOpenMiddle-icon:rtl, ::nodeOpenLast-icon:rtl, ::nodeClose-icon:rtl, ::nodeCloseFirst-icon:rtl, ::nodeCloseMiddle-icon:rtl, ::nodeCloseLast-icon:rtl, ::ChildFirst-icon:rtl, ::ChildMiddle-icon:rtl, ::ChildLast-icon:rtl `|             |

Notes:

1. The tld of t:jscookMenu define that a theme is required, so define a default theme
does not work. For define a t:jscookMenu, you may need to write some javascript for the theme, so it's not fit well with the skin. Anyway, you can define css classes in the skin file that uses the theme.

## Tomahawk Sandbox Custom Selectors ##

This components are contained in the package org.apache.myfaces.custom and are components contributed by developers.

| **Component**     | **Selector Base Name** | **Properties** | **Extends** |
|:------------------|:-----------------------|:---------------|:------------|
| s:accordionPane   | s|accordionPane        | ::style, ::displayValueOnly |             |
| s:ajaxChildComboBox | s|ajaxChildComboBox    | ::style, ::displayValueOnly, ::disabled, ::enabled | `AFFormControlContent` |
| s:autoUpdateDataTable | s|autoUpdateDataTable  | ::style, ::footer, ::header, ::row, ::rowStyle| _border-collapse:collapse_ |
| s:commandButtonAjax | t|commandButton        | ::style        |`AFDefaultFont, p_AFDisabled` |
| s:fieldset        | s|fieldset             | ::style        |             |
| s:filterTable     | s|filterTable          | ::style        |             |
| s:graphicImageDynamic | s|graphicImageDynamic  | ::style        |             |
| s:inputSuggest    | s|inputSuggest         | ::style, ::displayValueOnly | `AFFormControlContent` |
| s:inputSuggestAjax | s|inputSuggestAjax     | ::style, ::errorStyle | `AFFormControlContent(::style) , AFDefaultFont(::errorStyle), .AFHeaderLevelOne(::errorStyle), AFErrorTextForeground(::errorStyle) ` |
| s:layoutingContentPane | s|layoutingContentPane | ::style        |             |
| s:layoutingSplitPane  | s|layoutingSplitPane   | ::style        |             |
| s:layoutingTitlePane  | s|layoutingTitlePane   | ::containerNode, ::labelNode |             |
| s:message         | s|message              | ::style, ::fatal, ::info, ::error, ::warn | `AFDefaultFont(All properties), AFVeryDarkForeground(::style), .AFHeaderLevelOne(::error), AFErrorTextForeground(::error), AFDarkForeground(::info), MessageBoxMargin(::info) `|
| s:modalDialog     | s|modalDialog          | ::style        |             |
| s:outputLinkDynamic | s|outputLinkDynamic    | ::style        |`AFDefaultFont, AFLinkForeground(::style:link), AFActiveLinkForeground(::style:active), AFVisitedLinkForeground(::style:visited)`  |
| s:pprPanelGroup   | s|pprPanelGroup        | ::style        |             |
| s:roundedDiv      | s|roundedDiv           | ::style        |             |
| s:selectBooleanCheckboxAjax | s|selectBooleanCheckboxAjax | ::style        | `AFFormControlContent` |
| s:selectManyCheckboxAjax | s|selectManyCheckboxAjax | ::style, ::disabled, ::enabled | `AFFormControlContent` |
| s:selectManyPicklist | s|selectManyPicklist   | ::style, ::disabled, ::enabled | `AFFormControlContent` |
| s:selectOneRadioAjax | s|selectOneRadioAjax   | ::style, ::disabled, ::enabled | `AFFormControlContent` |
| s:tableSuggestAjax | s|tableSuggestAjax     | ::style, ::popup, ::table, ::comboBox, ::row, ::evenRow, ::oddRow, ::hoverStyle, ::displayValueOnly |             |
| s:toggleGroup     | s|toggleGroup          | ::style        |             |
| s:toggleLink      | s|toggleLink           | ::style        |             |
| s:togglePanel     | s|togglePanel          | ::style        |             |

Notes:

1. s:filterTable has a default style for headClass and tbodyClass property (see tag doc).
1. s:selectBooleanCheckboxAjax can load images or icons (attributes onImage and offImage) referenced in a css skin file using selectors ending with -icon or -icon:rtl like in h:graphicImage.
