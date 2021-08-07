package com.huyiyu.pbac.engine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huyiyu.pbac.engine.entity.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * <p>
 * 请求客户端资源 服务类
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-03
 */
public interface IResourceService extends IService<Resource> {

  Optional<Long> getResourceIdByPattern(HttpServletRequest request);
}
