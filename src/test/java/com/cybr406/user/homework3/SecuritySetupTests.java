package com.cybr406.user.homework3;

import com.cybr406.user.BaseTest;
import org.junit.Test;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@DirtiesContext
public class SecuritySetupTests extends BaseTest {
  
  @Test
  public void testSecurityConfigExists() {
    try {
      Class clazz = Class.forName("com.cybr406.user.configuration.SecurityConfiguration");
      
      assertTrue("SecurityConfiguration must extend WebSecurityConfigurerAdapter",
          WebSecurityConfigurerAdapter.class.isAssignableFrom(clazz));
      
    } catch (ClassNotFoundException e) {
      fail("Create com.cybr406.user.configuration.SecurityConfiguration");
    }
  }
  
  
}
