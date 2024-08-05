package com.huyiyu.auth.domain;

public interface PolicyUser {

  Object attribute(String attribute);

  void putttribute(String attribute, Object value);
}
