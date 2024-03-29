<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://myfaces.apache.org/sandbox" prefix="s"%>

<html>

<%@include file="../inc/head.inc" %>

<body>
<f:view>
	<h:panelGroup>

		<t:div id="Header">
			<f:subview id="head">
				<jsp:include page="../inc/head.jsp" />
			</f:subview>
		</t:div>
		<t:div id="Menu">
			<f:subview id="nav">
				<jsp:include page="../inc/navigation.jsp" />
			</f:subview>
		</t:div>

		<t:div id="Content">
			<h:form id="theselectForm">
			<t:saveState id="thescope"  value="#{ScopeBean}" />
			<h:panelGrid columns="1">
				<h:outputFormat value="Frobozz Industrial Magic Order Form" />
				<h:outputFormat value="Product order form"/>
			</h:panelGrid>

			<h:panelGrid columns="2" >
				<h:outputFormat value="product1"/>
				<h:selectOneMenu value="#{ScopeBean.selectedproduct1}">
					<f:selectItems value="#{catalog.items}"/>
				</h:selectOneMenu>
				<h:outputFormat value="product2"/>
				<h:selectOneMenu value="#{ScopeBean.selectedproduct2}">
					<f:selectItems value="#{catalog.items}"/>
				</h:selectOneMenu>
				<h:outputFormat value="product3"/>
				<h:selectOneMenu value="#{ScopeBean.selectedproduct3}">
					<f:selectItems value="#{catalog.items}"/>
				</h:selectOneMenu>
				<h:outputFormat value="product4"/>
				<h:selectOneMenu value="#{ScopeBean.selectedproduct4}">
					<f:selectItems value="#{catalog.items}"/>
				</h:selectOneMenu>
				<h:outputFormat value="product5"/>
				<h:selectOneMenu value="#{ScopeBean.selectedproduct5}">
					<f:selectItems value="#{catalog.items}"/>
				</h:selectOneMenu>
			</h:panelGrid>

			<h:panelGrid columns="3">
				<h:commandLink value="[Next >>>]" action="go_next"/>
			</h:panelGrid>
			</h:form>
		</t:div>
	</h:panelGroup>
</f:view>
</body>

<%@include file="../inc/page_footer.jsp" %>

</html>
