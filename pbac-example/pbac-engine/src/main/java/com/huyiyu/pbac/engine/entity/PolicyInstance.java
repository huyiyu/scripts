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
 * 策略表,规定了configuration的内容
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-05
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("policy_instance")
public class PolicyInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 策略ID
     */
    @TableField("policy_define_id")
    private Long policyDefineId;

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
    private LocalDateTime deletedTime;
}
