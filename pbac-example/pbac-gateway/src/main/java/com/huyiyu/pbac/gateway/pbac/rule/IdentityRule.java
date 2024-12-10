package com.huyiyu.pbac.gateway.pbac.rule;

import static com.huyiyu.pbac.core.constant.PbacConstant.PBAC_IDENTITY_PREFIX;

import com.huyiyu.pbac.core.domain.PbacContext;
import com.huyiyu.pbac.core.exception.BusiPbacException;
import com.huyiyu.pbac.core.rule.base.AbstractGenericRule;
import com.huyiyu.pbac.core.rule.base.IPbacRule;
import com.huyiyu.pbac.gateway.domain.R;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class IdentityRule implements IPbacRule {

  private final RestClient restClient;
  private final RedisTemplate redisTemplate;

  private List<String> httpForUserIdentity(String accountId) {
    return restClient
        .get()
        .uri("http://pbac-biz/identity/identityListByAccountId?accountId={accountId}", Map.of("accountId", accountId))
        .exchange(((clientRequest, clientResponse) -> clientResponse.bodyTo(
            new ParameterizedTypeReference<R<List<String>>>() {
            }))).orElseThrowException();
  }

  @Override
  public boolean decide(PbacContext pbacContext, String configuration) {
    String accountId = pbacContext.getPbacUser().getAccountId();
    String key = PBAC_IDENTITY_PREFIX + accountId;
    if (redisTemplate.hasKey(key)) {
      return redisTemplate.opsForSet().isMember(key, configuration);
    } else {
      List<String> identityList = httpForUserIdentity(accountId);
      return !CollectionUtils.isEmpty(identityList) && identityList.contains(configuration);
    }
  }
}
