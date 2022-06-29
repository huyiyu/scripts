package com.huyiyu.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huyiyu.excel.entity.ImportHistory;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 数据导入历史
 * </p>
 *
 * @author zyy
 * @since 2022-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("excel_import_history")
public class ExcelImportHistory implements Serializable, ImportHistory {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属项目
     */
    private String project;

    /**
     * 导入ID
     */
    @TableField("import_config_id")
    private Long importConfigId;

    /**
     * 类型
     */
    private String type;

    /**
     * 默认成功,表示正常结束
     */
    private Boolean success;

    /**
     * 异常信息
     */
    @TableField("error_msg")
    private String errorMsg;

    /**
     * 成功条数
     */
    @TableField("success_row")
    private Integer successRow;

    /**
     * 失败条数
     */
    @TableField("failure_row")
    private Integer failureRow;

    /**
     * 总条数
     */
    @TableField("total_row")
    private Integer totalRow;

    /**
     * 结果文件地址
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 导入开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 导入结束时间
     */
    @TableField("time_millis")
    private Long timeMillis;

    /**
     * 最早出错的行
     */
    @TableField("error_row")
    private Integer errorRow;


}
