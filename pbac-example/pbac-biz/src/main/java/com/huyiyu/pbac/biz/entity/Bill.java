package com.huyiyu.pbac.biz.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 成交单
 * </p>
 *
 * @author huyiyu
 * @since 2024-08-02
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 描述
     */
    @TableField("descriptions")
    private String descriptions;

    /**
     * 成交价
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 首付
     */
    @TableField("first_price")
    private BigDecimal firstPrice;

    /**
     * 销售员id
     */
    @TableField("salesman_id")
    private Long salesmanId;

    /**
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

    /**
     * 所在地区
     */
    @TableField("area_id")
    private Long areaId;

    /**
     * 地区名称
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 订单状态
     */
    @TableField("bill_status")
    private Byte billStatus;

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
