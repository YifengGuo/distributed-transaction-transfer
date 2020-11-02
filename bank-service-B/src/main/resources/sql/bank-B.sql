DROP TABLE IF EXISTS `account_bank_B`;
CREATE TABLE `account_bank_B`  (
  `id` varchar(32) NOT NULL,
  `accountId` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `balance` double(16, 2) NOT NULL DEFAULT 0.0,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;
INSERT INTO `account_bank_B` VALUES (1, 'B', 10000.0);