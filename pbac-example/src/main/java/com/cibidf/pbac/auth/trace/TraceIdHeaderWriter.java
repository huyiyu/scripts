package com.cibidf.pbac.auth.trace;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.util.StringUtils;

public class TraceIdHeaderWriter implements HeaderWriter {


  private static final String TRACEID_HEADER = "TID";

  @Override
  public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
    String traceId = TraceContext.traceId();
    if (StringUtils.hasText(traceId)) {
      response.setHeader(TRACEID_HEADER, traceId);
    }
  }
}
