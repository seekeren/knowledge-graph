package com.ruoyi.system.knowledge.config;

import java.util.concurrent.TimeUnit;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Neo4j 驱动配置
 */
@Configuration
public class KosNeo4jConfig
{
    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(prefix = "kos.neo4j", name = "enabled", havingValue = "true", matchIfMissing = true)
    public Driver kosNeo4jDriver(KosProperties properties)
    {
        KosProperties.Neo4j neo4j = properties.getNeo4j();
        Config.ConfigBuilder builder = Config.builder();
        long seconds = 30;
        try
        {
            seconds = Long.parseLong(neo4j.getConnectionTimeout().replaceAll("[^0-9]", ""));
        }
        catch (Exception ignored)
        {
        }
        builder.withConnectionTimeout(seconds, TimeUnit.SECONDS);
        return GraphDatabase.driver(neo4j.getUri(), AuthTokens.basic(neo4j.getUsername(), neo4j.getPassword()), builder.build());
    }
}
