package com.ruoyi.system.knowledge.service;

import java.util.List;
import com.ruoyi.system.knowledge.dto.KosExtractRequest;
import com.ruoyi.system.knowledge.dto.KosExtractResponse;
import com.ruoyi.system.knowledge.domain.KosTerm;
import com.ruoyi.system.knowledge.domain.KosRelationRule;

/**
 * KOS 知识抽取服务接口
 */
public interface IKosExtractionService
{
    KosExtractResponse extract(KosExtractRequest request);

    void refreshKosCache();

    List<KosTerm> listTerms(KosTerm query);

    KosTerm getTerm(String termId);

    int insertTerm(KosTerm term);

    int updateTerm(KosTerm term);

    int deleteTerm(String termId);

    List<KosRelationRule> listRules(KosRelationRule query);

    KosRelationRule getRule(String ruleId);

    int insertRule(KosRelationRule rule);

    int updateRule(KosRelationRule rule);

    int deleteRule(String ruleId);
}
