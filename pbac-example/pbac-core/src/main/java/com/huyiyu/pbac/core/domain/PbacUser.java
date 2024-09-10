package com.huyiyu.pbac.core.domain;


import java.util.Collection;
import java.util.List;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class PbacUser implements UserDetails {

  public static final String USERNAME_KEY = "username";
  public static final String ACCOUNT_ID_KEY = "accountId";
  public static final String AUTHORITIES_KEY = "roleCodes";

  private String accountId;
  private String password;
  private String username;
  private List<String> roleCodes;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return roleCodes.stream().map(SimpleGrantedAuthority::new).toList();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }
}
