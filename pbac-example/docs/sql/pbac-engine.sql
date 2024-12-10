DROP DATABASE IF EXISTS `pbac_engine`;
CREATE DATABASE `pbac_engine` charset utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pbac_engine;
SET NAMES utf8mb4;
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account`
(
    `id`           bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`     varchar(32)  DEFAULT NULL COMMENT '用户名',
    `password`     varchar(256) DEFAULT NULL COMMENT '密码',
    `create_time`  datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`  datetime     DEFAULT NULL COMMENT '更新时间',
    `deleted_time` datetime     DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `account_pk` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成交单';


INSERT INTO `account`
VALUES (1, 'mario', 'c674615a549e1bdfb5452a43c0a5fc78fbebac5c115a15c1d386182a25563a3f', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (2, 'luigi', 'ebe673779778bc185e89c4e8f8a0371e4d1e1a30a5dfc152ec47d8a9692ef183', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (3, 'peach', 'db9e56de9f69cee63f737caba20c2ac6cc49757c67d553023a10bfb193a07465', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (4, 'yoshi2', 'b90a80afe74447454baa6515ebd09a8354b6dcfe43d5c9803db32926775108a5', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (5, 'bowser', '044b61c3cd52c939cf557a2f3febcdb8622aacd6047d188dec9f515e7ba0431c', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (6, 'donkeykong', 'a9bbedc02e0ab7284d659387b288181fefc625aa2177db563157ddbebcad65fe', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (7, 'diddykong', '0ec15e453b8d1510ab675f2566661422f3457e4d9f07ccca3f0b818df438aa64', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (8, 'toad', '6dc0e69e94ec255ce0e90cc604eb41facb17e26715c279d833327c45c8250b46', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (9, 'toadette', '5afe13e3e39ac189ce113ba5ed75c9d81a84888a788f34e3ac58c97ee8151dfc', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (10, 'wario', 'd2beefb950b350d546e3be89721d8b343feb27d817aade8486c60c6b7355f899', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (11, 'waluigi', '763be547e52077ffb7f87b31381b11980598c297bf413e7251471feb1896fefc', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (12, 'rosalina', 'dd4485f573605f5e6018719e22810dcb5a4f6939f89606ff852b0a9b99d8a0b9', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (13, 'daisy', 'c51f51a4ad6d48b4ded4d7dab89c5f71f5029d3c56d57c462ead5920b8b70f5f', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (14, 'birdo', '3ddb278743d895700cfd2ecc154cd43be1c61e11318824f8c394b3dfb4f0c7dc', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (15, 'koopa', '8487d47b1ae34a3f0714206c36e8433a34e6e1984940bb1e6d338dbece44ddf5', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (16, 'kamek', 'efb92651c8ddac1ad832fed9cb42814baeef64dd4c5ee468cee2c369e1882d26', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (17, 'shyguys', '865c51707bd26b138aae42f15c5088f7944fa56d55c71770c55ddaa59e0a22cb', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (18, 'goomba', '68a8ed11db7234e320c6ac6e0050d55083cf7c49635e2c99368f6eace288bca9', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (19, 'lakitu', '7f37e4e516009d1b1fe509fdcb9754e9bc5140308a6ccb779af115f419a31edc', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (20, 'monty', '161d6de681e6f57d187ff9390b1f89e3de29d959d1b17d0ef26e96859b2f6b54', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (21, 'hammerbro', '2ec4f66662ba814db52cc2618c95ad08e1bc2f414ef20ad62d4c7c46214652f9', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (22, 'boomerangbro', '435c7c048c659b8dd0dc8f1c9816e500876264d2f41ce2c4e129c2cf4c4c61e8', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (23, 'firebro', 'e05c4e6f6822d2ee2bb0759a6d22280bb595855d7e2e477e947e027dd1f0384f', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (24, 'chainchomp', '26620bc222b5c9b46a7b0835610e817d68e9841cb048c9ec9bc873c6228a6a90', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (25, 'piranha', '35c9c6ac306b95e2ddbd231eb199907c7545ec2c8f154ae33375f331a1137892', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (26, 'buzzybeetle', '9067f2d89ba7d61872543db092e705d343d84c2d858d2ea2a3d4f8d9bd996338', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (27, 'spiny', '97f8c6889fb8b2c81bd2131eaeb625fe8578582bacd5f2cf7fc91618074eb498', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (28, 'blooper', '96a8da45b7571b0ce865128c5f2885ea6d380f400cd63b30ebdfaa3227da6ae1', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (29, 'cheepcheep', '212268e82c76e105e6d3c354365108715773e6cad8f77b1d9ba974110796586e', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (30, 'wiggler', '4ff2c6fded2d193607e0c642c282b618d8574d7e15bb2e98f33680311be25550', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (31, 'buzzy', '1cfe357340e829ee5a6303206f1f8295fe68a6afde1dce52e3eaeb2dc178a3ef', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (32, 'kingboo', '780d7eccca822a282475840bcd69d3bd097bd8e1c3a867c4fc90f5816f2643ff', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (33, 'boo', '3101a0f6ff7d6f660690a42c623b0c7561bc8d0d159a3eba64278629bada9f0d', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (34, 'drybones', 'cdc1053908c6446f48be0a5b5267911c55570b06a6d6aecad623b4cd05501a6f', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (35, 'thwomp', 'ebf19973f306abe17f585169bcd6c53f603f62a786302e3274b2cfa8dc587d11', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (36, 'thwimps', '06a3c99d3c95e85322e05a91a108ae7ba0f92930b20ae34bed8a0a36846b91b6', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (37, 'reznor', '243d1cf0816cd4300fb82d7750b0ef3f728a736071ac2a7f236eae41dd11351d', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (38, 'magikoopa', 'be2cbd923cdf7b937579e8ffc3b9246c5b68068cb6664f8ac7d012baa3e7ef3d', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (39, 'nabbitt', '243235c9d12ccdce2d147a8440c93132ef55dc35fad90c49eea6fd69676987d4', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (40, 'koopakid', '4159369a5d37b98b2a029102e884fd63f0f974fc9bcad9f62f3628e756d95e33', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (41, 'bowserjr', 'ecf469dc26e160d92b9f05006ba31a1b9b33071c0a0d408d0c9c657bc245495b', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (42, 'wendy', 'be0cbeff6dc6bae76c6fc95b1c6d847c51b1af8eb669f91a6023ed43180398f7', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (43, 'lemmy', 'e847e7dc863b28c27af2a22d10c7dd39e4798cbe241b1af13730d68ce31d130f', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (44, 'larry', '3df9c4e1b6f76746d69aa95f9b89c49b15ac05cf588e239c6d1f49806d477046', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (45, 'ludwig', 'ae4eceb885e1c3d650e4475bd84544a4107c0f0b693cfa79d37fb1c411598362', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (46, 'morton', '59d0c16a2b91b9e4e80d11aa002699c10991b90cf0f6e98366d24b0abc602337', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (47, 'roy', '405dd0f1ad181ef6af6a3f73dd9ed9521cd103a7218be9af1bf9385d5f8ad013', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (48, 'iggy', '4da0109b0695e16baa1bfab02df2eabd07920c1034bb0b32b70e69333c55c4a9', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (49, 'link', '6895d7ea678f106953befb3e2d7612102171f77c556aed39a5e16c91e0b5df91', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (50, 'zelda', '9ce93d5ce48615d96ae810126991603e87bebc272768e26f54276766fef5f799', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (51, 'ganondorf', 'c05b8bb13acd36e772e9543961fbe45e0d8396aa7b2b4317f1aed26d39c28bfc', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (52, 'sheik', 'd0853bca3258c6b32860a073fbcbb6ab7823b1c2611500bee7e95dc80a94106c', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (53, 'samus', '506aad9ec5f2f9f1fa2ee3c4f2b6b80af9d68fb5d591c56cc3e341edbd028e3a', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (54, 'kirby', 'bfa45746bbc05b4e69147e669d5a49479bd4a28a3d3286e3a0ad331c9e6cefff', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (55, 'metaknight', '21acdda2ad8205007c003e0a27042c964d0ed419597d5c1ea042137cab48a070', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (56, 'dedede', '1bb1c6749fef1619e8a75bd68ecf65edb95d29d8e1826fc43bc5bb1b73231955', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (57, 'fox', 'ac1b08d24f2c30d01e13775094c09efa11c37c4dd94cfdab10a65bdb3e705652', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (58, 'falco', '2628782c61ae793f371f17703758a43239bdf92c7f3a25698219b736abc4fa9f', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (59, 'wolf', '17c84b22f46b37b626a150f31043b7c3b742af86a7383f8d234bbdf768a84040', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (60, 'ness', '772da81a1cd61a89d875c48782972f698affaad8b836ac8730752bb53d2b42da', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (61, 'lucas', '21b6136ca0bb28114540ea7c516fb94421f97ebd792225b13a4f503fa84407cf', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (62, 'captainfalcon', '466821b39af58c3f45830b42bc19a33d7fc7b190daab3b3be787c4c631e1e2e3', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (63, 'jigglypuff', 'd8d8cebe5aaf6303bf0612ee29a11d0b307dee4ce7340e5cf97c3c973ff14cf2', '2024-07-31 08:25:09',
        NULL, '1000-01-01 00:00:00'),
       (64, 'marth', 'a29df82cae000e74734e2611c6cd6d84a1ef819910450f124f28952530f44aa8', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (65, 'roy2', 'd83be919ad53b4408933aff77f39d0662a60ba805c98c6f9fbd114c746ece95e', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (66, 'ike', '4c34d5e3a04dc8d43657ec2c040e184fd8bce7335c74b92b0b7b3ed5613cfe46', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (67, 'robin', 'de61f357eb6881c9a42907cf23c1f9d67004e74b59f18dec6e0f3d8a946d92e9', '2024-07-31 08:25:09', NULL,
        '1000-01-01 00:00:00'),
       (101, 'zelda2', 'd5bee8e68b10e8656a36b8cd53e4462068d972e91fec652b3d21d8372b36657f', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00'),
       (102, 'link2', 'ffc4db39d700562991dfc4910a142f4690d6921ee79633d13ae1ccf2d187856b', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00'),
       (103, 'samus2', 'b7f6bfd62d743b18cab4e6c42d648abd8c2408f8646406d377ef8bc6f8c4bdbb', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00'),
       (104, 'kirby2', '0bda56d0a8f4b417b8101d7b0c2c7d33bf38c74c57e3a17a4fb394bf8abdd0fd', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00'),
       (105, 'fox2', 'd661d8af78578c7d23d1d21b701d5de5738608c78d7d2ca18160b763f4fdfff5', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00'),
       (106, 'donkeykong2', 'e453200a2dd7754240b7f30b7de0d18874f5072fbd17153b9f5e0b07067c6a78', '2024-08-01 01:20:59',
        NULL, '1000-01-01 00:00:00'),
       (107, 'diddy', 'ea87ae857df161d21b9a44e0c35c1b484b2f8c84332ccf03b6080c542dc48e5f', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00'),
       (108, 'peach2', 'eaf5c70f4b4c04ce4e73b4ae0e2b97e76db58cb63e0fe31822505de75dedd757', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00'),
       (109, 'daisy2', '4fa8091c680674497d3e7198b5a96d0348c18878816ad05bc534d63c55cad96f', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00'),
       (110, 'yoshi', 'ce5a9f7260a17f1b5f3292a988980edfaad4bb529a0345fe538fa22b936791f1', '2024-08-01 01:20:59', NULL,
        '1000-01-01 00:00:00');


DROP TABLE IF EXISTS `account_role`;
CREATE TABLE `account_role`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `account_id`  bigint      DEFAULT NULL COMMENT '账号id',
    `role_id`     bigint      DEFAULT NULL COMMENT '角色ID',
    `role_code`   varchar(10) DEFAULT NULL COMMENT '角色编码不可修改，冗余',
    `create_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime    DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房管局审核员';


INSERT INTO `account_role`
VALUES (1, 1, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (2, 2, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (3, 3, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (4, 4, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (5, 5, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (6, 6, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (7, 7, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (8, 8, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (9, 9, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05'),
       (10, 10, 1, 'customer', '2024-08-04 22:38:03', '2024-08-04 22:38:05');


DROP TABLE IF EXISTS `area`;
CREATE TABLE `area`
(
    `id`           bigint   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         varchar(30)       DEFAULT NULL COMMENT '地区名称',
    `parent_id`    bigint   NOT NULL DEFAULT '-1' COMMENT '上级地区',
    `create_time`  datetime NOT NULL COMMENT '创建时间',
    `update_time`  datetime NOT NULL COMMENT '更新时间',
    `deleted_time` datetime NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地区';



DROP TABLE IF EXISTS `policy`;
CREATE TABLE `policy`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         varchar(50)  NOT NULL COMMENT '策略名称',
    `detail`       varchar(500) NOT NULL DEFAULT '' COMMENT '策略说明',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `update_time`  datetime     NOT NULL COMMENT '更新时间',
    `deleted_time` datetime     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='策略表,规定了configuration的内容';


INSERT INTO `policy`
VALUES (1, '购房相关策略', '允许三种用户查看', '2024-09-04 16:52:38', '2024-09-04 16:52:39', '1000-01-01 00:00:00');


DROP TABLE IF EXISTS `policy_rule`;
CREATE TABLE `policy_rule`
(
    `id`             bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `rule_id`        bigint       NOT NULL COMMENT '规则ID',
    `policy_id`      bigint       NOT NULL COMMENT '策略ID',
    `rule_level`     tinyint      NOT NULL DEFAULT '1' COMMENT '维度',
    `condition_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '组合条件 1.必要,2充分',
    `pirority`       int          NOT NULL DEFAULT '1' COMMENT '规则优先级',
    `param_value`    varchar(100) NOT NULL DEFAULT '' COMMENT '参数描述,方便绑定策略时填写',
    `create_time`    datetime     NOT NULL COMMENT '创建时间',
    `update_time`    datetime     NOT NULL COMMENT '更新时间',
    `deleted_time`   datetime     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='规则组合表';


INSERT INTO `policy_rule`
VALUES (1, 1, 1, 1, 2, 10, '', '2024-09-05 09:31:38', '2024-09-05 09:31:40', '1000-01-01 00:00:00'),
       (2, 2, 1, 1, 1, 4, '{\"startTime\":\"09:00:00\",\"endTime\":\"18:00:00\"}', '2024-09-05 09:31:45',
        '2024-09-05 09:31:47', '1000-01-01 00:00:00'),
       (3, 3, 1, 1, 1, 5, '\"CUSTOMER\"', '2024-09-05 09:34:25', '2024-09-05 09:34:23', '1000-01-01 00:00:00');


DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `policy_id`    bigint       NOT NULL COMMENT '策略ID',
    `name`         varchar(50)  NOT NULL COMMENT '资源名称',
    `pattern`      varchar(200) NOT NULL COMMENT '匹配规则,可以是精准的或者模糊的,policy 区分,因为精准的更快匹配',
    `match_type`   tinyint      NOT NULL DEFAULT '0' COMMENT '1 uri 精确匹配,2 uri 模糊匹配,3 table 匹配',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `update_time`  datetime     NOT NULL COMMENT '更新时间',
    `deleted_time` datetime              DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='请求客户端资源';


INSERT INTO `resource`
VALUES (2, 1, '请求购房记录', '/bill/page', 1, '2024-09-04 16:51:21', '2024-09-04 16:51:23', '1000-01-01 00:00:00');


DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`
(
    `id`           bigint      DEFAULT NULL,
    `name`         varchar(50) DEFAULT NULL,
    `code`         varchar(10) DEFAULT NULL,
    `create_time`  datetime    DEFAULT NULL,
    `update_time`  datetime    DEFAULT NULL,
    `deleted_time` datetime    DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO `role`
VALUES (1, '房产客户', 'customer', '2024-08-04 22:26:13', '2024-08-04 22:26:15', '1000-01-01 00:00:00');


DROP TABLE IF EXISTS `role_resource`;
CREATE TABLE `role_resource`
(
    `id`          bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id`     bigint      DEFAULT NULL COMMENT '角色ID',
    `resource_id` bigint      DEFAULT NULL COMMENT '资源id',
    `role_code`   varchar(10) DEFAULT NULL COMMENT '角色编码不可修改，冗余',
    `create_time` datetime    DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime    DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色资源表';


INSERT INTO `role_resource`
VALUES (1, 1, 2, 'customer', '2024-09-06 09:33:34', '2024-09-06 09:33:37');


DROP TABLE IF EXISTS `rule`;
CREATE TABLE `rule`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`         varchar(20)  NOT NULL COMMENT '规则定义名称',
    `level`        tinyint      NOT NULL DEFAULT '1' COMMENT '维度',
    `detail`       varchar(100)  NOT NULL DEFAULT '' COMMENT '规则说明',
    `handler_name` varchar(60)  NOT NULL DEFAULT '' COMMENT '指定执行器，与script二选一,handlerName优先',
    `scripts`      varchar(500) NOT NULL DEFAULT '' COMMENT '执行脚本,与handler_name 二选一,handlerName优先',
    `dynamic`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否动态',
    `param_desc`   text COMMENT '参数描述,提供前端渲染即用户填写,json格式案例[{"name":"名称","value":"值","desc":"描述"}]',
    `create_time`  datetime     NOT NULL COMMENT '创建时间',
    `update_time`  datetime     NOT NULL COMMENT '更新时间',
    `deleted_time` datetime     NOT NULL DEFAULT '1000-01-01 00:00:00' COMMENT '删除时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='鉴权规则表';


INSERT INTO `rule`
VALUES (1, '角色规则', 1, '根据用户拥有的角色判断', 'gatewayRoleRule', '', 0, '{}', '2024-09-04 16:54:28',
        '2024-09-04 16:54:30', '1000-01-01 00:00:00'),
       (2, '时间起止规则', 2, '根据一天中的开始时间和结束时间决定,一定要甜开始时间和结束时间段', 'dailyStartEndRule',
        '', 1, '{\"startTime\":\"开始时间(HH:mm:ss)\",\"endTime\":\"结束时间(HH:mm:ss)\"}', '2024-09-05 09:30:04',
        '2024-09-05 09:30:06', '1000-01-01 00:00:00'),
       (3, '身份规则', 1, '根据PbacUser的Identity信息匹配', 'identityRule', '', 2, '{\"identity\":[\"\"]}',
        '2024-09-05 09:38:46', '2024-09-05 09:38:49', '1000-01-01 00:00:00');