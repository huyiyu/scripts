DROP TABLE IF EXISTS `db_test`;
CREATE TABLE `db_test`
(
    `id`      bigint(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `field1`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field2`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field3`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field4`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field5`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field6`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field7`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field8`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field9`  varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field10` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field11` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field12` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field13` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field14` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field15` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field16` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field17` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field18` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field19` varchar(30) DEFAULT NULL COMMENT '测试字段',
    `field20` varchar(30) DEFAULT NULL COMMENT '测试字段',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='测试导入';


DROP TABLE IF EXISTS `excel_import_config`;
CREATE TABLE `excel_import_config`
(
    `id`             bigint(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project`        varchar(20)                  DEFAULT NULL COMMENT '所属项目',
    `read_step`      int(10) unsigned    NOT NULL DEFAULT 100 COMMENT '读取步长',
    `write_step`     int(10) unsigned    NOT NULL DEFAULT 1000 COMMENT '写入步长',
    `title`          varchar(50)                  DEFAULT NULL COMMENT '导入名称',
    `type`           varchar(50)                  DEFAULT NULL COMMENT '类型,强制使用泛形类的小写驼峰',
    `max_row`        int(16) unsigned             DEFAULT NULL COMMENT '最多导入条数,限制excel最多支持多少行导入',
    `sync`           tinyint(1)                   DEFAULT 0 COMMENT '是否同步返回,设置是流程阻塞直到运行结束',
    `lock_second`    bigint(16) unsigned          DEFAULT 40 COMMENT '锁等待时间',
    `release_second` bigint(16) unsigned          DEFAULT 300 COMMENT '锁超时释放时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4 COMMENT ='数据导入类型';


DROP TABLE IF EXISTS `excel_import_history`;
CREATE TABLE `excel_import_history`
(
    `id`               bigint(16) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `project`          varchar(20)      DEFAULT NULL COMMENT '所属项目',
    `import_config_id` bigint(16) unsigned NOT NULL COMMENT '导入ID',
    `type`             varchar(30)         NOT NULL COMMENT '类型',
    `success`          tinyint(1)       DEFAULT 1 COMMENT '默认成功,表示正常结束',
    `error_msg`        text             DEFAULT NULL COMMENT '异常信息',
    `success_row`      int(16) unsigned DEFAULT NULL COMMENT '成功条数',
    `failure_row`      int(16) unsigned DEFAULT NULL COMMENT '失败条数',
    `total_row`        int(16) unsigned DEFAULT NULL COMMENT '总条数',
    `file_url`         text      DEFAULT NULL COMMENT '结果文件地址',
    `start_time`       datetime         DEFAULT NULL COMMENT '导入开始时间',
    `time_millis`      bigint(20)       DEFAULT NULL COMMENT '导入结束时间',
    `error_row`        int(16)          DEFAULT NULL COMMENT '最早出错的行',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='数据导入历史';

