package com.chisw.microservices.nodes.testutil;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

/**
 * Provide common helper for RestController class
 */
public class RestControllerTestHelper {


    public static MockMvc mockMvc(StandaloneMockMvcBuilder standaloneMockMvcBuilder) {

        return standaloneMockMvcBuilder
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }
}
