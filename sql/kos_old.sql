-- KOS 知识抽取菜单 SQL

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('知识抽取与转化', SELECT MAX(menu_id) + 1 FROM (SELECT menu_id FROM sys_menu) AS tmp, 100, 'knowledge', NULL, 1, 0, 'M', '0', '0', '', 'tree-table', 'admin', NOW(), '', NULL, '知识抽取与转化目录');

SET @parent_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('基于KOS知识抽取', @parent_menu_id, 1, 'kos', 'knowledge/kos/index', 1, 0, 'C', '0', '0', 'knowledge:kos:list', 'chart', 'admin', NOW(), '', NULL, '基于KOS知识抽取菜单');

SET @kos_menu_id = LAST_INSERT_ID();

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
('KOS抽取', @kos_menu_id, 1, '', '', 1, 0, 'F', '0', '0', 'knowledge:kos:extract', '#', 'admin', NOW(), '', NULL, ''),
('KOS查询', @kos_menu_id, 2, '', '', 1, 0, 'F', '0', '0', 'knowledge:kos:query', '#', 'admin', NOW(), '', NULL, ''),
('KOS新增', @kos_menu_id, 3, '', '', 1, 0, 'F', '0', '0', 'knowledge:kos:add', '#', 'admin', NOW(), '', NULL, ''),
('KOS修改', @kos_menu_id, 4, '', '', 1, 0, 'F', '0', '0', 'knowledge:kos:edit', '#', 'admin', NOW(), '', NULL, ''),
('KOS删除', @kos_menu_id, 5, '', '', 1, 0, 'F', '0', '0', 'knowledge:kos:remove', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_name IN ('知识抽取与转化', '基于KOS知识抽取', 'KOS抽取', 'KOS查询', 'KOS新增', 'KOS修改', 'KOS删除');

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, menu_id FROM sys_menu WHERE menu_name IN ('知识抽取与转化', '基于KOS知识抽取', 'KOS抽取', 'KOS查询');
