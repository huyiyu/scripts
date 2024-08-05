package com.cibidf.pbac.auth.domain;

import com.huyiyu.auth.domain.impl.AttributeUser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class LoginUser extends AttributeUser implements UserDetails {

  public static final String ROLE_NAMES_KEY = "roleNames";
  public static final String USERNAME_KEY = "username";
  public static final String PASSWORD_KEY = "password";
  public static final String ACCOUNT_ID_KEY = "accountId";

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return new ArrayList<>();
  }

  @Override
  public String getPassword() {
    return (String) attribute(PASSWORD_KEY);
  }

  @Override
  public String getUsername() {

    return (String) attribute(USERNAME_KEY);
  }


}
