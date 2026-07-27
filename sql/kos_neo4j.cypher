-- Neo4j KOS 图模型初始化脚本

-- 创建唯一约束
CREATE CONSTRAINT kos_term_id IF NOT EXISTS FOR (t:KosTerm) REQUIRE t.termId IS UNIQUE;
CREATE CONSTRAINT relation_rule_id IF NOT EXISTS FOR (r:RelationRule) REQUIRE r.ruleId IS UNIQUE;
CREATE CONSTRAINT document_id IF NOT EXISTS FOR (d:Document) REQUIRE d.docId IS UNIQUE;
CREATE CONSTRAINT extracted_entity_id IF NOT EXISTS FOR (e:ExtractedEntity) REQUIRE e.entityId IS UNIQUE;

-- 创建索引
CREATE INDEX doc_content_hash IF NOT EXISTS FOR (d:Document) ON (d.contentHash);
CREATE INDEX entity_doc_id IF NOT EXISTS FOR (e:ExtractedEntity) ON (e.docId);
CREATE INDEX entity_term_id IF NOT EXISTS FOR (e:ExtractedEntity) ON (e.termId);

-- 清理旧数据（可选）
MATCH (d:Document) DETACH DELETE d;
MATCH (t:KosTerm) DELETE t;
MATCH (r:RelationRule) DELETE r;

-- 插入农业领域示例词条
CREATE (t1:KosTerm {termId: 'T001', name: '水稻', type: '作物', aliases: ['稻谷', '禾苗'], status: 'ENABLED', priority: 10});
CREATE (t2:KosTerm {termId: 'T002', name: '玉米', type: '作物', aliases: ['包谷', '玉蜀黍'], status: 'ENABLED', priority: 10});
CREATE (t3:KosTerm {termId: 'T003', name: '小麦', type: '作物', aliases: [], status: 'ENABLED', priority: 10});
CREATE (t4:KosTerm {termId: 'T004', name: '长江流域', type: '地区', aliases: ['长江'], status: 'ENABLED', priority: 5});
CREATE (t5:KosTerm {termId: 'T005', name: '江苏', type: '地区', aliases: ['苏'], status: 'ENABLED', priority: 5});
CREATE (t6:KosTerm {termId: 'T006', name: '浙江', type: '地区', aliases: ['浙'], status: 'ENABLED', priority: 5});
CREATE (t7:KosTerm {termId: 'T007', name: '东北平原', type: '地区', aliases: ['东北'], status: 'ENABLED', priority: 5});
CREATE (t8:KosTerm {termId: 'T008', name: '华北平原', type: '地区', aliases: ['华北'], status: 'ENABLED', priority: 5});
CREATE (t9:KosTerm {termId: 'T009', name: '袁隆平', type: '人物', aliases: [], status: 'ENABLED', priority: 8});
CREATE (t10:KosTerm {termId: 'T010', name: '杂交水稻', type: '技术', aliases: ['杂交稻'], status: 'ENABLED', priority: 9});

-- 插入关系规则
CREATE (r1:RelationRule {
  ruleId: 'R001',
  name: '种植于',
  triggerPattern: '种植|种植于|分布于|生长于',
  relationType: '种植于',
  sourceTypes: ['作物'],
  targetTypes: ['地区'],
  direction: 'FORWARD',
  maxDistance: 30,
  status: 'ENABLED',
  priority: 10
});

CREATE (r2:RelationRule {
  ruleId: 'R002',
  name: '属于',
  triggerPattern: '属于|隶属于',
  relationType: '属于',
  sourceTypes: ['地区'],
  targetTypes: ['地区'],
  direction: 'FORWARD',
  maxDistance: 20,
  status: 'ENABLED',
  priority: 8
});

CREATE (r3:RelationRule {
  ruleId: 'R003',
  name: '研究贡献',
  triggerPattern: '研究|贡献|推广|发明',
  relationType: '研究贡献',
  sourceTypes: ['人物'],
  targetTypes: ['技术', '作物'],
  direction: 'FORWARD',
  maxDistance: 25,
  status: 'ENABLED',
  priority: 9
});

CREATE (r4:RelationRule {
  ruleId: 'R004',
  name: '包括',
  triggerPattern: '包括|包含|涵盖',
  relationType: '包括',
  sourceTypes: ['地区'],
  targetTypes: ['地区'],
  direction: 'FORWARD',
  maxDistance: 20,
  status: 'ENABLED',
  priority: 7
});

CREATE (r5:RelationRule {
  ruleId: 'R005',
  name: '位于',
  triggerPattern: '位于|坐落|地处',
  relationType: '位于',
  sourceTypes: ['地区'],
  targetTypes: ['地区'],
  direction: 'FORWARD',
  maxDistance: 20,
  status: 'ENABLED',
  priority: 7
});
