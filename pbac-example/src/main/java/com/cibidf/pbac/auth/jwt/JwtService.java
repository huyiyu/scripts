package com.cibidf.pbac.auth.jwt;

import com.cibidf.pbac.auth.domain.LoginUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public interface JwtService extends JwtDecoder {


  String encode(LoginUser user);

  AbstractAuthenticationToken convertJwt(Jwt jwt);
}
