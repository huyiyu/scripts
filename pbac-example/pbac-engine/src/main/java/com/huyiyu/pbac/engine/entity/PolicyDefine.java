package com.huyiyu.pbac.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 鉴权规则表
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-05
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("policy_define")
public class PolicyDefine implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则说明
     */
    @TableField("policy_detail")
    private String policyDetail;

    /**
     * 指定执行器，与script二选一,handlerName优先
     */
    @TableField("handler_name")
    private String handlerName;

    /**
     * 执行脚本,与handler_name 二选一,handlerName优先
     */
    @TableField("scripts")
    private String scripts;

    /**
     * 参数描述,json格式案例[{"name":"名称","value":"值","desc":"描述"}]
     */
    @TableField("param_desc")
    private String paramDesc;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    @TableField("deleted_time")
    private LocalDateTime deletedTime;
}
