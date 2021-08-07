package com.huyiyu.simple.entity;

import com.huyiyu.excel.entity.ImportHistory;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SimpleImportHistory implements ImportHistory {

    private Long id;
    private Long importConfigId;
    private String type;
    private Boolean success;
    private Long timeMillis;
    private String errorMsg;
    private String fileUrl;
    private Integer totalRow;
    private Integer failureRow;
    private Integer successRow;
    private LocalDateTime startTime;

}
