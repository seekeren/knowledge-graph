package com.ruoyi.system.knowledge.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 抽取结果中的实体
 */
public class KosExtractedEntity
{
    private String entityId;
    private String normalizedName;
    private String type;
    private int frequency;
    private double score;
    private List<Integer> offsets = new ArrayList<>();
    private String termId;
    private List<String> matchedNames = new ArrayList<>();

    public String getEntityId()
    {
        return entityId;
    }

    public void setEntityId(String entityId)
    {
        this.entityId = entityId;
    }

    public String getNormalizedName()
    {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName)
    {
        this.normalizedName = normalizedName;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public int getFrequency()
    {
        return frequency;
    }

    public void setFrequency(int frequency)
    {
        this.frequency = frequency;
    }

    public double getScore()
    {
        return score;
    }

    public void setScore(double score)
    {
        this.score = score;
    }

    public List<Integer> getOffsets()
    {
        return offsets;
    }

    public void setOffsets(List<Integer> offsets)
    {
        this.offsets = offsets;
    }

    public String getTermId()
    {
        return termId;
    }

    public void setTermId(String termId)
    {
        this.termId = termId;
    }

    public List<String> getMatchedNames()
    {
        return matchedNames;
    }

    public void setMatchedNames(List<String> matchedNames)
    {
        this.matchedNames = matchedNames;
    }
}
