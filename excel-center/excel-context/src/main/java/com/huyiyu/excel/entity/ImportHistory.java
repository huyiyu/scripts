package com.huyiyu.excel.entity;

import java.time.LocalDateTime;

public interface ImportHistory {

    void setId(Long id);
    Long getId();
    void setImportConfigId(Long importConfigId);
    void setType(String type);
    void setSuccess(Boolean success);
    void setTimeMillis(Long timeMillis);
    void setErrorMsg(String errorMsg);
    void setFileUrl(String fileUrl);
    void setTotalRow(Integer totalRow);
    void setFailureRow(Integer failureRow);
    void setSuccessRow(Integer successRow);
    void setStartTime(LocalDateTime startTime);
}
