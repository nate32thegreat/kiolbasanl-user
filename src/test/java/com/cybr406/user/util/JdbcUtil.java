package com.cybr406.user.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JdbcUtil {

  @Autowired
  JdbcTemplate jdbcTemplate;

  private class UserRowMapper implements RowMapper<Map<String, Object>> {
    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
      String username = rs.getString("username");
      String password = rs.getString("password");
      boolean enabled = rs.getBoolean("enabled");

      Map<String, Object> user = new HashMap<>();
      user.put("username", username);
      user.put("password", password);
      user.put("enabled", enabled);

      return user;
    }
  }
  
  private class AuthorityRowMapper implements RowMapper<Map<String, Object>> {
    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
      String username = rs.getString("username");
      String authority = rs.getString("authority");

      Map<String, Object> auth = new HashMap<>();
      auth.put("username", username);
      auth.put("authority", authority);

      return auth;
    }
  }
  
  public List<Map<String, Object>> getUsers() {
    return jdbcTemplate.query("select * from users", new UserRowMapper());
  }
  
  public Map<String, Object> findUser(String username) {
    try {
      return jdbcTemplate.queryForObject(
          "select * from users where username = ?",
          new UserRowMapper(),
          username);
    } catch (EmptyResultDataAccessException e) {
      e.printStackTrace();
      return null;
    }
  }

  public List<Map<String, Object>> getAuthorities() {
    return jdbcTemplate.query("select * from authorities", new AuthorityRowMapper());
  }

  public List<Map<String, Object>> findAuthorities(String username) {
    return jdbcTemplate.query(
        "select * from authorities where username = ?",
        new AuthorityRowMapper(),
        username);
  }
  
}
