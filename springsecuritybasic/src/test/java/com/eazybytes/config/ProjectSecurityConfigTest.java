package com.eazybytes.config;

import com.eazybytes.model.Contact;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectSecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void accessUnauthenticatedPaths() throws Exception {
        // "/notices", "/contact", "/register" 경로는 모든 사용자에게 열려 있어야 합니다.
        mockMvc.perform(get("/notices")).andExpect(status().isOk());

        // Contact 객체의 예시 데이터 생성
        Contact sampleContact = new Contact();
        sampleContact.setContactId("SR123456789"); // 예시 ID, 실제 서비스에서는 랜덤으로 생성될 것입니다.
        sampleContact.setContactName("John Doe");
        sampleContact.setContactEmail("john@example.com");
        sampleContact.setSubject("Test Subject");
        sampleContact.setMessage("This is a test message.");
        sampleContact.setCreateDt(Date.valueOf("2021-01-01"));

        // Contact 객체를 JSON 형태로 변환
        String jsonContact = new ObjectMapper().writeValueAsString(sampleContact);

        mockMvc.perform(post("/contact")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(jsonContact))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register")).andExpect(status().isOk());
    }


    @Test
    public void accessAuthenticatedPathsWithoutLogin() throws Exception {
        // 로그인하지 않은 상태에서 인증된 사용자만 접근 가능한 경로에 요청하면 401(Unauthorized) 상태 코드가 반환되어야 합니다.
        mockMvc.perform(get("/myAccount")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/myBalance")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/myLoans")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/myCards")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/user")).andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser( roles = {"USER"})
    public void accessUserEndpointsWithUserRole() throws Exception {
        mockMvc.perform(get("/myAccount/1")).andExpect(status().isOk());
//        mockMvc.perform(get("/myBalance/1")).andExpect(status().isOk()); // .param() 메서드 사용
//        mockMvc.perform(get("/myLoans").param("id", "1")).andExpect(status().isOk());  // .param() 메서드 사용
//        mockMvc.perform(get("/myCards").param("id", "1")).andExpect(status().isOk());  // .param() 메서드 사용
//        mockMvc.perform(get("/user")).andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "happy", roles = {"ADMIN"})
    @Transactional
    public void accessEndpointsWithAdminRole() throws Exception {
        // 관리자 권한으로는 USER 역할을 위한 엔드포인트에 접근할 수 없습니다.
        mockMvc.perform(get("/myAccount/1")).andExpect(status().isForbidden());
//        mockMvc.perform(get("/myBalance/1")).andExpect(status().isOk());  // ADMIN 역할도 접근 가능,
        mockMvc.perform(get("/myLoans").param("id", "1")).andExpect(status().isForbidden());
        mockMvc.perform(get("/myCards").param("id", "1")).andExpect(status().isForbidden());
        mockMvc.perform(get("/user")).andExpect(status().isOk());
    }
    @Test
    public void hello() throws Exception {
        mockMvc.perform(get("/hello")).andExpect(status().isUnauthorized());
    }
}

