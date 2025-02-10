package com.eazybytes.config;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class CsrfTest {

    @Autowired
    private MockMvc mockMvc;

    // csrf 토큰 테스트코드
    @Test
    @WithMockUser
    public void given_withoutCsrf_whenNoticesCalled_thenForbidden() throws Exception {
        mockMvc.perform(get("/hello")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void given_witCsrf_whenHelloAPICalled_thenStatusisOk() throws Exception {
        mockMvc.perform(get("/hello").with(csrf())).andExpect(status().isOk());
    }


}
