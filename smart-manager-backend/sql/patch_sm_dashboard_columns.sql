-- 旧库 sm_dashboard 缺列导致 Unknown column 'theme_id'。可重复执行（已存在列会报错可忽略）。
USE smart_manager_db;
ALTER TABLE sm_dashboard ADD COLUMN theme_id BIGINT NULL COMMENT '指标主题分类ID';
ALTER TABLE sm_dashboard ADD COLUMN publish_type VARCHAR(32) NULL COMMENT '发布对象类型';
ALTER TABLE sm_dashboard ADD COLUMN publish_target VARCHAR(500) NULL COMMENT '发布对象ID';
ALTER TABLE sm_dashboard ADD COLUMN url VARCHAR(1000) NULL COMMENT '看板地址';
ALTER TABLE sm_dashboard ADD COLUMN publish_by VARCHAR(64) NULL COMMENT '发布人';
ALTER TABLE sm_dashboard ADD COLUMN publish_time DATETIME NULL COMMENT '发布时间';
