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
 * 房管局审核员
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("house_management_admin")
public class HouseManagementAdmin implements Serializable {

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
     * 所在地区ID
     */
    @TableField("area_id")
    private Long areaId;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickName;

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
