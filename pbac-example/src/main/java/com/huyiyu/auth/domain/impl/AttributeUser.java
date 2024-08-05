package com.huyiyu.auth.domain.impl;

import com.huyiyu.auth.domain.PolicyUser;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AttributeUser implements PolicyUser {

  private final Map<String, Object> attributes = new ConcurrentHashMap<>();


  @Override
  public Object attribute(String attribute) {
    return attributes.get(attribute);
  }

  @Override
  public void putttribute(String attribute, Object value) {
    attributes.put(attribute, value);
  }
}
