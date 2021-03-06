/*
 * Copyright 2015 Karl Dahlgren
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.castlemock.web.mock.rest.web.view.controller.method;

import com.castlemock.core.basis.model.ServiceProcessor;
import com.castlemock.core.mock.rest.model.event.domain.RestEvent;
import com.castlemock.core.mock.rest.model.project.RestApplicationGenerator;
import com.castlemock.core.mock.rest.model.project.RestMethodGenerator;
import com.castlemock.core.mock.rest.model.project.RestProjectGenerator;
import com.castlemock.core.mock.rest.model.project.RestResourceGenerator;
import com.castlemock.core.mock.rest.model.project.domain.*;
import com.castlemock.core.mock.rest.service.event.input.ReadRestEventWithMethodIdInput;
import com.castlemock.core.mock.rest.service.event.output.ReadRestEventWithMethodIdOutput;
import com.castlemock.core.mock.rest.service.project.input.*;
import com.castlemock.core.mock.rest.service.project.output.ReadRestMethodOutput;
import com.castlemock.core.mock.rest.service.project.output.ReadRestMockResponseOutput;
import com.castlemock.core.mock.rest.service.project.output.ReadRestResourceOutput;
import com.castlemock.web.basis.web.AbstractController;
import com.castlemock.web.mock.rest.config.TestApplication;
import com.castlemock.web.mock.rest.web.view.command.mockresponse.RestMockResponseModifierCommand;
import com.castlemock.web.mock.rest.web.view.controller.AbstractRestControllerTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;


/**
 * @author: Karl Dahlgren
 * @since: 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
@WebAppConfiguration
public class RestMethodControllerTest extends AbstractRestControllerTest {

    private static final String PAGE = "partial/mock/rest/method/restMethod.jsp";
    private static final String DELETE_REST_MOCK_RESPONSES_COMMAND = "command";
    private static final String DELETE_MOCK_RESPONSES_PAGE = "partial/mock/rest/mockresponse/deleteRestMockResponses.jsp";
    protected static final String REST_MOCK_RESPONSES = "restMockResponses";

    @InjectMocks
    private RestMethodController restMockResponseController;

    @Mock
    private ServiceProcessor serviceProcessor;

    @Override
    protected AbstractController getController() {
        return restMockResponseController;
    }

    @Test
    public void testGetMethod() throws Exception {
        final RestProject restProject = RestProjectGenerator.generateRestProject();
        final RestApplication restApplication = RestApplicationGenerator.generateRestApplication();
        final RestResource restResource = RestResourceGenerator.generateRestResource();
        final RestMethod restMethod = RestMethodGenerator.generateRestMethod();
        when(serviceProcessor.process(isA(ReadRestResourceInput.class))).thenReturn(ReadRestResourceOutput.builder()
                .restResource(restResource)
                .build());
        when(serviceProcessor.process(isA(ReadRestMethodInput.class))).thenReturn(ReadRestMethodOutput.builder()
                .restMethod(restMethod)
                .build());
        when(serviceProcessor.process(isA(ReadRestEventWithMethodIdInput.class))).thenReturn(ReadRestEventWithMethodIdOutput.builder()
                .restEvents(new ArrayList<RestEvent>())
                .build());
        final MockHttpServletRequestBuilder message = MockMvcRequestBuilders.get(SERVICE_URL + PROJECT + SLASH + restProject.getId() + SLASH + APPLICATION + SLASH + restApplication.getId() + SLASH + RESOURCE + SLASH + restResource.getId() + SLASH + METHOD + SLASH + restMethod.getId());
        mockMvc.perform(message)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(7 + GLOBAL_VIEW_MODEL_COUNT))
                .andExpect(MockMvcResultMatchers.forwardedUrl(INDEX))
                .andExpect(MockMvcResultMatchers.model().attribute(PARTIAL, PAGE))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_PROJECT_ID, restProject.getId()))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_APPLICATION_ID, restApplication.getId()))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_RESOURCE_ID, restResource.getId()))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_METHOD, restMethod));
    }

    @Test
    public void projectFunctionalityUpdate() throws Exception {
        final String projectId = "projectId";
        final String applicationId = "applicationId";
        final String resourceId = "resourceId";
        final String methodId = "methjodId";
        final String[] mockResponses = {"restMethod1", "restMethod2"};

        final RestMockResponse restMockResponse1 = new RestMockResponse();
        restMockResponse1.setName("restMockResponse1");

        final RestMockResponse restMockResponse2 = new RestMockResponse();
        restMockResponse2.setName("restMockResponse2");

        Mockito.when(serviceProcessor.process(Mockito.any(ReadRestMockResponseInput.class)))
                .thenReturn(ReadRestMockResponseOutput.builder().restMockResponse(restMockResponse1).build())
                .thenReturn(ReadRestMockResponseOutput.builder().restMockResponse(restMockResponse2).build());

        final RestMockResponseModifierCommand command = new RestMockResponseModifierCommand();
        command.setRestMockResponseIds(mockResponses);
        command.setRestMockResponseStatus("ENABLED");

        final MockHttpServletRequestBuilder message =
                MockMvcRequestBuilders.post(SERVICE_URL + PROJECT + SLASH + projectId + SLASH + APPLICATION + SLASH + applicationId + SLASH + RESOURCE + SLASH + resourceId + SLASH + METHOD + SLASH + methodId)
                        .param("action", "update").flashAttr("command", command);

        mockMvc.perform(message)
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/web/rest/project/" + projectId + "/application/" + applicationId + "/resource/" + resourceId + "/method/" + methodId));

        Mockito.verify(serviceProcessor, Mockito.times(2)).process(Mockito.isA(ReadRestMockResponseInput.class));
        Mockito.verify(serviceProcessor, Mockito.times(2)).process(Mockito.isA(UpdateRestMockResponseInput.class));
    }

    @Test
    public void projectFunctionalityDelete() throws Exception {
        final String projectId = "projectId";
        final String applicationId = "applicationId";
        final String resourceId = "resourceId";
        final String methodId = "methjodId";
        final String[] mockResponseIds = {"restMethod1", "restMethod2"};

        final RestMockResponseModifierCommand command = new RestMockResponseModifierCommand();
        command.setRestMockResponseIds(mockResponseIds);


        final RestMockResponse restMockResponse1 = new RestMockResponse();
        restMockResponse1.setName("restMockResponse1");

        final RestMockResponse restMockResponse2 = new RestMockResponse();
        restMockResponse2.setName("restMockResponse2");

        final List<RestMockResponse> restMockResponses = Arrays.asList(restMockResponse1, restMockResponse2);

        Mockito.when(serviceProcessor.process(Mockito.any(ReadRestMockResponseInput.class)))
                .thenReturn(ReadRestMockResponseOutput.builder().restMockResponse(restMockResponse1).build())
                .thenReturn(ReadRestMockResponseOutput.builder().restMockResponse(restMockResponse2).build());

        final MockHttpServletRequestBuilder message =
                MockMvcRequestBuilders.post(SERVICE_URL + PROJECT + SLASH + projectId +
                        SLASH + APPLICATION + SLASH + applicationId + SLASH + RESOURCE + SLASH + resourceId + SLASH + METHOD + SLASH + methodId)
                        .param("action", "delete").flashAttr("command", command);


        mockMvc.perform(message)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().size(6 + GLOBAL_VIEW_MODEL_COUNT))
                .andExpect(MockMvcResultMatchers.forwardedUrl(INDEX))
                .andExpect(MockMvcResultMatchers.model().attribute(PARTIAL, DELETE_MOCK_RESPONSES_PAGE))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_PROJECT_ID, projectId))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_APPLICATION_ID, applicationId))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_RESOURCE_ID, resourceId))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_METHOD_ID, methodId))
                .andExpect(MockMvcResultMatchers.model().attribute(REST_MOCK_RESPONSES, restMockResponses))
                .andExpect(MockMvcResultMatchers.model().attributeExists(DELETE_REST_MOCK_RESPONSES_COMMAND));


        Mockito.verify(serviceProcessor, Mockito.times(2)).process(Mockito.any(ReadRestResourceInput.class));

    }
    
    @Test
    public void testServiceFunctionalityDuplicate() throws Exception {
        final String projectId = "projectId";
        final String applicationId = "applicationId";
        final String resourceId = "resourceId";
        final String methodId = "resourceId";
        final String[] restMockResponseIds = {"MockResponse1", "MockResponse2"};


        final RestMockResponse restMockResponse1 = new RestMockResponse();
        restMockResponse1.setId("MockResponseId1");

        final RestMockResponse restMockResponse2 = new RestMockResponse();
        restMockResponse2.setId("MockResponseId2");

        Mockito.when(serviceProcessor.process(Mockito.any(ReadRestMockResponseInput.class)))
                .thenReturn(ReadRestMockResponseOutput.builder().restMockResponse(restMockResponse1).build())
                .thenReturn(ReadRestMockResponseOutput.builder().restMockResponse(restMockResponse2).build());


        final RestMockResponseModifierCommand command = new RestMockResponseModifierCommand();
        command.setRestMockResponseIds(restMockResponseIds);

        final MockHttpServletRequestBuilder message =
                MockMvcRequestBuilders.post(SERVICE_URL + PROJECT + SLASH + projectId + SLASH + APPLICATION
                        + SLASH + applicationId + SLASH + RESOURCE + SLASH + resourceId + SLASH + METHOD + SLASH + methodId)
                        .param("action", "duplicate").flashAttr("command", command);

        mockMvc.perform(message)
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.model().size(1))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/web/rest/project/" + projectId
                        + "/application/" + applicationId + "/resource/" + resourceId
                        + "/method/" + methodId));

        Mockito.verify(serviceProcessor, Mockito.times(2)).process(Mockito.isA(ReadRestMockResponseInput.class));
        Mockito.verify(serviceProcessor, Mockito.times(2)).process(Mockito.isA(CreateRestMockResponseInput.class));

    }



}
