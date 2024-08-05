package com.cibidf.pbac.auth.policy;

import com.cibidf.pbac.auth.domain.LoginUser;
import com.cibidf.pbac.entity.HouseManagementAdmin;
import com.cibidf.pbac.service.IHouseManagementAdminService;
import com.cibidf.pbac.utils.JsonUtil;
import com.huyiyu.auth.domain.PolicyUser;
import com.huyiyu.auth.service.PolicyHandler;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimePolicyHandler implements PolicyHandler {

  private final IHouseManagementAdminService houseManagementAdminService;


  @Data
  public static class TimeParam {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
  }

  @Override
  public boolean decide(PolicyUser user, String policyParam) {
    if (user instanceof LoginUser loginUser) {
      Long accountId = Long.parseLong(loginUser.attribute(LoginUser.ACCOUNT_ID_KEY).toString());
      if (!houseManagementAdminService.contains(accountId)) {
        return false;
      }
      TimeParam timeParam = JsonUtil.json2Object(policyParam, TimeParam.class);
      if (timeParam.getStartTime() == null || timeParam.getEndTime() == null) {
        log.warn("该次匹配未填写开始时间和结束时间,检查policyResource配置");
        return false;
      }
      return timeParam.getStartTime().isBefore(LocalDateTime.now())
          && timeParam.getEndTime().isAfter(LocalDateTime.now());
    }
    return false;
  }
}
