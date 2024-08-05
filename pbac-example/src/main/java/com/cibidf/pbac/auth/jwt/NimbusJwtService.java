package com.cibidf.pbac.auth.jwt;

import com.cibidf.pbac.auth.domain.LoginUser;
import com.cibidf.pbac.property.PbacProperties;
import com.cibidf.pbac.utils.DateUtil;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;


public class NimbusJwtService implements JwtService {


  private static final Logger log = LoggerFactory.getLogger(NimbusJwtService.class);
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

  @Override
  public String encode(LoginUser user) {
    JWTClaimsSet claimsSet = new Builder()
        .subject(user.getUsername())
        .claim(LoginUser.ACCOUNT_ID_KEY, user.attribute(LoginUser.ACCOUNT_ID_KEY))
        .claim(LoginUser.USERNAME_KEY, user.getUsername())
        .claim(LoginUser.ROLE_NAMES_KEY, user.attribute(LoginUser.ROLE_NAMES_KEY))
        .expirationTime(DateUtil.onFutureByDuration(jwt.getExpiration()))
        .issuer(jwt.getIssuer())
        .audience(jwt.getAdience())
        .issueTime(new Date())
        .jwtID(String.valueOf(user.attribute(LoginUser.ACCOUNT_ID_KEY)))
        .build();
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS512), claimsSet);
    ;
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


  @Override
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

  private LoginUser jwt2LoginUser(Jwt token) {
    LoginUser loginUser = new LoginUser();
    loginUser.putttribute(LoginUser.ACCOUNT_ID_KEY, token.getClaimAsString(LoginUser.ACCOUNT_ID_KEY));
    loginUser.putttribute(LoginUser.USERNAME_KEY, token.getClaimAsString(LoginUser.USERNAME_KEY));
    List<String> roleNames = token.getClaimAsStringList(LoginUser.ROLE_NAMES_KEY);
    if (CollectionUtils.isEmpty(roleNames)){
      roleNames = new ArrayList<>();
    }
    loginUser.putttribute(LoginUser.ROLE_NAMES_KEY, roleNames);
    return loginUser;
  }

  @Override
  public AbstractAuthenticationToken convertJwt(Jwt jwt) {
    return new LoginUserAcuhenticationToken(jwt2LoginUser(jwt), jwt);
  }
}