package com.huyiyu.pbac.biz.entity;

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
 * 销售员表
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("salesman")
public class Salesman implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号ID
     */
    @TableField("account_id")
    private Long accountId;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 从业年限
     */
    @TableField("job_year")
    private Byte jobYear;

    /**
     * 职业等级
     */
    @TableField("job_level")
    private Byte jobLevel;

    /**
     * 是否部门领导
     */
    @TableField("is_leader")
    private Boolean leader;

    /**
     * 部门ID
     */
    @TableField("department_id")
    private Long departmentId;

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
