<%@ include file="../../../../includes.jspf"%>

<%--
  ~ Copyright 2015 Karl Dahlgren
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~   http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<c:url var="rest_method_update_url"  value="/web/rest/project/${restProjectId}/application/${restApplicationId}/resource/${restResource.id}" />
<div class="content-top">
    <h1><spring:message code="rest.restresource.header.resource" arguments="${restResource.name}"/></h1>
    <div align="right">
        <sec:authorize access="hasRole('ADMIN') or hasRole('MODIFIER')">
            <a class="button-secondary pure-button" href="<c:url value="/web/rest/project/${restProjectId}/application/${restApplicationId}/resource/${restResource.id}/create/method"/>"><i class="fa fa-plus"></i> <span><spring:message code="rest.restresource.button.createmethod"/></span></a>
            <a class="button-success pure-button" href="<c:url value="/web/rest/project/${restProjectId}/application/${restApplicationId}/resource/${restResource.id}/update"/>"><i class="fa fa-file"></i> <span><spring:message code="rest.restresource.button.update"/></span></a>
            <a class="button-error pure-button" href="<c:url value="/web/rest/project/${restProjectId}/application/${restApplicationId}/resource/${restResource.id}/delete"/>"><i class="fa fa-trash"></i> <span><spring:message code="rest.restresource.button.delete"/></span></a>
        </sec:authorize>
    </div>
</div>
<div class="content-summary">
    <table class="formTable">
        <tr>
            <td class="column1"><label path="name"><spring:message code="rest.restresource.label.name"/></label></td>
            <td class="column2"><label path="name">${restResource.name}</label></td>
        </tr>
        <tr>
            <td class="column1"><label path="name"><spring:message code="rest.restresource.label.uri"/></label></td>
            <td class="column2"><label path="name">${restResource.uri}</label></td>
        </tr>
    </table>
</div>

<h2 class="decorated"><span><spring:message code="rest.restresource.header.methods"/></span></h2>
<c:choose>
    <c:when test="${restResource.restMethods.size() > 0}">
        <form:form action="${rest_method_update_url}/" method="POST"  commandName="restMethodModifierCommand">
            <div class="table-frame">
                <table class="entityTable">
                    <col width="10%">
                    <col width="30%">
                    <col width="10%">
                    <col width="10%">
                    <col width="40%">
                    <tr>
                        <th><spring:message code="rest.restresource.column.selected"/></th>
                        <th><spring:message code="rest.restresource.column.methodname"/></th>
                        <th><spring:message code="rest.restresource.column.methodmethodtype"/></th>
                        <th><spring:message code="rest.restresource.column.methodmethodstatus"/></th>
                        <th><spring:message code="rest.restresource.column.methodforwardedendpoint"/></th>
                    </tr>
                    <c:forEach items="${restResource.restMethods}" var="restMethod" varStatus="loopStatus">
                        <tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
                            <td><form:checkbox path="restMethodIds" name="${restMethod.id}" value="${restMethod.id}"/></td>
                            <td><a href="<c:url value="/web/rest/project/${restProjectId}/application/${restApplicationId}/resource/${restResource.id}/method/${restMethod.id}"/>">${restMethod.name}</a></td>
                            <td><a href="<c:url value="/web/rest/project/${restProjectId}/application/${restApplicationId}/resource/${restResource.id}/method/${restMethod.id}"/>">${restMethod.restMethodType}</a></td>
                            <td><a href="<c:url value="/web/rest/project/${restProjectId}/application/${restApplicationId}/resource/${restResource.id}/method/${restMethod.id}"/>">${restMethod.restMethodStatus}</a></td>
                            <td><a href="<c:url value="/web/rest/project/${restProjectId}/application/${restApplicationId}/resource/${restResource.id}/method/${restMethod.id}"/>">${restMethod.forwardedEndpoint}</a></td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
            <sec:authorize access="hasRole('ADMIN') or hasRole('MODIFIER')">
                <button class="button-error pure-button" type="submit" name="action" value="delete"><i class="fa fa-trash"></i> <span><spring:message code="rest.restresource.button.deleteresource"/></span></button>
            </sec:authorize>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form:form>

    </c:when>
    <c:otherwise>
        <spring:message code="rest.restresource.label.nomethods"/>
    </c:otherwise>
</c:choose>