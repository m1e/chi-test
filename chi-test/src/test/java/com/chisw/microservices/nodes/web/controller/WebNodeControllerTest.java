package com.chisw.microservices.nodes.web.controller;

import com.chisw.microservices.nodes.exception.NodeAlreadyExistsException;
import com.chisw.microservices.nodes.exception.NodeNotFoundException;
import com.chisw.microservices.nodes.exception.RootAlreadyExistsException;
import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import com.chisw.microservices.nodes.service.NodeService;
import com.chisw.microservices.nodes.testutil.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.chisw.microservices.nodes.testutil.RestControllerTestHelper.mockMvc;
import static com.chisw.microservices.nodes.testutil.TestUtils.json;
import static com.chisw.microservices.nodes.testutil.TestUtils.node;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Category(UnitTest.class)
public class WebNodeControllerTest {

    @Mock
    private NodeService nodeService;

    private MockMvc mvc;


    @Before
    public void setUp() throws Exception {

        this.nodeService = mock(NodeService.class);
        this.mvc = mockMvc(MockMvcBuilders.standaloneSetup(new NodeController(nodeService)));
    }

    @Test
    public void get_success() throws Exception {

        when(nodeService.getById(eq("1"))).thenReturn(
                node("1", "1")
        );

        mvc.perform(get("/node/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.path", is("1")));

        verify(nodeService, times(1)).getById(eq("1"));
    }

    @Test
    public void get_not_found() throws Exception {

        when(nodeService.getById(eq("1"))).thenThrow(new NodeNotFoundException("e"));

        mvc.perform(get("/node/1"))
                .andExpect(status().isNotFound());

        verify(nodeService, times(1)).getById(eq("1"));
    }

    @Test
    public void ancestors_success() throws Exception {

        when(nodeService.getAncestors(eq("2"), any())).thenReturn(new PageImpl<>(asList(
                node("1", "1"),
                node("2", "2")
                )
                )
        );

        mvc.perform(get("/node/2/ancestors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.numberOfElements", is(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content.[0].id", is("1")))
                .andExpect(jsonPath("$.content.[0].path", is("1")))
                .andExpect(jsonPath("$.content.[1].id", is("2")))
                .andExpect(jsonPath("$.content.[1].path", is("2")));


        verify(nodeService, times(1)).getAncestors(eq("2"), any());
    }

    @Test
    public void ancestors_not_found() throws Exception {

        when(nodeService.getAncestors(any(), any())).thenThrow(new NodeNotFoundException("e"));

        mvc.perform(get("/node/2/ancestors"))
                .andExpect(status().isNotFound());

        verify(nodeService, times(1)).getAncestors(eq("2"), any());
    }

    @Test
    public void descendants_success() throws Exception {

        when(nodeService.getDescendants(eq("2"), any())).thenReturn(new PageImpl<>(asList(
                node("1", "1"),
                node("2", "2")
                )
                )
        );

        mvc.perform(get("/node/2/descendants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.numberOfElements", is(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content.[0].id", is("1")))
                .andExpect(jsonPath("$.content.[0].path", is("1")))
                .andExpect(jsonPath("$.content.[1].id", is("2")))
                .andExpect(jsonPath("$.content.[1].path", is("2")));

        verify(nodeService, times(1)).getDescendants(eq("2"), any());
    }


    @Test
    public void descendants_not_found() throws Exception {

        when(nodeService.getDescendants(eq("2"), any())).thenThrow(new NodeNotFoundException("e"));


        mvc.perform(get("/node/2/descendants"))
                .andExpect(status().isNotFound());

        verify(nodeService, times(1)).getDescendants(eq("2"), any());
    }

    @Test
    public void findOrCreate_success() throws Exception {

        when(nodeService.findOrCreate(any(), any())).thenReturn(node("1", "1"));

        mvc.perform(

                post("/node").content(

                        json("{'id':'1','parentId':'1'}")).contentType(APPLICATION_JSON_UTF8_VALUE)

        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.path", is("1")));


        verify(nodeService, times(1)).findOrCreate(eq("1"), eq("1"));
    }


    @Test
    public void findOrCreate_not_found() throws Exception {

        when(nodeService.findOrCreate(any(), any())).thenThrow(new NodeNotFoundException(""));

        mvc.perform(

                post("/node").content(

                        json("{'id':'1','parentId':'1'}")).contentType(APPLICATION_JSON_UTF8_VALUE)

        )
                .andExpect(status().isNotFound());


        verify(nodeService, times(1)).findOrCreate(eq("1"), eq("1"));
    }

    @Test
    public void findOrCreate_root_exists() throws Exception {

        when(nodeService.findOrCreate(any(), any())).thenThrow(new RootAlreadyExistsException());

        mvc.perform(

                post("/node").content(

                        json("{'id':'1','parentId':'1'}")).contentType(APPLICATION_JSON_UTF8_VALUE)

        )
                .andExpect(status().isConflict());


        verify(nodeService, times(1)).findOrCreate(eq("1"), eq("1"));
    }


    @Test
    public void update_success() throws Exception {

        when(nodeService.update(any(), any())).thenReturn(node("2", "1"));

        mvc.perform(

                put("/node/1").content(

                        json("{'id':'2'}")).contentType(APPLICATION_JSON_UTF8_VALUE)

                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id", is("2")))
                .andExpect(jsonPath("$.path", is("1")));


        verify(nodeService, times(1)).update(eq("1"), eq("2"));
    }


    @Test
    public void update_not_found() throws Exception {

        doThrow(new NodeNotFoundException("e")).when(nodeService).update(eq("1"), eq("2"));

        mvc.perform(

                put("/node/1").content(

                        json("{'id':'2'}")).contentType(APPLICATION_JSON_UTF8_VALUE)

                 )
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(nodeService, times(1)).update(eq("1"), eq("2"));
    }

    @Test
    public void update_id_already_exists() throws Exception {

        doThrow(new NodeAlreadyExistsException("e")).when(nodeService).update(eq("1"), eq("2"));

        mvc.perform(

                put("/node/1").content(

                        json("{'id':'2'}")).contentType(APPLICATION_JSON_UTF8_VALUE)

        )
                .andDo(print())
                .andExpect(status().isConflict());

        verify(nodeService, times(1)).update(eq("1"), eq("2"));
    }

    @Test
    public void deleteBranch_success() throws Exception {

        doNothing().when(nodeService).deleteBranch(any());

        mvc.perform(delete("/node/2"))
                .andExpect(status().isOk());

        verify(nodeService, times(1)).deleteBranch(eq("2"));
    }

    @Test
    public void deleteBranch_not_found() throws Exception {

        doThrow(new NodeNotFoundException("e")).when(nodeService).deleteBranch(any());

        mvc.perform(delete("/node/2"))
                .andExpect(status().isNotFound());


        verify(nodeService, times(1)).deleteBranch(eq("2"));
    }

}