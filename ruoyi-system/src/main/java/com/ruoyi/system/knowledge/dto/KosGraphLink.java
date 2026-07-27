package com.ruoyi.system.knowledge.dto;

/**
 * 知识图谱边
 */
public class KosGraphLink
{
    private String source;
    private String target;
    private String relation;
    private String method;
    private double value;

    public KosGraphLink()
    {
    }

    public KosGraphLink(String source, String target, String relation, String method, double value)
    {
        this.source = source;
        this.target = target;
        this.relation = relation;
        this.method = method;
        this.value = value;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getRelation()
    {
        return relation;
    }

    public void setRelation(String relation)
    {
        this.relation = relation;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public double getValue()
    {
        return value;
    }

    public void setValue(double value)
    {
        this.value = value;
    }
}
