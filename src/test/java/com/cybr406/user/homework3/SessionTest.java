package com.cybr406.user.homework3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SessionTest {
  
  @LocalServerPort
  int port;
  
  RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
  
  @Autowired
  ObjectMapper objectMapper;
  
  @Test
  public void testSession() throws Exception {
    Map<String, Object> signUp = new HashMap<>();
    signUp.put("email", "test@example.com");
    signUp.put("password", "password");
    signUp.put("firstName", "Test");
    signUp.put("lastName", "Testerton");

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    ParameterizedTypeReference<Map<String, Object>> typeReference = new ParameterizedTypeReference<Map<String, Object>>() {};
    
    ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
        "http://localhost:{port}/signup",
        HttpMethod.POST,
        new HttpEntity<>(signUp, httpHeaders),
        typeReference,
        port);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    Map<String, Object> profile = response.getBody();
    assertNotNull(profile);
    List<String> cookie = response.getHeaders().get("Set-Cookie");
    assertNull("The server tried to set a cookie. Have you made your sever stateless?", cookie);
    
    Map<String, Object> patch = new HashMap<>();
    signUp.put("firstName", "Mr. Test");

    httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setBasicAuth("test@example.com", "password");
    
    response = restTemplate.exchange(
        "http://localhost:{port}/profiles/{id}",
        HttpMethod.PATCH,
        new HttpEntity<>(patch, httpHeaders),
        typeReference,
        port,
        profile.get("id"));

    assertTrue(response.getStatusCode().is2xxSuccessful());
    
    cookie = response.getHeaders().get("Set-Cookie");
    assertNull("The server tried to set a cookie. Have you made your sever stateless?", cookie);
  }
  
}
