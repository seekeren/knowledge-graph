package com.ruoyi.system.knowledge.domain;

import java.util.Collections;
import java.util.List;

/**
 * KOS 关系规则
 */
public class KosRelationRule
{
    private String ruleId;
    private String name;
    private String triggerPattern;
    private String relationType;
    private List<String> sourceTypes;
    private List<String> targetTypes;
    private String direction;
    private int maxDistance;
    private String status;
    private int priority;

    public KosRelationRule()
    {
    }

    public KosRelationRule(String ruleId, String name, String triggerPattern, String relationType,
            List<String> sourceTypes, List<String> targetTypes, String direction, int maxDistance,
            String status, int priority)
    {
        this.ruleId = ruleId;
        this.name = name;
        this.triggerPattern = triggerPattern;
        this.relationType = relationType;
        this.sourceTypes = sourceTypes;
        this.targetTypes = targetTypes;
        this.direction = direction;
        this.maxDistance = maxDistance;
        this.status = status;
        this.priority = priority;
    }

    public String getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(String ruleId)
    {
        this.ruleId = ruleId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTriggerPattern()
    {
        return triggerPattern;
    }

    public void setTriggerPattern(String triggerPattern)
    {
        this.triggerPattern = triggerPattern;
    }

    public String getRelationType()
    {
        return relationType;
    }

    public void setRelationType(String relationType)
    {
        this.relationType = relationType;
    }

    public List<String> getSourceTypes()
    {
        return sourceTypes == null ? Collections.emptyList() : sourceTypes;
    }

    public void setSourceTypes(List<String> sourceTypes)
    {
        this.sourceTypes = sourceTypes;
    }

    public List<String> getTargetTypes()
    {
        return targetTypes == null ? Collections.emptyList() : targetTypes;
    }

    public void setTargetTypes(List<String> targetTypes)
    {
        this.targetTypes = targetTypes;
    }

    public String getDirection()
    {
        return direction;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }

    public int getMaxDistance()
    {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance)
    {
        this.maxDistance = maxDistance;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }
}
