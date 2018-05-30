package com.liberologico.janine.conf;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPool;

@Configuration
public class JedisPoolConfig
{
    @Bean
    public JedisPool jedisPool(  JedisConnectionFactory jedisConnectionFactory )
    {
        GenericObjectPoolConfig poolConfig = jedisConnectionFactory.getPoolConfig();
        poolConfig.setTestOnBorrow( true );
        return new JedisPool( poolConfig,
                jedisConnectionFactory.getHostName(),
                jedisConnectionFactory.getPort()
        );
    }
}
