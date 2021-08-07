package com.huyiyu.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huyiyu.excel.entity.ImportConfig;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 数据导入类型
 * </p>
 *
 * @author zyy
 * @since 2022-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("excel_import_config")
public class ExcelImportConfig implements Serializable, ImportConfig {


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
     * 读取步长
     */
    @TableField("read_step")
    private Integer readStep;

    /**
     * 写入步长
     */
    @TableField("write_step")
    private Integer writeStep;

    /**
     * 导入名称
     */
    private String title;

    /**
     * 类型,强制使用泛形类的小写驼峰
     */
    private String type;

    /**
     * 最多导入条数,限制excel最多支持多少行导入
     */
    @TableField("max_row")
    private Integer maxRow;

    /**
     * 是否同步返回,设置是流程阻塞直到运行结束
     */
    private Boolean sync;

    /**
     * 锁等待时间
     */
    @TableField("lock_second")
    private Long lockSecond;

    /**
     * 锁超时释放时间
     */
    @TableField("release_second")
    private Long releaseSecond;


}
