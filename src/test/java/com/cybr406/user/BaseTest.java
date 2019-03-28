package com.cybr406.user;

import com.cybr406.user.util.AuthUtil;
import com.cybr406.user.util.JdbcUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseTest {

  @Autowired
  protected MockMvc mockMvc;
  
  @Autowired
  protected JdbcUtil jdbcUtil;
  
  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected AuthUtil authUtil;
  
  protected ResultActions signUp(String email, String password, String firstName, String lastName) throws Exception {
    Map<String, Object> signUp = new HashMap<>();
    signUp.put("email", email);
    signUp.put("password", password);
    signUp.put("firstName", firstName);
    signUp.put("lastName", lastName);

    return mockMvc.perform(post("/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUp)));
  }
  
  protected Resource<Profile> responseToProfileResource(MockHttpServletResponse response) throws Exception {
    TypeReference<Resource<Profile>> typeReference = new TypeReference<Resource<Profile>>() {};
    return objectMapper.readValue(response.getContentAsString(), typeReference);
  }
  
  protected Profile responseToProfile(MockHttpServletResponse response) throws Exception {
    return objectMapper.readValue(response.getContentAsString(), Profile.class);
  }
  
}
