-- ==================================================
-- WDMP 工业互联网平台 数据库初始化脚本
-- ==================================================

CREATE DATABASE IF NOT EXISTS wdmp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE wdmp;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS sys_dept (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
    dept_name VARCHAR(64) NOT NULL COMMENT '部门名称',
    order_num INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0停用',
    deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    dept_id BIGINT COMMENT '部门ID',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(128) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name VARCHAR(64) COMMENT '真实姓名',
    phone VARCHAR(16) COMMENT '手机号',
    email VARCHAR(128) COMMENT '邮笱',
    avatar VARCHAR(256) COMMENT '头像URL',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT, role_name VARCHAR(64) NOT NULL, role_key VARCHAR(64) NOT NULL UNIQUE,
    order_num INT NOT NULL DEFAULT 0, status TINYINT NOT NULL DEFAULT 1, remark VARCHAR(256),
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT NOT NULL AUTO_INCREMENT, parent_id BIGINT NOT NULL DEFAULT 0, menu_name VARCHAR(64) NOT NULL,
    menu_type TINYINT NOT NULL, path VARCHAR(256), component VARCHAR(256), perms VARCHAR(128), icon VARCHAR(64),
    order_num INT NOT NULL DEFAULT 0, visible TINYINT NOT NULL DEFAULT 1, status TINYINT NOT NULL DEFAULT 1,
    deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

CREATE TABLE IF NOT EXISTS sys_config (
    id BIGINT NOT NULL AUTO_INCREMENT, config_name VARCHAR(128) NOT NULL, config_key VARCHAR(128) NOT NULL UNIQUE,
    config_value TEXT, config_type TINYINT NOT NULL DEFAULT 0, remark VARCHAR(256),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

CREATE TABLE IF NOT EXISTS sys_user_role (user_id BIGINT NOT NULL, role_id BIGINT NOT NULL, PRIMARY KEY (user_id, role_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
CREATE TABLE IF NOT EXISTS sys_role_menu (role_id BIGINT NOT NULL, menu_id BIGINT NOT NULL, PRIMARY KEY (role_id, menu_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS sys_file (
    id BIGINT NOT NULL AUTO_INCREMENT, file_name VARCHAR(256) NOT NULL, file_path VARCHAR(512) NOT NULL,
    file_size BIGINT, file_type VARCHAR(64), upload_user BIGINT, upload_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件记录表';

CREATE TABLE IF NOT EXISTS sys_report (
    id BIGINT NOT NULL AUTO_INCREMENT, report_name VARCHAR(128) NOT NULL, report_type TINYINT NOT NULL DEFAULT 1,
    data_sql TEXT, config_json LONGTEXT, remark VARCHAR(256), create_user BIGINT,
    status TINYINT NOT NULL DEFAULT 1, deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报表定义表';

CREATE TABLE IF NOT EXISTS sys_job (
    id BIGINT NOT NULL AUTO_INCREMENT, job_name VARCHAR(128) NOT NULL, job_group VARCHAR(64) NOT NULL DEFAULT 'DEFAULT',
    cron_expr VARCHAR(64) NOT NULL, bean_name VARCHAR(256) NOT NULL, method_name VARCHAR(64) NOT NULL,
    params VARCHAR(512), status TINYINT NOT NULL DEFAULT 1, remark VARCHAR(256), deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务表';

CREATE TABLE IF NOT EXISTS sys_job_log (
    id BIGINT NOT NULL AUTO_INCREMENT, job_id BIGINT NOT NULL, job_name VARCHAR(128),
    status TINYINT NOT NULL DEFAULT 1, message TEXT, start_time DATETIME, end_time DATETIME, cost_ms BIGINT,
    PRIMARY KEY (id), INDEX idx_job_id (job_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行日志表';

CREATE TABLE IF NOT EXISTS sys_push_msg (
    id BIGINT NOT NULL AUTO_INCREMENT, title VARCHAR(128) NOT NULL, content TEXT NOT NULL,
    msg_type TINYINT NOT NULL DEFAULT 1, target_type TINYINT NOT NULL DEFAULT 0, target_ids VARCHAR(512),
    send_user BIGINT, send_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT NOT NULL DEFAULT 1, deleted TINYINT NOT NULL DEFAULT 0, PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息推送表';

CREATE TABLE IF NOT EXISTS sys_org (
    id BIGINT NOT NULL AUTO_INCREMENT, parent_id BIGINT NOT NULL DEFAULT 0, org_name VARCHAR(128) NOT NULL,
    org_code VARCHAR(64) UNIQUE, org_type TINYINT NOT NULL DEFAULT 1, order_num INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1, remark VARCHAR(256), deleted TINYINT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='组织机构表';

-- 初始数据
INSERT IGNORE INTO sys_dept (id, parent_id, dept_name, order_num) VALUES (1, 0, '总公司', 0),(2, 1, '研发部', 1),(3, 1, '运营部', 2);
INSERT IGNORE INTO sys_role (id, role_name, role_key, order_num) VALUES (1, '超级管理员', 'admin', 0),(2, '普通用户', 'normal', 1);
-- 默认管理员密码：Admin@123
INSERT IGNORE INTO sys_user (id, dept_id, username, password, real_name, status) VALUES
(1, 1, 'admin', '$2a$10$nyZkxb8VPOz9o9UNKqFkhuzTiUIqk9CvVNK0M7tPx0bPSE8STqOoC', '系统管理员', 1);
INSERT IGNORE INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT IGNORE INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, order_num) VALUES
(1, 0, '系统管理', 1, '/system', NULL, NULL, 'Setting', 1),
(2, 1, '用户管理', 2, '/system/user', 'system/user/index', 'system:user:list', 'User', 1),
(3, 1, '角色管理', 2, '/system/role', 'system/role/index', 'system:role:list', 'Avatar', 2),
(4, 1, '菜单管理', 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'Menu', 3),
(5, 1, '部门管理', 2, '/system/dept', 'system/dept/index', 'system:dept:list', 'OfficeBuilding', 4),
(6, 1, '系统配置', 2, '/system/config', 'system/config/index', 'system:config:list', 'Tools', 5),
(7, 0, '文件管理', 2, '/file', 'file/index', 'file:list', 'FolderOpened', 2),
(8, 0, '报表管理', 1, '/report', NULL, NULL, 'DataAnalysis', 3),
(9, 8, '报表设计', 2, '/report/designer', 'report/designer/index', 'report:designer', 'EditPen', 1),
(10, 8, '报表查看', 2, '/report/viewer', 'report/viewer/index', 'report:viewer', 'View', 2),
(11, 0, '任务调度', 2, '/task', 'task/index', 'task:list', 'Clock', 4),
(12, 0, '消息推送', 2, '/push', 'push/index', 'push:list', 'Bell', 5),
(13, 0, '组织管理', 2, '/org', 'org/index', 'org:list', 'Connection', 6);
INSERT IGNORE INTO sys_role_menu (role_id, menu_id) SELECT 1, id FROM sys_menu;
INSERT IGNORE INTO sys_config (config_name, config_key, config_value, config_type) VALUES
('系统名称', 'sys.name', '工业互联网平台', 0),
('会话超时', 'sys.session.timeout', '480', 0),
('文件存储路径', 'sys.file.path', './uploads', 0);
