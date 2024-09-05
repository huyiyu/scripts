package com.huyiyu.pbac.core.jwt.holder;

import com.huyiyu.pbac.core.domain.PbacUser;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;

public class PbacUserHolder {

  private static final ThreadLocal<PbacUser> threadLocal = new ThreadLocal<>();

  public static PbacUser getUser() {
    return threadLocal.get();
  }


  public static void setUser(PbacUser user) {
    threadLocal.set(user);
  }

  public static void removeUser() {
    threadLocal.remove();
  }

}
