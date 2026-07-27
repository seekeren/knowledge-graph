package com.ruoyi.system.knowledge.service.impl;

import java.security.MessageDigest;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.knowledge.config.KosProperties;
import com.ruoyi.system.knowledge.domain.KosExtractedEntity;
import com.ruoyi.system.knowledge.domain.KosExtractedRelation;
import com.ruoyi.system.knowledge.domain.KosRelationRule;
import com.ruoyi.system.knowledge.domain.KosTerm;
import com.ruoyi.system.knowledge.dto.KosExtractRequest;
import com.ruoyi.system.knowledge.dto.KosExtractResponse;
import com.ruoyi.system.knowledge.dto.KosGraphNode;
import com.ruoyi.system.knowledge.dto.KosGraphLink;
import com.ruoyi.system.knowledge.engine.KosExtractionEngine;
import com.ruoyi.system.knowledge.repository.KosNeo4jRepository;
import com.ruoyi.system.knowledge.service.IKosExtractionService;

@Service
public class KosExtractionServiceImpl implements IKosExtractionService
{
    @Autowired
    private KosNeo4jRepository neo4jRepo;

    @Autowired
    private KosExtractionEngine engine;

    @Autowired
    private KosProperties properties;

    private List<KosTerm> cachedTerms = Collections.emptyList();
    private List<KosRelationRule> cachedRules = Collections.emptyList();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public KosExtractResponse extract(KosExtractRequest request)
    {
        if (engine.isCacheExpired())
        {
            refreshKosCache();
        }

        String content = request.getContent();
        Set<String> typeFilter = request.getEntityTypes() == null || request.getEntityTypes().isEmpty() ? null
                : new HashSet<>(request.getEntityTypes());

        List<KosExtractedEntity> entities = engine.extractEntities(content, request.getMinFrequency(), typeFilter);

        if (entities.size() > request.getMaxEntities())
        {
            entities = entities.subList(0, request.getMaxEntities());
        }

        List<KosExtractedRelation> relations = engine.extractRelations(content, entities,
                Boolean.TRUE.equals(request.getEnableRuleRelation()), Boolean.TRUE.equals(request.getEnableCooccurrence()),
                request.getCooccurrenceWindow(), request.getMinCooccurrence());

        String docId = UUID.randomUUID().toString();
        String contentHash = sha256(content);
        String currentUser = getCurrentUsername();

        neo4jRepo.saveDocument(docId,
                StringUtils.isNotEmpty(request.getTitle()) ? request.getTitle() : "未命名", content, contentHash,
                currentUser);
        neo4jRepo.saveEntitiesAndRelations(docId, entities, relations);

        KosExtractResponse response = buildResponse(docId, request.getTitle(), entities, relations);
        return response;
    }

    @Override
    public void refreshKosCache()
    {
        cachedTerms = neo4jRepo.loadAllEnabledTerms();
        cachedRules = neo4jRepo.loadAllEnabledRules();
        engine.refreshCache(cachedTerms, cachedRules);
    }

    private KosExtractResponse buildResponse(String docId, String title, List<KosExtractedEntity> entities,
            List<KosExtractedRelation> relations)
    {
        KosExtractResponse resp = new KosExtractResponse();
        resp.setDocId(docId);
        resp.setTitle(title != null ? title : "未命名");
        resp.setEntityCount(entities.size());
        resp.setRelationCount(relations.size());
        resp.setEntities(entities);
        resp.setRelations(relations);

        Map<String, KosGraphNode> nodeMap = new LinkedHashMap<>();
        for (KosExtractedEntity e : entities)
        {
            KosGraphNode node = new KosGraphNode(e.getNormalizedName(), e.getNormalizedName(), e.getType(),
                    e.getFrequency(), 10 + e.getFrequency() * 2);
            nodeMap.put(node.getId(), node);
        }

        List<KosGraphLink> links = new ArrayList<>();
        Set<String> evidenceSet = new LinkedHashSet<>();
        for (KosExtractedRelation r : relations)
        {
            String src = r.getSourceName();
            String tgt = r.getTargetName();
            if (nodeMap.containsKey(src) && nodeMap.containsKey(tgt))
            {
                KosGraphLink link = new KosGraphLink(src, tgt, r.getRelationType(), r.getMethod(), r.getScore());
                links.add(link);
                if (StringUtils.isNotEmpty(r.getEvidence()))
                {
                    evidenceSet.add(r.getEvidence());
                }
            }
        }

        resp.setNodes(new ArrayList<>(nodeMap.values()));
        resp.setLinks(links);
        resp.setEvidences(new ArrayList<>(evidenceSet));
        return resp;
    }

    private String sha256(String input)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash)
            {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    private String getCurrentUsername()
    {
        try
        {
            LoginUser user = SecurityUtils.getLoginUser();
            return user != null && user.getUsername() != null ? user.getUsername() : "system";
        }
        catch (Exception e)
        {
            return "system";
        }
    }

    @Override
    public List<KosTerm> listTerms(KosTerm query)
    {
        return new ArrayList<>(cachedTerms);
    }

    @Override
    public KosTerm getTerm(String termId)
    {
        return cachedTerms.stream().filter(t -> termId.equals(t.getTermId())).findFirst().orElse(null);
    }

    @Override
    public int insertTerm(KosTerm term)
    {
        return 1;
    }

    @Override
    public int updateTerm(KosTerm term)
    {
        return 1;
    }

    @Override
    public int deleteTerm(String termId)
    {
        return 1;
    }

    @Override
    public List<KosRelationRule> listRules(KosRelationRule query)
    {
        return new ArrayList<>(cachedRules);
    }

    @Override
    public KosRelationRule getRule(String ruleId)
    {
        return cachedRules.stream().filter(r -> ruleId.equals(r.getRuleId())).findFirst().orElse(null);
    }

    @Override
    public int insertRule(KosRelationRule rule)
    {
        return 1;
    }

    @Override
    public int updateRule(KosRelationRule rule)
    {
        return 1;
    }

    @Override
    public int deleteRule(String ruleId)
    {
        return 1;
    }
}
