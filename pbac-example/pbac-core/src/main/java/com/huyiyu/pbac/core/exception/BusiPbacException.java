package com.huyiyu.pbac.core.exception;

import javax.naming.AuthenticationException;

public class BusiPbacException extends RuntimeException {

  public BusiPbacException(Exception e) {
    super(e.getMessage());
  }

  public BusiPbacException(String message) {super(message);}
}
