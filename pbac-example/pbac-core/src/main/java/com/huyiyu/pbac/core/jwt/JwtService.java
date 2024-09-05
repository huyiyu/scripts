package com.huyiyu.pbac.core.jwt;

import com.huyiyu.pbac.core.domain.PbacUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public interface JwtService extends JwtDecoder {
  
  String encode(PbacUser user);

  PbacUser jwt2PbacUser(Jwt token);
}
