package com.ruoyi.system.knowledge.engine;

import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.system.knowledge.config.KosProperties;
import com.ruoyi.system.knowledge.domain.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * 确定性 KOS 抽取引擎（词典+规则+共现）
 */
@Component
public class KosExtractionEngine
{
    private List<KosTerm> termsCache = new ArrayList<>();
    private List<KosRelationRule> rulesCache = new ArrayList<>();
    private long cacheTimestamp = 0L;

    @Autowired
    private KosProperties properties;

    private static final int CACHE_TTL_MS = 300000;

    @PostConstruct
    public void init()
    {
    }

    @PreDestroy
    public void destroy()
    {
        termsCache.clear();
        rulesCache.clear();
    }

    public synchronized void refreshCache(List<KosTerm> terms, List<KosRelationRule> rules)
    {
        this.termsCache = terms != null ? new ArrayList<>(terms) : new ArrayList<>();
        this.rulesCache = rules != null ? new ArrayList<>(rules) : new ArrayList<>();
        this.cacheTimestamp = System.currentTimeMillis();
    }

    public boolean isCacheExpired()
    {
        return System.currentTimeMillis() - cacheTimestamp > CACHE_TTL_MS;
    }

    public List<KosExtractedEntity> extractEntities(String text, int minFrequency, Set<String> typeFilter)
    {
        if (text == null || text.isEmpty())
            return new ArrayList<>();

        List<KosTerm> activeTerms = termsCache;
        if (typeFilter != null && !typeFilter.isEmpty())
        {
            activeTerms = activeTerms.stream().filter(t -> typeFilter.contains(t.getType())).collect(Collectors.toList());
        }

        Map<String, KosExtractedEntity> entityMap = new LinkedHashMap<>();
        List<MatchCandidate> candidates = new ArrayList<>();

        for (KosTerm term : activeTerms)
        {
            for (String name : term.getAllNames())
            {
                int idx = 0;
                while ((idx = text.indexOf(name, idx)) >= 0)
                {
                    candidates.add(new MatchCandidate(idx, name, term));
                    idx += name.length();
                }
            }
        }

        candidates.sort(Comparator.comparingInt(MatchCandidate::getStart).reversed()
                .thenComparingInt(c -> c.name.length()).reversed().thenComparingInt(c -> c.term.getPriority()).reversed());

        Set<Integer> usedPos = new HashSet<>();
        for (MatchCandidate mc : candidates)
        {
            if (!isOverlap(mc.start, mc.name.length(), usedPos))
            {
                markUsed(mc.start, mc.name.length(), usedPos);
                String key = mc.term.getName();
                entityMap.putIfAbsent(key,
                        newEntity(text, mc.term, key, Collections.singletonList(mc.start), minFrequency));
                KosExtractedEntity e = entityMap.get(key);
                e.getOffsets().add(mc.start);
                e.setFrequency(e.getOffsets().size());
                e.setScore(e.getFrequency() * (1.0 + mc.term.getPriority() * 0.1));
            }
        }

        return entityMap.values().stream().sorted(Comparator.comparingInt(KosExtractedEntity::getFrequency).reversed())
                .collect(Collectors.toList());
    }

    public List<KosExtractedRelation> extractRelations(String text, List<KosExtractedEntity> entities,
            boolean enableRule, boolean enableCooc, int coocWindow, int minCooc)
    {
        List<KosExtractedRelation> relations = new ArrayList<>();
        Map<String, KosExtractedEntity> nameToEntity = entities.stream()
                .collect(Collectors.toMap(KosExtractedEntity::getNormalizedName, e -> e, (a, b) -> a));

        if (enableRule)
        {
            relations.addAll(extractRuleRelations(text, entities, nameToEntity));
        }

        if (enableCooc)
        {
            relations.addAll(extractCooccurrenceRelations(text, entities, nameToEntity, coocWindow, minCooc));
        }

        Map<String, KosExtractedRelation> dedup = new LinkedHashMap<>();
        for (KosExtractedRelation r : relations)
        {
            String key = r.getSourceName() + "||" + r.getTargetName() + "||" + r.getRelationType();
            KosExtractedRelation existing = dedup.get(key);
            if (existing == null || "RULE".equals(r.getMethod()))
            {
                dedup.put(key, r);
            }
        }

        return new ArrayList<>(dedup.values());
    }

    private List<KosExtractedRelation> extractRuleRelations(String text, List<KosExtractedEntity> entities,
            Map<String, KosExtractedEntity> nameToEntity)
    {
        List<KosExtractedRelation> relations = new ArrayList<>();
        for (KosRelationRule rule : rulesCache)
        {
            Pattern pat;
            try
            {
                pat = Pattern.compile("(?:" + rule.getTriggerPattern() + ")");
            }
            catch (Exception e)
            {
                continue;
            }
            Matcher m = pat.matcher(text);
            while (m.find())
            {
                int trigStart = m.start();
                int trigEnd = m.end();
                List<KosExtractedEntity> lefts = new ArrayList<>(), rights = new ArrayList<>();
                for (KosExtractedEntity e : entities)
                {
                    for (int off : e.getOffsets())
                    {
                        if (off + e.getNormalizedName().length() <= trigStart
                                && trigStart - off <= rule.getMaxDistance())
                        {
                            if (rule.getSourceTypes().contains(e.getType()))
                                lefts.add(e);
                        }
                        if (off >= trigEnd && off - trigEnd <= rule.getMaxDistance())
                        {
                            if (rule.getTargetTypes().contains(e.getType()))
                                rights.add(e);
                        }
                    }
                }
                if ("LEFT".equals(rule.getDirection()))
                {
                    for (KosExtractedEntity s : lefts)
                    {
                        KosExtractedRelation r = new KosExtractedRelation();
                        r.setSourceName(s.getNormalizedName());
                        r.setSourceType(s.getType());
                        r.setTargetName(s.getNormalizedName());
                        r.setTargetType(s.getType());
                        r.setRelationType(rule.getRelationType());
                        r.setMethod("RULE");
                        r.setRuleId(rule.getRuleId());
                        r.setScore(1.0 + rule.getPriority() * 0.1);
                        r.setEvidence(text.substring(Math.max(0, trigStart - 20), Math.min(text.length(), trigEnd + 20)));
                        relations.add(r);
                    }
                }
                else
                {
                    for (KosExtractedEntity s : lefts)
                    {
                        for (KosExtractedEntity t : rights)
                        {
                            KosExtractedRelation r = new KosExtractedRelation();
                            r.setSourceName(s.getNormalizedName());
                            r.setSourceType(s.getType());
                            r.setTargetName(t.getNormalizedName());
                            r.setTargetType(t.getType());
                            r.setRelationType(rule.getRelationType());
                            r.setMethod("RULE");
                            r.setRuleId(rule.getRuleId());
                            r.setScore(1.0 + rule.getPriority() * 0.1);
                            r.setEvidence(text.substring(Math.max(0, trigStart - 20), Math.min(text.length(), trigEnd + 20)));
                            relations.add(r);
                        }
                    }
                }
            }
        }
        return relations;
    }

    private List<KosExtractedRelation> extractCooccurrenceRelations(String text, List<KosExtractedEntity> entities,
            Map<String, KosExtractedEntity> nameToEntity, int window, int minCount)
    {
        String[] sentences = text.split("[。！？.!?;；\n]");
        Map<String, Map<String, Integer>> pairCounts = new HashMap<>();
        Map<String, Map<String, Integer>> pairDistances = new HashMap<>();

        for (String sent : sentences)
        {
            List<int[]> hits = new ArrayList<>();
            for (KosExtractedEntity e : entities)
            {
                String name = e.getNormalizedName();
                int idx = 0;
                while ((idx = sent.indexOf(name, idx)) >= 0)
                {
                    hits.add(new int[] { idx, idx + name.length(), entities.indexOf(e) });
                    idx += name.length();
                }
            }
            hits.sort(Comparator.comparingInt(a -> a[0]));
            for (int i = 0; i < hits.size(); i++)
            {
                for (int j = i + 1; j < hits.size(); j++)
                {
                    if (hits.get(j)[0] - hits.get(i)[1] > window)
                        break;
                    String e1 = entities.get(hits.get(i)[2]).getNormalizedName();
                    String e2 = entities.get(hits.get(j)[2]).getNormalizedName();
                    int dist = hits.get(j)[0] - hits.get(i)[1];
                    if (e1.equals(e2))
                        continue;
                    pairCounts.computeIfAbsent(e1, k -> new HashMap<>()).merge(e2, 1, Integer::sum);
                    pairDistances.computeIfAbsent(e1, k -> new HashMap<>()).merge(e2, dist, Math::min);
                }
            }
        }

        List<KosExtractedRelation> relations = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> e1Entry : pairCounts.entrySet())
        {
            for (Map.Entry<String, Integer> e2Entry : e1Entry.getValue().entrySet())
            {
                int cnt = e2Entry.getValue();
                if (cnt >= minCount)
                {
                    KosExtractedEntity e1 = nameToEntity.get(e1Entry.getKey());
                    KosExtractedEntity e2 = nameToEntity.get(e2Entry.getKey());
                    KosExtractedRelation r = new KosExtractedRelation();
                    r.setSourceName(e1.getNormalizedName());
                    r.setSourceType(e1.getType());
                    r.setTargetName(e2.getNormalizedName());
                    r.setTargetType(e2.getType());
                    r.setRelationType("RELATED");
                    r.setMethod("COOC");
                    r.setScore(0.5 + cnt * 0.1);
                    r.setEvidence("共现" + cnt + "次");
                    relations.add(r);
                }
            }
        }
        return relations;
    }

    private boolean isOverlap(int start, int len, Set<Integer> used)
    {
        for (int i = start; i < start + len; i++)
        {
            if (used.contains(i))
                return true;
        }
        return false;
    }

    private void markUsed(int start, int len, Set<Integer> used)
    {
        for (int i = start; i < start + len; i++)
        {
            used.add(i);
        }
    }

    private KosExtractedEntity newEntity(String text, KosTerm term, String normalizedName, List<Integer> offsets,
            int minFreq)
    {
        KosExtractedEntity e = new KosExtractedEntity();
        e.setEntityId(UUID.randomUUID().toString());
        e.setNormalizedName(normalizedName);
        e.setType(term.getType());
        e.setTermId(term.getTermId());
        e.setOffsets(new ArrayList<>(offsets));
        e.setFrequency(1);
        e.setScore(1.0 + term.getPriority() * 0.1);
        e.setMatchedNames(Collections.singletonList(normalizedName));
        return e;
    }

    private static class MatchCandidate
    {
        int start;
        String name;
        KosTerm term;

        MatchCandidate(int start, String name, KosTerm term)
        {
            this.start = start;
            this.name = name;
            this.term = term;
        }

        int getStart()
        {
            return start;
        }
    }
}
