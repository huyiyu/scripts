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
 * 请求客户端资源
 * </p>
 *
 * @author huyiyu
 * @since 2024-09-03
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("resource")
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 策略ID
     */
    @TableField("policy_id")
    private Long policyId;

    /**
     * 资源名称
     */
    @TableField("name")
    private String name;

    /**
     * 匹配规则,可以是精准的或者模糊的,policy 区分,因为精准的更快匹配
     */
    @TableField("pattern")
    private String pattern;

    /**
     * 1 uri 精确匹配,2 uri 模糊匹配,3 table 匹配
     */
    @TableField("match_type")
    private Byte matchType;

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
