package com.ruoyi.system.knowledge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * KOS 知识抽取配置属性
 */
@Component
@ConfigurationProperties(prefix = "kos")
public class KosProperties
{
    private Neo4j neo4j = new Neo4j();

    private Extract extract = new Extract();

    public Neo4j getNeo4j()
    {
        return neo4j;
    }

    public void setNeo4j(Neo4j neo4j)
    {
        this.neo4j = neo4j;
    }

    public Extract getExtract()
    {
        return extract;
    }

    public void setExtract(Extract extract)
    {
        this.extract = extract;
    }

    public static class Neo4j
    {
        private String uri = "bolt://localhost:7687";
        private String database = "neo4j";
        private String username = "neo4j";
        private String password = "neo4j";
        private String connectionTimeout = "30s";
        private boolean enabled = true;

        public String getUri()
        {
            return uri;
        }

        public void setUri(String uri)
        {
            this.uri = uri;
        }

        public String getDatabase()
        {
            return database;
        }

        public void setDatabase(String database)
        {
            this.database = database;
        }

        public String getUsername()
        {
            return username;
        }

        public void setUsername(String username)
        {
            this.username = username;
        }

        public String getPassword()
        {
            String env = System.getenv("NEO4J_PASSWORD");
            return env != null && !env.isEmpty() ? env : password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public String getConnectionTimeout()
        {
            return connectionTimeout;
        }

        public void setConnectionTimeout(String connectionTimeout)
        {
            this.connectionTimeout = connectionTimeout;
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public void setEnabled(boolean enabled)
        {
            this.enabled = enabled;
        }
    }

    public static class Extract
    {
        private int maxTextLength = 20000;
        private int defaultMaxEntities = 200;
        private int defaultMinFrequency = 1;
        private int defaultCooccurrenceWindow = 120;
        private int defaultMinCooccurrence = 1;
        private int ruleTimeoutMillis = 2000;
        private int maxRulePatternLength = 500;

        public int getMaxTextLength()
        {
            return maxTextLength;
        }

        public void setMaxTextLength(int maxTextLength)
        {
            this.maxTextLength = maxTextLength;
        }

        public int getDefaultMaxEntities()
        {
            return defaultMaxEntities;
        }

        public void setDefaultMaxEntities(int defaultMaxEntities)
        {
            this.defaultMaxEntities = defaultMaxEntities;
        }

        public int getDefaultMinFrequency()
        {
            return defaultMinFrequency;
        }

        public void setDefaultMinFrequency(int defaultMinFrequency)
        {
            this.defaultMinFrequency = defaultMinFrequency;
        }

        public int getDefaultCooccurrenceWindow()
        {
            return defaultCooccurrenceWindow;
        }

        public void setDefaultCooccurrenceWindow(int defaultCooccurrenceWindow)
        {
            this.defaultCooccurrenceWindow = defaultCooccurrenceWindow;
        }

        public int getDefaultMinCooccurrence()
        {
            return defaultMinCooccurrence;
        }

        public void setDefaultMinCooccurrence(int defaultMinCooccurrence)
        {
            this.defaultMinCooccurrence = defaultMinCooccurrence;
        }

        public int getRuleTimeoutMillis()
        {
            return ruleTimeoutMillis;
        }

        public void setRuleTimeoutMillis(int ruleTimeoutMillis)
        {
            this.ruleTimeoutMillis = ruleTimeoutMillis;
        }

        public int getMaxRulePatternLength()
        {
            return maxRulePatternLength;
        }

        public void setMaxRulePatternLength(int maxRulePatternLength)
        {
            this.maxRulePatternLength = maxRulePatternLength;
        }
    }
}
