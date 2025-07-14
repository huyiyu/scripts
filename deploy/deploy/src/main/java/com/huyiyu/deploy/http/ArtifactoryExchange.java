package com.huyiyu.deploy.http;

import org.springframework.core.io.Resource;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface ArtifactoryExchange {

  Resource pull(String s);
}
