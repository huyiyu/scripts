package com.huyiyu.auth.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.huyiyu.auth.domain.PolicyUser;
import java.util.List;

public interface PolicyMatcher<P, U> {


  List<Pair<Long, String>> match(P pattern);

  PolicyHandler getPolicyHandler(Long policyId);


  default boolean decide(P pattern, PolicyUser user) {
    List<Pair<Long, String>> matchePolicys = match(pattern);
    if (CollectionUtil.isNotEmpty(matchePolicys)) {
      for (Pair<Long, String> pair : matchePolicys) {
        PolicyHandler policyHandler = getPolicyHandler(pair.getKey());
        if (policyHandler.decide(user, pair.getValue())) {
          return true;
        }
      }
    }
    return false;
  }
}
