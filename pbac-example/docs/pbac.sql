

/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policy_define`
--

LOCK TABLES `policy_define` WRITE;
/*!40000 ALTER TABLE `policy_define`
    DISABLE KEYS */;
INSERT INTO `policy_define`
VALUES (1, '基于角色的权限校验', 'rolePolicyHandler', '',
        '[{\"name\":\"roleCode\",\"desc\":\"角色对应的ID\",\"type\":\"java.util.String\"}]',
        '2024-08-04 08:07:39', '2024-08-04 08:07:45', '1000-01-01 00:00:00'),
       (3, '基于时间的权限校验', 'timePolicyHandler', '',
        '[{\"name\":\"startTime\",\"desc\":\"开始时间\",\"type\":\"java.time.LocalDateTime\"},{\"name\":\"endTime\",\"desc\":\"结束时间\",\"type\":\"java.time.LocalDateTime\"}]',
        '2024-08-04 23:42:39', '2024-08-04 23:42:42', '1000-01-01 00:00:00');
/*!40000 ALTER TABLE `policy_define`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `policy_instance`
--

DROP TABLE IF EXISTS `policy_instance`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `policy_instance`
(
    `id`               bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `policy_define_id` bigint   NOT NULL COMMENT '策略ID',
    `param_value`      varchar(100) DEFAULT NULL COMMENT '参数描述,方便绑定策略时填写',
    `create_time`      datetime NOT NULL COMMENT '创建时间',
    `update_time`      datetime NOT NULL COMMENT '更新时间',
    `deleted_time`     datetime     DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='策略表,规定了configuration的内容';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `policy_instance`
--

LOCK TABLES `policy_instance` WRITE;
/*!40000 ALTER TABLE `policy_instance`
    DISABLE KEYS */;
INSERT INTO `policy_instance`
VALUES (1, 1, '{\"roleCode\":\"customer\"}', '2024-08-04 08:07:39', '2024-08-04 08:07:45',
        '1000-01-01 00:00:00'),
       (2, 3, '{\"startTime\":\"2024-08-05 09:00:00\",\"endTime\":\"2024-08-06 09:00:00\"}',
        '2024-08-04 08:07:39', '2024-08-04 08:07:45', '1000-01-01 00:00:00');
/*!40000 ALTER TABLE `policy_instance`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource`
--

DROP TABLE IF EXISTS `resource`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resource`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         varchar(20)  NOT NULL COMMENT '资源名称',
    `pattern`      varchar(200) NOT NULL COMMENT '匹配规则,可以是精准的或者模糊的,policy 区分,因为精准的更快匹配',
    `match_type`   tinyint      NOT NULL DEFAULT '0' COMMENT '1 uri 精确匹配,2 uri 模糊匹配,3 table 匹配,4.',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `update_time`  datetime     NOT NULL COMMENT '更新时间',
    `deleted_time` datetime              DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='请求客户端资源';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
/*!40000 ALTER TABLE `resource`
    DISABLE KEYS */;
INSERT INTO `resource`
VALUES (1, '购房结果分页查询', '/bill/page', 1, '2024-08-04 22:20:19', '2024-08-04 22:20:21',
        '1000-01-01 00:00:00');
/*!40000 ALTER TABLE `resource`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resource_policy_instance`
--

DROP TABLE IF EXISTS `resource_policy_instance`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resource_policy_instance`
(
    `id`                 bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `resource_id`        bigint NOT NULL COMMENT '资源id',
    `policy_instance_id` bigint NOT NULL COMMENT '策略ID',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='规则资源关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource_policy_instance`
--

LOCK TABLES `resource_policy_instance` WRITE;
/*!40000 ALTER TABLE `resource_policy_instance`
    DISABLE KEYS */;
INSERT INTO `resource_policy_instance`
VALUES (1, 1, 1),
       (2, 1, 2);
/*!40000 ALTER TABLE `resource_policy_instance`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role`
(
    `id`           bigint      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         varchar(10) NOT NULL COMMENT '角色名称',
    `code`         varchar(10) NOT NULL COMMENT '角色编码',
    `create_time`  datetime    NOT NULL COMMENT '创建时间',
    `update_time`  datetime    NOT NULL COMMENT '更新时间',
    `deleted_time` datetime DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='房管局审核员';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role`
    DISABLE KEYS */;
INSERT INTO `role`
VALUES (1, '房产客户', 'customer', '2024-08-04 22:26:13', '2024-08-04 22:26:15',
        '1000-01-01 00:00:00');
/*!40000 ALTER TABLE `role`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salesman`
--

DROP TABLE IF EXISTS `salesman`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salesman`
(
    `id`            bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `account_id`    bigint      DEFAULT NULL COMMENT '登录账号ID',
    `nick_name`     varchar(10) DEFAULT NULL COMMENT '昵称',
    `job_year`      tinyint     DEFAULT NULL COMMENT '从业年限',
    `job_level`     tinyint     DEFAULT NULL COMMENT '职业等级',
    `is_leader`     tinyint(1)  DEFAULT NULL COMMENT '是否部门领导',
    `department_id` bigint      DEFAULT NULL COMMENT '部门ID',
    `create_time`   datetime    DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime    DEFAULT NULL COMMENT '更新时间',
    `deleted_time`  datetime    DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 41
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='销售员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salesman`
--

LOCK TABLES `salesman` WRITE;
/*!40000 ALTER TABLE `salesman`
    DISABLE KEYS */;
INSERT INTO `salesman`
VALUES (1, 1, '马里奥', 5, 3, 1, 1, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (2, 2, '路易吉', 7, 4, 1, 2, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (3, 3, '桃子', 6, 2, 1, 3, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (4, 4, '耀西', 8, 5, 1, 4, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (5, 5, '库巴', 10, 6, 1, 5, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (6, 6, '大金刚', 4, 3, 1, 6, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (7, 7, '迪迪刚', 9, 4, 1, 7, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (8, 8, '蘑菇头', 3, 2, 1, 8, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (9, 9, '蘑菇头女孩', 5, 3, 1, 9, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (10, 10, '瓦里奥', 7, 4, 1, 10, '2024-07-31 09:00:31', '2024-07-31 09:00:31','1000-01-01 00:00:00'),
       (11, 11, '瓦路易吉', 2, 2, 0, 1, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (12, 12, '罗莎琳娜', 4, 3, 0, 1, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (13, 13, '鸟人', 6, 4, 0, 2, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (14, 14, '卡梅克', 3, 3, 0, 2, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (15, 15, '库巴兵', 8, 5, 0, 3, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (16, 16, '害羞鬼', 7, 4, 0, 3, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (17, 17, '拉奇库', 5, 3, 0, 4, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (18, 18, '蒙提鼹鼠', 4, 2, 0, 4, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (19, 19, '锤兄弟', 6, 3, 0, 5, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (20, 20, '回旋镖兄弟', 3, 2, 0, 5, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (21, 21, '火焰兄弟', 5, 3, 0, 6, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (22, 22, '链条咬咬', 4, 2, 0, 6, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (23, 23, '食人花', 7, 4, 0, 7, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (24, 24, '甲壳虫', 6, 3, 0, 7, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (25, 25, '刺刺龟', 5, 2, 0, 8, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (26, 26, '章鱼哥', 3, 3, 0, 8, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (27, 27, '小鱼鱼', 4, 2, 0, 9, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (28, 28, '小毛虫', 5, 3, 0, 9, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (29, 29, '甲虫', 2, 2, 0, 10, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (30, 30, '幽灵王', 6, 4, 0, 10, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (31, 31, '幽灵', 3, 3, 0, 1, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (32, 32, '干骨兵', 8, 5, 0, 2, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (33, 33, '碾压砖块', 4, 3, 0, 3, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (34, 34, '小碾压砖块', 5, 2, 0, 4, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (35, 35, '雷诺', 7, 4, 0, 5, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (36, 36, '魔法库巴', 4, 3, 0, 6, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (37, 37, '兔八哥', 6, 2, 0, 7, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (38, 38, '库巴小子', 5, 3, 0, 8, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (39, 39, '瓦里奥', 2, 2, 0, 9, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00'),
       (40, 40, '路易吉', 3, 3, 0, 10, '2024-07-31 09:00:31', '2024-07-31 09:00:31',
        '1000-01-01 00:00:00');
-- Dump completed on 2024-08-05  9:13:48
