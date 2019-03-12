package com.cybr406.user.homework3;

import com.cybr406.user.BaseTest;
import org.junit.Test;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@DirtiesContext
public class UserSetupTests extends BaseTest {
  
  @Test
  public void testUsersTable() {
    try {
      List<Map<String, Object>> users = jdbcUtil.getUsers();
      
      Map<String, Object> admin = users.stream()
          .filter(user -> "admin".equals(user.get("username")))
          .findFirst()
          .orElse(null);
      
      assertNotNull("An user with username 'admin' must exist.", admin);
      
      assertNotEquals(
          "You must ensure passwords are not stored in plain text in the database.",
          "admin",
          admin.get("password"));
    } catch (BadSqlGrammarException e) {
      e.printStackTrace();
      fail("Unable select users from users table. Does it exist? Are its columns correct?");
    }
  }
  
  @Test
  public void testAuthoritiesTable() {
    try {
      List<Map<String, Object>> authorities = jdbcUtil.getAuthorities();

      Map<String, Object> adminAuthority = authorities.stream()
          .filter(auth -> "admin".equals(auth.get("username")))
          .findFirst()
          .orElse(null);

      assertNotNull("An authority for username 'admin' must exist.", adminAuthority);

      assertEquals(
          "User 'admin' must have authority 'ADMIN'.",
          "ROLE_ADMIN",
          adminAuthority.get("authority"));
      
    } catch (BadSqlGrammarException e) {
      e.printStackTrace();
      fail("Unable select authorities from authorities table. Does it exist? Are its columns correct?");
    }
  }
  
}
