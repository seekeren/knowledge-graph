package com.ruoyi.system.knowledge.dto;

import java.util.ArrayList;
import java.util.List;
import com.ruoyi.system.knowledge.domain.KosExtractedEntity;
import com.ruoyi.system.knowledge.domain.KosExtractedRelation;

/**
 * KOS 抽取响应
 */
public class KosExtractResponse
{
    private String docId;
    private String title;
    private int entityCount;
    private int relationCount;
    private List<KosExtractedEntity> entities = new ArrayList<>();
    private List<KosExtractedRelation> relations = new ArrayList<>();
    private List<KosGraphNode> nodes = new ArrayList<>();
    private List<KosGraphLink> links = new ArrayList<>();
    private List<String> evidences = new ArrayList<>();

    public String getDocId()
    {
        return docId;
    }

    public void setDocId(String docId)
    {
        this.docId = docId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getEntityCount()
    {
        return entityCount;
    }

    public void setEntityCount(int entityCount)
    {
        this.entityCount = entityCount;
    }

    public int getRelationCount()
    {
        return relationCount;
    }

    public void setRelationCount(int relationCount)
    {
        this.relationCount = relationCount;
    }

    public List<KosExtractedEntity> getEntities()
    {
        return entities;
    }

    public void setEntities(List<KosExtractedEntity> entities)
    {
        this.entities = entities;
    }

    public List<KosExtractedRelation> getRelations()
    {
        return relations;
    }

    public void setRelations(List<KosExtractedRelation> relations)
    {
        this.relations = relations;
    }

    public List<KosGraphNode> getNodes()
    {
        return nodes;
    }

    public void setNodes(List<KosGraphNode> nodes)
    {
        this.nodes = nodes;
    }

    public List<KosGraphLink> getLinks()
    {
        return links;
    }

    public void setLinks(List<KosGraphLink> links)
    {
        this.links = links;
    }

    public List<String> getEvidences()
    {
        return evidences;
    }

    public void setEvidences(List<String> evidences)
    {
        this.evidences = evidences;
    }
}
