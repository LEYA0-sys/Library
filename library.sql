/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : library

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 10/06/2025 21:04:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bs_book
-- ----------------------------
DROP TABLE IF EXISTS `bs_book`;
CREATE TABLE `bs_book`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书籍名称',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `book_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '书籍编码',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'available' COMMENT '状态：available-可借阅, borrowed-已借出',
  `borrow_count` int NULL DEFAULT 0 COMMENT '借阅次数',
  `user_id` bigint NULL DEFAULT NULL COMMENT '当前借阅用户ID',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图片URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '书籍表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bs_book
-- ----------------------------
INSERT INTO `bs_book` VALUES (1, '活着', '余华', 'BK00001', 'available', 1, NULL, '/api/upload/files/49c3bd1e-0705-4d42-849e-69e2a0ee392f.webp', '《活着》是作家余华的代表作之一，讲述了农村人福贵悲惨的人生遭遇。', '2025-06-06 20:50:49', '2025-06-09 19:52:23');
INSERT INTO `bs_book` VALUES (2, '百年孤独', '加西亚·马尔克斯', 'BK00002', 'available', 2, NULL, '/api/upload/files/bf078f30-9df6-4ca4-917d-a31359dbf855.webp', '《百年孤独》是哥伦比亚作家加西亚·马尔克斯的代表作，也是拉丁美洲魔幻现实主义文学的代表作。', '2025-06-06 20:50:49', '2025-06-09 19:52:37');
INSERT INTO `bs_book` VALUES (3, '三体', '刘慈欣', 'BK00003', 'available', 1, NULL, '/api/upload/files/c1ee9135-8121-4e11-9a07-fc6e0ba1d29b.webp', '《三体》是刘慈欣创作的系列长篇科幻小说，由《三体》、《三体Ⅱ·黑暗森林》、《三体Ⅲ·死神永生》组成。', '2025-06-06 20:50:49', '2025-06-09 19:52:45');
INSERT INTO `bs_book` VALUES (4, '人类简史', '尤瓦尔·赫拉利', 'BK00004', 'available', 1, NULL, '/api/upload/files/51e31880-2641-41da-9858-73d810a7082a.webp', '《人类简史：从动物到上帝》是以色列历史学家尤瓦尔·赫拉利创作的科普著作。', '2025-06-06 20:50:49', '2025-06-09 19:52:52');
INSERT INTO `bs_book` VALUES (5, '明朝那些事儿', '当年明月', 'BK00005', 'available', 2, NULL, '/api/upload/files/0f5b9d1f-605e-44f2-9560-335069b3e59f.webp', '《明朝那些事儿》是一部描写明朝历史的网络历史学著作。', '2025-06-06 20:50:49', '2025-06-09 19:52:59');
INSERT INTO `bs_book` VALUES (6, '红楼梦', '曹雪芹', 'BK00005', 'available', 1, NULL, '/api/upload/files/9cf71293-2cff-4924-b2da-071f0afae5f4.webp', '中国古典四大名著之一，描绘贾府的荣辱兴衰与宝黛爱情悲剧，展现封建社会的全景画卷。\n', '2025-06-09 19:16:50', '2025-06-09 19:53:06');

-- ----------------------------
-- Table structure for bs_book_type
-- ----------------------------
DROP TABLE IF EXISTS `bs_book_type`;
CREATE TABLE `bs_book_type`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型名称',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '书籍类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bs_book_type
-- ----------------------------
INSERT INTO `bs_book_type` VALUES (1, '文学', '2025-06-06 20:50:49');
INSERT INTO `bs_book_type` VALUES (2, '历史', '2025-06-06 20:50:49');
INSERT INTO `bs_book_type` VALUES (3, '科技', '2025-06-06 20:50:49');
INSERT INTO `bs_book_type` VALUES (4, '艺术', '2025-06-06 20:50:49');
INSERT INTO `bs_book_type` VALUES (5, '教育', '2025-06-06 20:50:49');
INSERT INTO `bs_book_type` VALUES (6, '生活', '2025-06-06 20:50:49');
INSERT INTO `bs_book_type` VALUES (7, '政治', '2025-06-09 18:51:03');

-- ----------------------------
-- Table structure for bs_book_type_relation
-- ----------------------------
DROP TABLE IF EXISTS `bs_book_type_relation`;
CREATE TABLE `bs_book_type_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` bigint NOT NULL COMMENT '书籍ID',
  `type_id` bigint NOT NULL COMMENT '类型ID',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE,
  INDEX `idx_type_id`(`type_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '书籍类型关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bs_book_type_relation
-- ----------------------------
INSERT INTO `bs_book_type_relation` VALUES (1, 1, 1, '2025-06-06 20:50:49');
INSERT INTO `bs_book_type_relation` VALUES (2, 2, 1, '2025-06-06 20:50:49');
INSERT INTO `bs_book_type_relation` VALUES (3, 3, 3, '2025-06-06 20:50:49');
INSERT INTO `bs_book_type_relation` VALUES (4, 4, 2, '2025-06-06 20:50:49');
INSERT INTO `bs_book_type_relation` VALUES (5, 4, 3, '2025-06-06 20:50:49');
INSERT INTO `bs_book_type_relation` VALUES (6, 5, 2, '2025-06-06 20:50:49');
INSERT INTO `bs_book_type_relation` VALUES (7, 6, 1, NULL);

-- ----------------------------
-- Table structure for bs_borrow
-- ----------------------------
DROP TABLE IF EXISTS `bs_borrow`;
CREATE TABLE `bs_borrow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `book_id` bigint NOT NULL COMMENT '书籍ID',
  `start_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '借阅开始时间',
  `end_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '实际归还时间',
  `expect_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预期归还时间',
  `borrow_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'borrowing' COMMENT '借阅状态：borrowing-借阅中, returned-已归还, overdue-超期',
  `has_delayed` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'no' COMMENT '是否延期：yes-是, no-否',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_book_id`(`book_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '借阅表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bs_borrow
-- ----------------------------
INSERT INTO `bs_borrow` VALUES (1, 2, 1, '2025-06-08 22:21:58', '2025-06-08 22:25:16', '2025-07-08 22:21:58', 'returned', '0', '2025-06-08 22:21:58', '2025-06-08 22:25:16');
INSERT INTO `bs_borrow` VALUES (2, 2, 1, '2025-06-08 22:22:15', '2025-06-08 22:28:21', '2025-07-08 22:22:15', 'returned', '0', '2025-06-08 22:22:15', '2025-06-08 22:28:21');
INSERT INTO `bs_borrow` VALUES (3, 2, 1, '2025-06-08 22:22:23', '2025-06-08 22:28:23', '2025-07-08 22:22:23', 'returned', '0', '2025-06-08 22:22:23', '2025-06-08 22:28:23');
INSERT INTO `bs_borrow` VALUES (4, 2, 1, '2025-06-08 22:24:59', '2025-06-08 22:28:25', '2025-07-08 22:24:59', 'returned', '0', '2025-06-08 22:24:59', '2025-06-08 22:28:25');
INSERT INTO `bs_borrow` VALUES (5, 2, 1, '2025-06-08 22:28:37', '2025-06-08 22:30:59', '2025-07-08 22:28:37', 'returned', '0', '2025-06-08 22:28:37', '2025-06-08 22:30:59');
INSERT INTO `bs_borrow` VALUES (6, 2, 1, '2025-06-08 22:33:07', '2025-06-08 22:33:27', '2025-07-15 22:33:07', 'returned', '1', '2025-06-08 22:33:07', '2025-06-08 22:33:27');
INSERT INTO `bs_borrow` VALUES (7, 2, 2, '2025-06-08 22:37:18', '2025-06-08 22:37:28', '2025-07-08 22:37:18', 'returned', '0', '2025-06-08 22:37:18', '2025-06-08 22:37:28');
INSERT INTO `bs_borrow` VALUES (8, 2, 2, '2025-06-08 22:37:32', '2025-06-08 22:37:44', '2025-07-08 22:37:32', 'returned', '0', '2025-06-08 22:37:32', '2025-06-08 22:37:44');
INSERT INTO `bs_borrow` VALUES (9, 2, 3, '2025-06-08 22:40:31', '2025-06-08 22:40:38', '2025-07-08 22:40:31', 'returned', '0', '2025-06-08 22:40:31', '2025-06-08 22:40:38');
INSERT INTO `bs_borrow` VALUES (10, 2, 5, '2025-06-08 22:41:02', '2025-06-08 22:41:10', '2025-07-08 22:41:02', 'returned', '0', '2025-06-08 22:41:02', '2025-06-08 22:41:10');
INSERT INTO `bs_borrow` VALUES (11, 2, 5, '2025-06-08 22:41:15', '2025-06-08 22:41:18', '2025-07-08 22:41:15', 'returned', '0', '2025-06-08 22:41:15', '2025-06-08 22:41:18');
INSERT INTO `bs_borrow` VALUES (12, 3, 3, '2025-06-08 23:39:21', '2025-06-08 23:39:30', '2025-07-08 23:39:21', 'returned', '0', '2025-06-08 23:39:21', '2025-06-08 23:39:30');
INSERT INTO `bs_borrow` VALUES (13, 3, 1, '2025-06-08 23:39:25', '2025-06-08 23:39:32', '2025-07-08 23:39:25', 'returned', '0', '2025-06-08 23:39:25', '2025-06-08 23:39:32');
INSERT INTO `bs_borrow` VALUES (14, 3, 2, '2025-06-08 23:56:37', '2025-06-08 23:56:59', '2025-07-08 23:56:37', 'returned', '0', '2025-06-08 23:56:37', '2025-06-08 23:56:59');
INSERT INTO `bs_borrow` VALUES (15, 3, 4, '2025-06-08 00:00:00', '2025-06-09 00:04:03', '2025-06-15 00:00:00', 'returned', '0', '2025-06-09 00:03:56', '2025-06-09 00:04:03');
INSERT INTO `bs_borrow` VALUES (16, 3, 2, '2025-06-09 00:00:00', '2025-06-09 19:29:23', '2025-06-16 00:00:00', 'returned', '0', '2025-06-09 18:43:35', '2025-06-09 19:29:23');
INSERT INTO `bs_borrow` VALUES (17, 5, 6, '2025-06-09 00:00:00', '2025-06-09 19:38:58', '2025-07-09 00:00:00', 'returned', '0', '2025-06-09 19:38:45', '2025-06-09 19:38:58');

-- ----------------------------
-- Table structure for bs_comment
-- ----------------------------
DROP TABLE IF EXISTS `bs_comment`;
CREATE TABLE `bs_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '留言内容',
  `comment_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '留言时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '留言表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bs_comment
-- ----------------------------
INSERT INTO `bs_comment` VALUES (2, 2, '17、18周汇报数据库大作业', '2025-06-08 23:38:11');
INSERT INTO `bs_comment` VALUES (3, 2, '6.16汇报机器学习大作业', '2025-06-08 23:38:26');
INSERT INTO `bs_comment` VALUES (4, 3, '我是用户user2', '2025-06-08 23:38:54');

-- ----------------------------
-- Table structure for bs_credit_record
-- ----------------------------
DROP TABLE IF EXISTS `bs_credit_record`;
CREATE TABLE `bs_credit_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `change_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变更时间',
  `credit_change` int NOT NULL COMMENT '信用积分变化',
  `reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '变化原因',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '信用记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bs_credit_record
-- ----------------------------
INSERT INTO `bs_credit_record` VALUES (1, 2, '2025-06-09 20:29:19', -10, '逾期未归还图书《三体》。', NULL);

-- ----------------------------
-- Table structure for bs_user
-- ----------------------------
DROP TABLE IF EXISTS `bs_user`;
CREATE TABLE `bs_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `reading_count` int NULL DEFAULT 0 COMMENT '在读书数',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '角色',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'active' COMMENT '状态',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新时间',
  `credit` int NULL DEFAULT 100 COMMENT '信用积分',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bs_user
-- ----------------------------
INSERT INTO `bs_user` VALUES (1, 'admin', '123456', '系统管理员', '13800000000', '123456789@qq.com', 0, 'admin', 'active', '2025-06-06 20:50:49', '2025-06-09 19:37:22', 100);
INSERT INTO `bs_user` VALUES (2, 'user1', '123456', '测试用户1', '13900000001', '123456789@qq.com', 11, 'user', 'active', '2025-06-06 20:50:49', '2025-06-09 18:49:33', 90);
INSERT INTO `bs_user` VALUES (3, 'user2', '123456', '测试用户2', '13900000002', '123456789@qq.com', 0, 'user', 'active', '2025-06-06 20:50:49', '2025-06-09 18:48:59', 102);
INSERT INTO `bs_user` VALUES (5, 'user3', '123456', NULL, NULL, '123456789@qq.com', 0, 'user', 'active', '2025-06-09 19:38:20', '2025-06-09 19:38:20', 102);

SET FOREIGN_KEY_CHECKS = 1;
