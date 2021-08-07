package com.huyiyu.pbac.core.exception;

import java.io.IOException;
import javax.naming.AuthenticationException;

public class BusiAuthenticationException extends AuthenticationException {

  public BusiAuthenticationException(Exception e) {
    super(e.getMessage());
  }

  public BusiAuthenticationException(String message) {super(message);}
}
