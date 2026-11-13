-- test 账号只读权限初始化脚本
-- 执行后，test 会绑定到普通用户 R_USER，仅拥有查看权限。

INSERT INTO `role` (`role_id`, `role_name`, `role_code`, `description`, `enabled`, `create_time`, `update_time`)
VALUES
(3, '普通用户', 'R_USER', '仅允许查看数据，不允许新增、编辑和删除', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  `role_name` = VALUES(`role_name`),
  `role_code` = VALUES(`role_code`),
  `description` = VALUES(`description`),
  `enabled` = VALUES(`enabled`),
  `update_time` = NOW();

UPDATE `user`
SET `role_id` = 3,
    `updated_at` = DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s')
WHERE `username` = 'test';
