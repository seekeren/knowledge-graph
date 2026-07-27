-- ========================================
-- LLM扩展：模型配置表 + 提示词模板表
-- ========================================

-- 1. LLM模型配置表
CREATE TABLE IF NOT EXISTS llm_model_config (
    model_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模型ID',
    model_name VARCHAR(100) NOT NULL COMMENT '模型名称（如qwen-max）',
    display_name VARCHAR(100) NOT NULL COMMENT '显示名称（如通义千问Max）',
    provider VARCHAR(50) DEFAULT 'aliyun' COMMENT '服务商: aliyun/openai/custom',
    api_key VARCHAR(500) NOT NULL COMMENT 'API Key',
    endpoint VARCHAR(200) COMMENT 'API端点',
    temperature DECIMAL(3,2) DEFAULT 0.7 COMMENT '温度参数',
    max_tokens INT DEFAULT 2000 COMMENT '最大Token',
    status CHAR(1) DEFAULT '0' COMMENT '状态: 0启用1停用',
    order_num INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_by VARCHAR(64) DEFAULT '',
    create_time DATETIME DEFAULT NOW(),
    update_by VARCHAR(64) DEFAULT '',
    update_time DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='LLM模型配置表';

-- 插入默认Qwen模型（API Key需用户自行填写）
INSERT INTO llm_model_config (model_name, display_name, provider, endpoint, temperature, max_tokens, status, order_num, remark)
VALUES
('qwen-max', '通义千问Max', 'aliyun', 'https://dashscope.aliyuncs.com/compatible-mode/v1', 0.7, 4000, '0', 1, '阿里云Qwen官方模型'),
('qwen-plus', '通义千问Plus', 'aliyun', 'https://dashscope.aliyuncs.com/compatible-mode/v1', 0.7, 2000, '0', 2, ''),
('qwen-turbo', '通义千问Turbo', 'aliyun', 'https://dashscope.aliyuncs.com/compatible-mode/v1', 0.7, 1000, '0', 3, '');

-- 2. 提示词模板表
CREATE TABLE IF NOT EXISTS llm_prompt_template (
    prompt_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    prompt_name VARCHAR(100) NOT NULL COMMENT '模板名称',
    prompt_type VARCHAR(50) DEFAULT 'entity' COMMENT '类型: entity/relation/custom',
    content TEXT NOT NULL COMMENT '提示词内容',
    language VARCHAR(20) DEFAULT 'zh' COMMENT '适用语言: zh/en/all',
    status CHAR(1) DEFAULT '0' COMMENT '状态: 0启用1停用',
    order_num INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_by VARCHAR(64) DEFAULT '',
    create_time DATETIME DEFAULT NOW(),
    update_by VARCHAR(64) DEFAULT '',
    update_time DATETIME DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='提示词模板表';

-- 插入默认提示词模板
INSERT INTO llm_prompt_template (prompt_name, prompt_type, content, language, status, order_num, remark)
VALUES
('提示词1-疾病', 'entity', '你是专业的医疗知识抽取专家。请从以下文本中抽取出疾病实体，以JSON格式返回，格式示例：{"疾病": [{"疾病名称": ["猪丹毒"], "疾病别名": [], "疾病英文名": []}]}。只返回JSON，不要其他解释。', 'zh', '0', 1, '医疗疾病实体抽取'),
('提示词2-药物', 'entity', '你是专业的医疗知识抽取专家。请从以下文本中抽取出药物实体，以JSON格式返回。只返回JSON，不要其他解释。', 'zh', '0', 2, '药物实体抽取'),
('提示词3-症状', 'entity', '你是专业的医疗知识抽取专家。请从以下文本中抽取出症状实体，以JSON格式返回。只返回JSON，不要其他解释。', 'zh', '0', 3, '症状实体抽取'),
('关系抽取通用', 'relation', '你是专业的知识抽取专家。请从以下文本中抽取出实体间的语义关系，以JSON格式返回，包含头实体、关系类型、尾实体。只返回JSON，不要其他解释。', 'zh', '0', 10, '通用关系抽取');
