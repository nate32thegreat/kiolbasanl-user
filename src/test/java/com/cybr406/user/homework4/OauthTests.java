package com.cybr406.user.homework4;

import com.cybr406.user.BaseTest;
import com.cybr406.user.Profile;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class OauthTests extends BaseTest {
  
  @Autowired
  JdbcTemplate jdbcTemplate;
  
  private Map<String, String> validatePasswordResponse(String response) throws Exception {
    TypeReference<Map<String, String>> typeReference = new TypeReference<Map<String, String>>() {};
    Map<String, String> auth = objectMapper.readValue(response, typeReference);
    
    assertThat("access_token must not be null", auth.get("access_token"), notNullValue());
    assertThat("token_type must be bearer", auth.get("token_type"), equalTo("bearer"));
    assertThat("refresh_token must not be null", auth.get("refresh_token"), notNullValue());
    assertThat("expires_in should be greater than zero", Integer.parseInt(auth.get("expires_in")), greaterThan(0));
    
    return auth;
  }
  
  private String testPasswordGrant(String username, String password) throws Exception {
    return mockMvc.perform(post("/oauth/token")
        .header("authorization", authUtil.encodeBasicAuth("api", ""))
        .param("grant_type", "password")
        .param("username", username)
        .param("password", password))
        .andExpect(status().is2xxSuccessful())
        .andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }
  
  @Test
  public void testPublicApiClient() throws Exception {
    signUp("test@example.com", "secret", "Test", "Testerton");
    String response = testPasswordGrant("test@example.com", "secret");
    validatePasswordResponse(response);
  }

  @Test
  public void testPublicApiClientCannotAccessCheckToken() throws Exception {
    signUp("test@example.com", "secret", "Test", "Testerton");
    String response = testPasswordGrant("test@example.com", "secret");
    Map<String, String> auth = validatePasswordResponse(response);

    mockMvc.perform(post("/oauth/check_token")
        .header("authorization", authUtil.bearerTokenAuth(auth.get("access_token")))
        .param("token", auth.get("access_token")))
        .andExpect(status().isUnauthorized())
        .andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void testProtectedPostClientCanAccessCheckToken() throws Exception {
    signUp("test@example.com", "secret", "Test", "Testerton");
    String response = testPasswordGrant("test@example.com", "secret");
    String apiToken = validatePasswordResponse(response).get("access_token");

    response = mockMvc.perform(post("/oauth/check_token")
        .header("authorization", authUtil.encodeBasicAuth("post", "secret"))
        .param("token", apiToken))
        .andExpect(status().is2xxSuccessful())
        .andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsString();

    TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
    Map<String, Object> auth = objectMapper.readValue(response, typeReference);
    List<String> authorities = (List) auth.get("authorities");
    
    assertThat(auth.get("client_id"), equalTo("api"));
    assertThat(auth.get("user_name"), equalTo("test@example.com"));
    assertThat(authorities, contains("ROLE_BLOGGER"));
  }
  
  @Test
  public void testCannotModifyOwnAccountUsingBasicAuth() throws Exception {
    MockHttpServletResponse servletResponse = signUp("test@example.com", "secret", "Test", "Testerton")
        .andReturn()
        .getResponse();
    Profile profile = responseToProfile(servletResponse);

    Map<String, Object> patch = new HashMap<>();
    patch.put("firstName", "First");

    String response = mockMvc.perform(patch("/profiles/{id}", profile.getId())
        .header("authorization", authUtil.encodeBasicAuth("test@example.com", "secret"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().isUnauthorized())
        .andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsString();
    
    assertThat(response, containsString("Full authentication is required to access this resource"));
  }

  @Test
  public void testCanModifyOwnAccountUsingToken() throws Exception {
    MockHttpServletResponse servletResponse = signUp("test@example.com", "secret", "Test", "Testerton")
        .andReturn()
        .getResponse();
    Profile profile = responseToProfile(servletResponse);
    String response = testPasswordGrant("test@example.com", "secret");
    String token = validatePasswordResponse(response).get("access_token");

    Map<String, Object> patch = new HashMap<>();
    patch.put("firstName", "First");

    mockMvc.perform(patch("/profiles/{id}", profile.getId())
        .header("authorization", authUtil.bearerTokenAuth(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().is2xxSuccessful())
        .andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  @Test
  public void testCannotModifyOtherAccountUsingToken() throws Exception {
    signUp("test@example.com", "secret", "Test", "Testerton");
    String response = testPasswordGrant("test@example.com", "secret");
    String token = validatePasswordResponse(response).get("access_token");
    
    MockHttpServletResponse servletResponse = signUp("other@example.com", "secret", "Other", "Otherton")
        .andReturn()
        .getResponse();
    Profile profile = responseToProfile(servletResponse);

    Map<String, Object> patch = new HashMap<>();
    patch.put("firstName", "First");

    mockMvc.perform(patch("/profiles/{id}", profile.getId())
        .header("authorization", authUtil.bearerTokenAuth(token))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().isForbidden())
        .andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  @Test
  public void testAnonymousCannotModifyAccounts() throws Exception {
    MockHttpServletResponse servletResponse = signUp("test@example.com", "secret", "Test", "Testerton")
        .andReturn()
        .getResponse();
    Profile profile = responseToProfile(servletResponse);

    Map<String, Object> patch = new HashMap<>();
    patch.put("firstName", "First");

    mockMvc.perform(patch("/profiles/{id}", profile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().isUnauthorized())
        .andDo(print())
        .andReturn()
        .getResponse()
        .getContentAsString();
  }

  @Test
  public void testGetProfiles() throws Exception {
    signUp("test@example.com", "secret", "Test", "Testerton");

    mockMvc.perform(get("/profiles"))
        .andExpect(status().isOk());
  }
  
}
