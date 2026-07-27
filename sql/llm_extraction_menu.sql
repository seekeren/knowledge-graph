-- LLM 知识抽取菜单 SQL（固定 menu_id，幂等可重复执行）

-- 二级菜单：LLM知识抽取 (menu_id=2010)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2010, 'LLM知识抽取', 2000, 2, 'llm', 'knowledge/llm/index', '', 1, 0, 'C', '0', '0', 'knowledge:llm:list', 'skill', 'admin', NOW(), '', NULL, 'LLM知识抽取菜单'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2010);

-- 按钮权限 (menu_id=2011)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2011, 'LLM抽取', 2010, 1, '', '', '', 1, 0, 'F', '0', '0', 'knowledge:llm:extract', '#', 'admin', NOW(), '', NULL, ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2011);

-- 角色授权
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2010 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2010);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, 2011 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = 2011);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, 2010 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 2010);

INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, 2011 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_menu WHERE role_id = 2 AND menu_id = 2011);
