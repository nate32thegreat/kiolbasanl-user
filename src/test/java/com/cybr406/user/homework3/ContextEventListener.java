package com.cybr406.user.homework3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ContextEventListener {

  @Autowired
  JdbcTemplate jdbcTemplate;

  @EventListener
  public void b(ContextClosedEvent event) {
    jdbcTemplate.execute("drop table users");
    jdbcTemplate.execute("drop table authorities");
    jdbcTemplate.execute("delete from profile");
  }
  
}
