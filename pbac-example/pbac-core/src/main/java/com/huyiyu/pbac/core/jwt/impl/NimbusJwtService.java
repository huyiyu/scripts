package com.huyiyu.pbac.core.jwt.impl;

import com.huyiyu.pbac.core.domain.PbacUser;
import com.huyiyu.pbac.core.jwt.JwtService;
import com.huyiyu.pbac.core.property.PbacProperties;
import com.huyiyu.pbac.core.utils.DateUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.Assert;

@Slf4j
public class NimbusJwtService implements JwtService {


  private final PbacProperties.Jwt jwt;

  private final JWSSigner jwsSigner;
  private final JWSVerifier jwsVerifier;


  public NimbusJwtService(PbacProperties.Jwt jwt) {
    this.jwt = jwt;
    this.jwsVerifier = createJwsVerifier(jwt);
    this.jwsSigner = createJwsSigner(jwt);
  }

  private static JWSVerifier createJwsVerifier(PbacProperties.Jwt jwt) {
    try {
      return new MACVerifier(jwt.getSecret());
    } catch (JOSEException e) {
      throw new RuntimeException(e);
    }
  }

  private JWSSigner createJwsSigner(PbacProperties.Jwt jwt) {
    try {
      return new MACSigner(jwt.getSecret());
    } catch (JOSEException e) {
      throw new RuntimeException("生成ed25519密钥失败", e);
    }
  }

  public String encode(PbacUser user) {
    user.setPassword("");
    JWTClaimsSet claimsSet = new Builder()
        .subject(user.getUsername())
        .claim(PbacUser.ACCOUNT_ID_KEY, user.getAccountId())
        .claim(PbacUser.USERNAME_KEY, user.getUsername())
        .claim(PbacUser.AUTHORITIES_KEY, user.getRoleCodes())
        .expirationTime(DateUtil.onFutureByDuration(jwt.getExpiration()))
        .issuer(jwt.getIssuer())
        .audience(jwt.getAdience())
        .issueTime(new Date())
        .jwtID(String.valueOf(user.getAccountId()))
        .build();
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);
    sign(signedJWT);
    return signedJWT.serialize();


  }

  private void sign(SignedJWT signedJWT) {
    try {
      signedJWT.sign(jwsSigner);
    } catch (JOSEException e) {
      throw new RuntimeException("签名出错", e);
    }
  }

  public Jwt decode(String token) throws JwtException {
    try {
      SignedJWT parse = SignedJWT.parse(token);
      JWTClaimsSet jwtClaimsSet = parse.getJWTClaimsSet();
      Assert.isTrue(parse.verify(jwsVerifier), "签名校验失败");
      Assert.isTrue(DateUtil.isAfter(jwtClaimsSet.getExpirationTime(), new Date()), "签名已过期");
      return Jwt.withTokenValue(token)
          .headers(headers -> {
            headers.putAll(parse.getHeader().toJSONObject());
          })
          .audience(jwtClaimsSet.getAudience())
          .claims(map -> map.putAll(jwtClaimsSet.getClaims()))
          .issuer(jwtClaimsSet.getIssuer())
          .expiresAt(jwtClaimsSet.getExpirationTime().toInstant())
          .issuedAt(jwtClaimsSet.getIssueTime().toInstant())
          .jti(jwtClaimsSet.getJWTID())
          .subject(jwtClaimsSet.getSubject())
          .build();
    } catch (ParseException | JOSEException e) {
      throw new JwtException("解析jwt出现异常", e);
    }
  }

  @Override
  public PbacUser jwt2PbacUser(Jwt token) {
    PbacUser loginUser = new PbacUser();
    loginUser.setAccountId(token.getClaimAsString(PbacUser.ACCOUNT_ID_KEY));
    loginUser.setUsername(token.getClaimAsString(PbacUser.USERNAME_KEY));
    loginUser.setRoleCodes(token.getClaimAsStringList(PbacUser.AUTHORITIES_KEY));
    return loginUser;
  }
}