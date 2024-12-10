package com.huyiyu.pbac.gateway.pbac.rule;

import static com.huyiyu.pbac.core.constant.PbacConstant.PBAC_ROLE_CODES_PREFIX;

import com.huyiyu.pbac.core.rule.base.impl.RoleBaseRule;
import com.huyiyu.pbac.gateway.domain.R;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class GatewayRoleRule extends RoleBaseRule {


  private final RestClient restClient;
  private final RedisTemplate redisTemplate;

  @Override
  public Collection<String> listRoleCodesByResourceId(Long resourceId) {
    String key = PBAC_ROLE_CODES_PREFIX + resourceId;
    if (redisTemplate.hasKey(key)) {
      return (Collection<String>) redisTemplate.opsForSet().members(key);
    }else {
      return httpForRoleCodesByResourceId(resourceId);
    }
  }

  private List<String> httpForRoleCodesByResourceId(Long resourceId) {
    return restClient.get()
        .uri("http://pbac-engine/role/roleCodesByResourceId?resourceId={resourceId}", Map.of("resourceId", resourceId))
        .exchange(((clientRequest, clientResponse) -> clientResponse
            .bodyTo(new ParameterizedTypeReference<R<List<String>>>() {
            }).orElseThrowException()));
  }
}
