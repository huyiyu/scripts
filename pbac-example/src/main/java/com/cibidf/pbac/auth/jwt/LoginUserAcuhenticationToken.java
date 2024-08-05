package com.cibidf.pbac.auth.jwt;

import com.cibidf.pbac.auth.domain.LoginUser;
import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class LoginUserAcuhenticationToken extends JwtAuthenticationToken {

  @Getter
  private LoginUser loginUser;

  public LoginUserAcuhenticationToken(LoginUser loginUser, Jwt jwt) {
    super(jwt);
    this.loginUser = loginUser;
  }


}
