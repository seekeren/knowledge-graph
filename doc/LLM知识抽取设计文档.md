# LLM 知识抽取设计文档

## 一、功能概述

在「知识抽取与转化」菜单下新增「LLM知识抽取」功能，通过大语言模型实现文本知识抽取，支持：

- 抽取类型：实体 / 关系
- 语言类型：中文 / 英文
- **模型可扩展**：通义千问Max/Plus/Turbo（可自行添加其他模型）
- **提示词模板管理**：疾病/药物/症状/关系抽取模板
- **提示词预览**：选择提示词后可点击预览查看完整内容
- 抽取结果：JSON 结构化展示
- **阿里云Qwen真实对接**：兼容OpenAI协议格式

---

## 二、菜单与权限

### 2.1 菜单结构
```
知识抽取与转化 (2000)
├─ 基于KOS知识抽取 (2001)
└─ LLM知识抽取 (2010)
```

### 2.2 权限定义
| 权限标识               | 说明   |
|------------------------|--------|
| knowledge:llm:list     | 页面访问 |
| knowledge:llm:extract  | 执行抽取 |

SQL 文件：`sql/llm_extraction_menu.sql`

---

## 三、数据库设计

### 3.1 模型配置表 llm_model_config

| 字段          | 类型      | 说明                     |
|---------------|-----------|--------------------------|
| model_id      | BIGINT PK | 模型ID                   |
| model_name    | VARCHAR   | 模型名称（如 qwen-max） |
| display_name  | VARCHAR   | 显示名称                 |
| provider      | VARCHAR   | 服务商 aliyun/openai/custom |
| api_key       | VARCHAR   | API Key（加密存储建议） |
| endpoint      | VARCHAR   | API端点（兼容OpenAI协议）|
| temperature   | DECIMAL   | 温度参数                 |
| max_tokens    | INT       | 最大Token               |
| status        | CHAR(1)   | 0启用1停用               |
| order_num     | INT       | 排序                     |

### 3.2 提示词模板表 llm_prompt_template

| 字段          | 类型      | 说明                 |
|---------------|-----------|----------------------|
| prompt_id     | BIGINT PK | 模板ID               |
| prompt_name   | VARCHAR   | 模板名称             |
| prompt_type   | VARCHAR   | entity/relation/custom |
| content       | TEXT      | 提示词内容           |
| language      | VARCHAR   | zh/en/all            |
| status        | CHAR(1)   | 0启用1停用           |
| order_num     | INT       | 排序                 |

SQL 文件：`sql/llm_extension.sql`

---

## 四、后端 API

### 4.1 模型列表
```
GET /knowledge/llm/models
```
返回所有启用的模型，前端下拉选择。

### 4.2 提示词模板列表
```
GET /knowledge/llm/prompts
```
返回所有启用的提示词模板。

### 4.3 提示词详情（预览用）
```
GET /knowledge/llm/prompts/{promptId}
```
返回模板完整内容。

### 4.4 抽取接口
```
POST /knowledge/llm/extract
Content-Type: application/json
```

**请求体：**
```json
{
  "extractType": "entity",
  "language": "zh",
  "model": "qwen-max",
  "promptId": "1",
  "content": "待抽取文本..."
}
```

**响应体：**
```json
{
  "code": 200,
  "data": {
    "extractType": "entity",
    "model": "qwen-max",
    "promptId": "1",
    "promptName": "提示词1-疾病",
    "result": "{...}"
  }
}
```

### 4.5 代码位置
| 文件 | 说明 |
|------|------|
| LlmModelConfig.java | 模型配置DO |
| LlmPromptTemplate.java | 提示词DO |
| LlmModelConfigMapper.xml | MyBatis Mapper |
| LlmPromptTemplateMapper.xml | MyBatis Mapper |
| ILlmService.java / LlmServiceImpl.java | Qwen调用实现 |
| LlmExtractionController.java | Controller |
| llm.js | 前端API封装 |

---

## 五、前端页面

### 5.1 页面路径
```
ruoyi-ui/src/views/knowledge/llm/index.vue
```

### 5.2 功能点
- ✅ 模型列表动态加载（支持扩展）
- ✅ 提示词模板列表动态加载
- ✅ **提示词预览按钮**：点击弹窗显示完整内容
- ✅ 蓝色全屏抽取按钮
- ✅ JSON结果展示区

---

## 六、扩展说明

### 6.1 配置API Key
1. 执行 `sql/llm_extension.sql` 建表
2. 更新 `llm_model_config.api_key` 字段为你的阿里云DashScope API Key
3. 重启后端

### 6.2 添加新模型
在 `llm_model_config` 表 INSERT 一条记录即可，无需改代码：
```sql
INSERT INTO llm_model_config (model_name, display_name, provider, endpoint, api_key, temperature, max_tokens, status, order_num)
VALUES ('qwen-72b', '通义千问72B', 'aliyun', 'https://dashscope.aliyuncs.com/compatible-mode/v1', 'sk-xxx', 0.7, 4000, '0', 4);
```

### 6.3 添加新提示词模板
在 `llm_prompt_template` 表 INSERT 一条记录即可。

### 6.4 接入其他OpenAI兼容模型
目前实现仅调用 OpenAI 兼容的 `/chat/completions` 接口，任何兼容此格式的服务均可直接接入（OpenAI、Azure OpenAI、通义千问、百川、DeepSeek等）。

### 6.5 结果写入Neo4j
参考 KOS抽取 `KosNeo4jRepository`，在 `LlmServiceImpl.callLlm()` 返回后，解析 JSON 并入库即可。
