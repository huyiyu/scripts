package com.huyiyu.pbac.core.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class LoginUser implements UserDetails {

  private static final List<GrantedAuthority> authorities = new ArrayList<>();

  public static final String USERNAME_KEY = "username";
  public static final String PASSWORD_KEY = "password";
  public static final String ACCOUNT_ID_KEY = "accountId";

  private String accountId;
  private String password;
  private String username;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }
}