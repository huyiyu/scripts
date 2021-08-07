package com.huyiyu.pbac.core.jwt;

import com.huyiyu.pbac.core.domain.LoginUser;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public interface JwtService extends JwtDecoder {
  
  String encode(LoginUser user);
}
