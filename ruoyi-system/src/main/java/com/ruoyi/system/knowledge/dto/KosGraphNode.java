package com.ruoyi.system.knowledge.dto;

/**
 * 知识图谱节点
 */
public class KosGraphNode
{
    private String id;
    private String name;
    private String category;
    private int value;
    private double symbolSize;

    public KosGraphNode()
    {
    }

    public KosGraphNode(String id, String name, String category, int value, double symbolSize)
    {
        this.id = id;
        this.name = name;
        this.category = category;
        this.value = value;
        this.symbolSize = symbolSize;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCategory()
    {
        return category;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public double getSymbolSize()
    {
        return symbolSize;
    }

    public void setSymbolSize(double symbolSize)
    {
        this.symbolSize = symbolSize;
    }
}
