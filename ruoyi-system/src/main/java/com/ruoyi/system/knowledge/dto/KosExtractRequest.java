package com.ruoyi.system.knowledge.dto;

import java.util.List;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * KOS 抽取请求
 */
public class KosExtractRequest
{
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    @NotBlank(message = "正文不能为空")
    @Size(max = 20000, message = "正文长度不能超过20000个字符")
    private String content;

    @Min(value = 1, message = "最大实体数至少为1")
    @Max(value = 500, message = "最大实体数不能超过500")
    private Integer maxEntities = 200;

    @Min(value = 1, message = "最小词频至少为1")
    @Max(value = 100, message = "最小词频不能超过100")
    private Integer minFrequency = 1;

    @Min(value = 10, message = "共现窗口至少为10")
    @Max(value = 1000, message = "共现窗口不能超过1000")
    private Integer cooccurrenceWindow = 120;

    @Min(value = 1, message = "最小共现次数至少为1")
    @Max(value = 100, message = "最小共现次数不能超过100")
    private Integer minCooccurrence = 1;

    private Boolean enableRuleRelation = true;

    private Boolean enableCooccurrence = true;

    private List<String> entityTypes;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public Integer getMaxEntities()
    {
        return maxEntities;
    }

    public void setMaxEntities(Integer maxEntities)
    {
        this.maxEntities = maxEntities;
    }

    public Integer getMinFrequency()
    {
        return minFrequency;
    }

    public void setMinFrequency(Integer minFrequency)
    {
        this.minFrequency = minFrequency;
    }

    public Integer getCooccurrenceWindow()
    {
        return cooccurrenceWindow;
    }

    public void setCooccurrenceWindow(Integer cooccurrenceWindow)
    {
        this.cooccurrenceWindow = cooccurrenceWindow;
    }

    public Integer getMinCooccurrence()
    {
        return minCooccurrence;
    }

    public void setMinCooccurrence(Integer minCooccurrence)
    {
        this.minCooccurrence = minCooccurrence;
    }

    public Boolean getEnableRuleRelation()
    {
        return enableRuleRelation;
    }

    public void setEnableRuleRelation(Boolean enableRuleRelation)
    {
        this.enableRuleRelation = enableRuleRelation;
    }

    public Boolean getEnableCooccurrence()
    {
        return enableCooccurrence;
    }

    public void setEnableCooccurrence(Boolean enableCooccurrence)
    {
        this.enableCooccurrence = enableCooccurrence;
    }

    public List<String> getEntityTypes()
    {
        return entityTypes;
    }

    public void setEntityTypes(List<String> entityTypes)
    {
        this.entityTypes = entityTypes;
    }
}
