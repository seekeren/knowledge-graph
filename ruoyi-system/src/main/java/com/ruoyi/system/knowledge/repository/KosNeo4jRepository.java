package com.ruoyi.system.knowledge.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.Values;
import org.neo4j.driver.exceptions.Neo4jException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.knowledge.domain.KosExtractedEntity;
import com.ruoyi.system.knowledge.domain.KosExtractedRelation;
import com.ruoyi.system.knowledge.domain.KosRelationRule;
import com.ruoyi.system.knowledge.domain.KosTerm;

/**
 * KOS Neo4j 数据访问
 */
@Component
public class KosNeo4jRepository
{
    @Autowired
    private Driver driver;

    private static final String DEFAULT_DB = "neo4j";

    public List<KosTerm> loadAllEnabledTerms()
    {
        String cypher = "MATCH (t:KosTerm) WHERE t.status = 'ENABLED' RETURN t.termId AS termId, t.name AS name, t.type AS type, t.aliases AS aliases, t.priority AS priority ORDER BY t.priority DESC, t.name";
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DB)))
        {
            Result result = session.run(cypher);
            List<KosTerm> terms = new ArrayList<>();
            while (result.hasNext())
            {
                Record r = result.next();
                KosTerm t = new KosTerm();
                t.setTermId(r.get("termId").asString(""));
                t.setName(r.get("name").asString(""));
                t.setType(r.get("type").asString(""));
                t.setAliases(r.get("aliases", Values.NULL).isNull() ? Collections.emptyList()
                        : r.get("aliases").asList(Value -> Value.asString("")));
                t.setStatus("ENABLED");
                t.setPriority(r.get("priority").asInt(0));
                terms.add(t);
            }
            return terms;
        }
        catch (Neo4jException e)
        {
            throw new ServiceException("加载KOS词条失败: " + e.getMessage());
        }
    }

    public List<KosRelationRule> loadAllEnabledRules()
    {
        String cypher = "MATCH (r:RelationRule) WHERE r.status = 'ENABLED' RETURN r.ruleId AS ruleId, r.name AS name, r.triggerPattern AS triggerPattern, r.relationType AS relationType, r.sourceTypes AS sourceTypes, r.targetTypes AS targetTypes, r.direction AS direction, r.maxDistance AS maxDistance, r.priority AS priority ORDER BY r.priority DESC, r.name";
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DB)))
        {
            Result result = session.run(cypher);
            List<KosRelationRule> rules = new ArrayList<>();
            while (result.hasNext())
            {
                Record r = result.next();
                KosRelationRule rule = new KosRelationRule();
                rule.setRuleId(r.get("ruleId").asString(""));
                rule.setName(r.get("name").asString(""));
                rule.setTriggerPattern(r.get("triggerPattern").asString(""));
                rule.setRelationType(r.get("relationType").asString(""));
                rule.setSourceTypes(r.get("sourceTypes", Values.NULL).isNull() ? Collections.emptyList()
                        : r.get("sourceTypes").asList(v -> v.asString("")));
                rule.setTargetTypes(r.get("targetTypes", Values.NULL).isNull() ? Collections.emptyList()
                        : r.get("targetTypes").asList(v -> v.asString("")));
                rule.setDirection(r.get("direction").asString("BOTH"));
                rule.setMaxDistance(r.get("maxDistance").asInt(120));
                rule.setStatus("ENABLED");
                rule.setPriority(r.get("priority").asInt(0));
                rules.add(rule);
            }
            return rules;
        }
        catch (Neo4jException e)
        {
            throw new ServiceException("加载KOS关系规则失败: " + e.getMessage());
        }
    }

    public void saveDocument(String docId, String title, String content, String contentHash, String createdBy)
    {
        String cypher = "CREATE (d:Document {docId: $docId, title: $title, content: $content, contentHash: $contentHash, createdBy: $createdBy, createdAt: datetime()})";
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DB)))
        {
            session.run(cypher, Values.parameters("docId", docId, "title", title, "content", content, "contentHash",
                    contentHash, "createdBy", createdBy));
        }
        catch (Neo4jException e)
        {
            throw new ServiceException("保存文档失败: " + e.getMessage());
        }
    }

    public void saveEntitiesAndRelations(String docId, List<KosExtractedEntity> entities,
            List<KosExtractedRelation> relations)
    {
        try (Session session = driver.session(SessionConfig.forDatabase(DEFAULT_DB)))
        {
            session.writeTransaction(tx ->
            {
                for (KosExtractedEntity e : entities)
                {
                    String cypher = "CREATE (ee:ExtractedEntity {entityId: $entityId, docId: $docId, name: $name, normalizedName: $normalizedName, type: $type, frequency: $frequency, score: $score, offsets: $offsets, termId: $termId})";
                    tx.run(cypher,
                            Values.parameters("entityId", e.getEntityId(), "docId", docId, "name", e.getNormalizedName(),
                                    "normalizedName", e.getNormalizedName(), "type", e.getType(), "frequency",
                                    e.getFrequency(), "score", e.getScore(), "offsets",
                                    e.getOffsets() == null ? new ArrayList<>() : e.getOffsets(), "termId",
                                    e.getTermId()));

                    tx.run("MATCH (d:Document {docId: $docId}), (ee:ExtractedEntity {entityId: $entityId}) CREATE (d)-[:CONTAINS]->(ee)",
                            Values.parameters("docId", docId, "entityId", e.getEntityId()));

                    tx.run("MATCH (ee:ExtractedEntity {entityId: $entityId}), (t:KosTerm {termId: $termId}) CREATE (ee)-[:MATCHED_TO]->(t)",
                            Values.parameters("entityId", e.getEntityId(), "termId", e.getTermId()));
                }

                Map<String, String> nameToId = new HashMap<>();
                for (KosExtractedEntity e : entities)
                {
                    nameToId.put(e.getNormalizedName(), e.getEntityId());
                }

                for (KosExtractedRelation r : relations)
                {
                    String sourceId = nameToId.get(r.getSourceName());
                    String targetId = nameToId.get(r.getTargetName());
                    if (sourceId == null || targetId == null)
                        continue;
                    String cypher = "MATCH (s:ExtractedEntity {entityId: $sourceId}), (t:ExtractedEntity {entityId: $targetId}) CREATE (s)-[r:EXTRACTED_RELATION {relationType: $relationType, method: $method, ruleId: $ruleId, score: $score, evidence: $evidence}]->(t)";
                    tx.run(cypher,
                            Values.parameters("sourceId", sourceId, "targetId", targetId, "relationType",
                                    r.getRelationType(), "method", r.getMethod(), "ruleId",
                                    r.getRuleId() == null ? "" : r.getRuleId(), "score", r.getScore(), "evidence",
                                    r.getEvidence() == null ? "" : r.getEvidence()));
                }
                return null;
            });
        }
        catch (Neo4jException e)
        {
            throw new ServiceException("保存实体和关系失败: " + e.getMessage());
        }
    }
}
