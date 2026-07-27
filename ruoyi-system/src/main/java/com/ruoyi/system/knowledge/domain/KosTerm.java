package com.ruoyi.system.knowledge.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * KOS 可维护词条
 */
public class KosTerm
{
    private String termId;
    private String name;
    private String type;
    private List<String> aliases;
    private String status;
    private int priority;

    public KosTerm()
    {
    }

    public KosTerm(String termId, String name, String type, List<String> aliases, String status, int priority)
    {
        this.termId = termId;
        this.name = name;
        this.type = type;
        this.aliases = aliases;
        this.status = status;
        this.priority = priority;
    }

    public String getTermId()
    {
        return termId;
    }

    public void setTermId(String termId)
    {
        this.termId = termId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public List<String> getAliases()
    {
        return aliases == null ? Collections.emptyList() : aliases;
    }

    public void setAliases(List<String> aliases)
    {
        this.aliases = aliases;
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

    public List<String> getAllNames()
    {
        List<String> all = new ArrayList<>();
        all.add(name);
        if (aliases != null && !aliases.isEmpty())
        {
            all.addAll(aliases);
        }
        return all;
    }
}
