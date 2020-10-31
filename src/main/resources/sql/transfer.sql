DROP TABLE IF EXISTS `transfer`;
CREATE TABLE `transfer`  (
  `id` varchar(32) NOT NULL,
  `accountId` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `balance` double(16, 2) NOT NULL DEFAULT 0.0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;
INSERT INTO `transfer` VALUES (1, 'A', 10000.0);
INSERT INTO `transfer` VALUES (2, 'B', 10000.0);