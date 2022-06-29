package com.huyiyu.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 测试导入
 * </p>
 *
 * @author zyy
 * @since 2022-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("db_test")
@Accessors(chain = true)
public class DbTest implements Serializable {


    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 测试字段
     */
    private String field1;

    /**
     * 测试字段
     */
    private String field2;

    /**
     * 测试字段
     */
    private String field3;

    /**
     * 测试字段
     */
    private String field4;

    /**
     * 测试字段
     */
    private String field5;

    /**
     * 测试字段
     */
    private String field6;

    /**
     * 测试字段
     */
    private String field7;

    /**
     * 测试字段
     */
    private String field8;

    /**
     * 测试字段
     */
    private String field9;

    /**
     * 测试字段
     */
    private String field10;

    /**
     * 测试字段
     */
    private String field11;

    /**
     * 测试字段
     */
    private String field12;

    /**
     * 测试字段
     */
    private String field13;

    /**
     * 测试字段
     */
    private String field14;

    /**
     * 测试字段
     */
    private String field15;

    /**
     * 测试字段
     */
    private String field16;

    /**
     * 测试字段
     */
    private String field17;

    /**
     * 测试字段
     */
    private String field18;

    /**
     * 测试字段
     */
    private String field19;

    /**
     * 测试字段
     */
    private String field20;


}
