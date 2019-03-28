package com.cybr406.user.homework3;

import com.cybr406.user.BaseTest;
import com.cybr406.user.Profile;
import org.junit.Test;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
public class SecurityTests extends BaseTest {
  
  @Test
  @Transactional
  public void testWWWAuthenticateHeader() throws Exception {
    signUp(
        "a@example.com", "password", "Abe", "Ableton")
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();

    MockHttpServletResponse response = mockMvc.perform(delete("/profiles/1"))
        .andExpect(status().isUnauthorized())
        .andReturn()
        .getResponse();
    
    String header = response.getHeader("WWW-Authenticate");
    assertNull(
        "The server should block the WWW-Authenticate header to " +
        "prevent users using browsers from accidentally logging in.",
        header);
  }
  
  @Test
  @Transactional
  public void testAnonymousCanReadProfiles() throws Exception {
    MockHttpServletResponse response = signUp(
        "a@example.com", "password", "Abe", "Ableton")
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();

    Profile profile = responseToProfile(response);

    mockMvc.perform(get("/profiles", profile.getId()))
        .andExpect(status().isOk());
    
    mockMvc.perform(get("/profiles/{id}", profile.getId()))
        .andExpect(status().isOk());
  }
  
  @Test
  @Transactional
  public void testAnonymousCannotModifyProfiles() throws Exception {
    MockHttpServletResponse response = signUp(
        "a@example.com", "password", "Abe", "Ableton")
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();
    
    Profile profile = responseToProfile(response);

    Map<String, Object> post = new HashMap<>();
    post.put("email", "b@example.com");
    post.put("firstName", "First");
    post.put("lastName", "Last");

    Map<String, Object> patch = new HashMap<>();
    post.put("firstName", "First");
    
    // Anon can't post a new profile
    mockMvc.perform(post("/profiles")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(post)))
        .andExpect(status().isUnauthorized());

    // Anon can't replace a profile
    mockMvc.perform(put("/profiles/{id}", profile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(post)))
        .andExpect(status().isUnauthorized());
    
    // Anon can't update individual fields
    mockMvc.perform(patch("/profiles/{id}", profile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().isUnauthorized());
    
    // Anon cannot delete a profile.
    mockMvc.perform(delete("/profiles/{id}", profile.getId()))
        .andExpect(status().isUnauthorized());
  }
  
  @Test
  @Transactional
  public void testBloggerCanModifyOwnProfile() throws Exception {
    String email = "blogger@example.com";
    String password = "password";
    
    MockHttpServletResponse response = signUp(
        email, password, "Bethany", "Blogsalot")
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();
    
    Profile profile = responseToProfile(response);
    
    Map<String, Object> patch = new HashMap<>();
    patch.put("firstName", "Beth");
    
    mockMvc.perform(patch("/profiles/{id}", profile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", authUtil.encodeBasicAuth(email, password))
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().is2xxSuccessful())
        .andReturn()
        .getResponse();

    response = mockMvc.perform(get("/profiles/{id}", profile.getId()))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse();
    
    Resource<Profile> profileResource = responseToProfileResource(response);
    assertEquals(patch.get("firstName"), profileResource.getContent().getFirstName());
  }

  @Test
  @Transactional
  public void testBloggerCannotModifyOthersProfile() throws Exception {
    MockHttpServletResponse response = signUp(
        "blogger@example.com", "password", "Beth", "Blogsalot")
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();

    Profile bethProfile = responseToProfile(response);

    String email = "gh0stwriter@elitefanfiction.com";
    String password = "password";
    signUp(
        email, password, "Spooky", "Stories")
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();

    // Why does gh0stwriter dislike Beth's blog posts so much?
    Map<String, Object> patch = new HashMap<>();
    patch.put("firstName", "Cheap");
    patch.put("lastName", "Hack");
    mockMvc.perform(patch("/profiles/{id}", bethProfile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", authUtil.encodeBasicAuth(email, password))
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().isForbidden());

    mockMvc.perform(delete("/profiles/{id}", bethProfile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", authUtil.encodeBasicAuth(email, password))
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().isForbidden());
  }

  @Test
  @Transactional
  public void testAdminCanModifyAnyProfile() throws Exception {
    MockHttpServletResponse response = signUp(
        "blogger@example.com", "password", "Beth", "Blogsalot")
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();

    Profile bethProfile = responseToProfile(response);
    
    response = signUp(
        "gh0stwriter@elitefanfiction.com", "password", "Spooky", "Stories")
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse();

    Profile ghostProfile = responseToProfile(response);

    Map<String, Object> patch = new HashMap<>();
    patch.put("firstName", "Test");

    mockMvc.perform(patch("/profiles/{id}", bethProfile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", authUtil.encodeBasicAuth("admin", "admin"))
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().is2xxSuccessful());

    mockMvc.perform(patch("/profiles/{id}", ghostProfile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", authUtil.encodeBasicAuth("admin", "admin"))
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().is2xxSuccessful());

    mockMvc.perform(delete("/profiles/{id}", bethProfile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", authUtil.encodeBasicAuth("admin", "admin"))
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().is2xxSuccessful());

    mockMvc.perform(delete("/profiles/{id}", ghostProfile.getId())
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", authUtil.encodeBasicAuth("admin", "admin"))
        .content(objectMapper.writeValueAsString(patch)))
        .andExpect(status().is2xxSuccessful());
  }
  
}
