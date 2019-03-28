package com.cybr406.user.homework3;

import com.cybr406.user.BaseTest;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
public class UserControllerTests extends BaseTest {
  
  @Test
  @Transactional
  public void testSignUp() throws Exception {
    Map<String, Object> signUp = new HashMap<>();
    signUp.put("email", "test@example.com");
    signUp.put("password", "test");
    signUp.put("firstName", "Test");
    signUp.put("lastName", "Testerton");
    
    mockMvc.perform(post("/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isCreated());
    
    Map<String, Object> user = jdbcUtil.findUser("test@example.com");
    assertNotNull("The signup endpoint must must insert a user into the 'users' security table", user);
    assertThat("Passwords must be encoded during sign up", user.get("password"), not(equalTo(signUp.get("password"))));
    
    List<Map<String, Object>> authorities = jdbcUtil.findAuthorities("test@example.com");
    Map<String, Object> authority = authorities.stream()
        .filter(auth -> "ROLE_BLOGGER".equals(auth.get("authority")))
        .findFirst()
        .orElse(null);
    
    assertNotNull("Newly signed up users should have the 'BLOGGER' authority.", authority);
  }
  
  @Test
  public void testSignUpValidation() throws Exception {
    Map<String, Object> signUp = new HashMap<>();

    mockMvc.perform(post("/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUp)))
        .andExpect(status().isBadRequest())
        .andDo(MockMvcResultHandlers.print());
  }
  
}
