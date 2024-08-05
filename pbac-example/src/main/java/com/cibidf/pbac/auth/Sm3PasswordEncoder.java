package com.cibidf.pbac.auth;

import cn.hutool.crypto.SmUtil;
import java.nio.charset.StandardCharsets;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class Sm3PasswordEncoder implements PasswordEncoder {


  @Override
  public String encode(CharSequence rawPassword) {
    return "";
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes servletRequestAttributes) {
      String username = servletRequestAttributes.getRequest().getParameter("username");
      rawPassword = SmUtil.sm3WithSalt(username.getBytes(StandardCharsets.UTF_8))
          .digestHex(rawPassword.toString());
      return StringUtils.hasText(rawPassword) && rawPassword.equals(encodedPassword);
    }
    return false;
  }
}
