<%@ page session="false" contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@ taglib uri="http://myfaces.apache.org/trinidad/html" prefix="trh"%>
<html>

<!--
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
//-->
<f:view>
<%@include file="inc/head.inc" %>

<body>



    <f:loadBundle basename="org.apache.myfaces.examples.resource.example_messages" var="example_messages"/>
    
    <h:form>
        <t:dataTable id="data"
                styleClass="standardTable"            
                headerClass="standardTable_SortHeader"
                footerClass="standardTable_Footer"
                rowClasses="standardTable_Row1,standardTable_Row2"
                var="car"                                                        
                value="#{autosortlist.cars}"                 
                sortColumn="#{autosortlist.sortColumn}" 
                sortAscending="#{autosortlist.sortAscending}"
                preserveDataModel="true"
                preserveSort="true">

            <t:column sortable="true" defaultSorted="true">
                <f:facet name="header">  
                    <h:outputText value="ID" />                
                </f:facet>
                <h:outputText value="#{car.id}" />            
            </t:column>

            <t:column>
                <f:facet name="header">  
                    <h:outputText value="#{example_messages['sort_cartype']}" />                                
                </f:facet>
                <h:outputText value="#{car.type}" />            
            </t:column>

            <t:column sortable="true">
                <f:facet name="header">
                    <h:outputText value="#{example_messages['sort_carcolor']}" />                
                </f:facet>
                <h:outputText value="#{car.color}" />            
            </t:column>

        </t:dataTable>      
    </h:form>

    <jsp:include page="inc/mbean_source.jsp"/>

</f:view>

<%@include file="inc/page_footer.jsp" %>

</body>

</html>
