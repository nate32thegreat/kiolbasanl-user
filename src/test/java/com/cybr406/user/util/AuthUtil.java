package com.cybr406.user.util;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class AuthUtil {

  public String encodeBasicAuth(String username, String password) {
    String auth = String.format("%s:%s", username, password);
    String encoded = Base64.getEncoder().encodeToString(auth.getBytes());
    return String.format("Basic %s", encoded);
  }
  
  public String bearerTokenAuth(String token) {
    return String.format("Bearer %s", token);
  }
  
}
