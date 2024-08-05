package com.cibidf.pbac.auth.manage.uri;

import com.cibidf.pbac.auth.domain.LoginUser;
import com.cibidf.pbac.auth.jwt.LoginUserAcuhenticationToken;
import com.huyiyu.auth.domain.PolicyUser;
import com.huyiyu.auth.service.PolicyMatcher;
import jakarta.servlet.http.HttpServletRequest;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@RequiredArgsConstructor
public class UriPolicyAuthorizationManager<R, U> implements
    AuthorizationManager<RequestAuthorizationContext> {

  private static final AuthorizationDecision ACCEPT = new AuthorizationDecision(true);
  private static final AuthorizationDecision DENY = new AuthorizationDecision(false);

  private final PolicyMatcher<HttpServletRequest, Long> policyMatcher;


  @Override
  public AuthorizationDecision check(Supplier<Authentication> authentication,
      RequestAuthorizationContext object) {
    if (authentication.get() instanceof LoginUserAcuhenticationToken loginUserAcuhenticationToken) {
      LoginUser principal = loginUserAcuhenticationToken.getLoginUser();
      return policyMatcher.decide(object.getRequest(), principal) ? ACCEPT : DENY;
    }
    return DENY;
  }
}
