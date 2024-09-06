package com.huyiyu.pbac.engine.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 规则组合表
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-06
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("policy_rule")
public class PolicyRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则ID
     */
    @TableField("rule_id")
    private Long ruleId;

    /**
     * 策略ID
     */
    @TableField("policy_id")
    private Long policyId;

    /**
     * 维度
     */
    @TableField("rule_level")
    private Byte ruleLevel;

    /**
     * 组合条件 1.必要,2充分
     */
    @TableField("condition_type")
    private Boolean conditionType;

    /**
     * 规则优先级
     */
    @TableField("pirority")
    private Integer pirority;

    /**
     * 参数描述,方便绑定策略时填写
     */
    @TableField("param_value")
    private String paramValue;

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
    @TableLogic
    private LocalDateTime deletedTime;
}
