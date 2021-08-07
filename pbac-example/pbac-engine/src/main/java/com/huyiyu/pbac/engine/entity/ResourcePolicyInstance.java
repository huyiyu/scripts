package com.huyiyu.pbac.engine.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 规则资源关联表
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-05
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("resource_policy_instance")
public class ResourcePolicyInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资源id
     */
    @TableField("resource_id")
    private Long resourceId;

    /**
     * 策略ID
     */
    @TableField("policy_instance_id")
    private Long policyInstanceId;
}
