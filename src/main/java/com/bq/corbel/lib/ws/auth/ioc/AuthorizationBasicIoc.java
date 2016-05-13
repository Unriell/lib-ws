package com.bq.corbel.lib.ws.auth.ioc;

import com.google.gson.JsonObject;
import com.bq.corbel.lib.ws.auth.*;
import com.bq.corbel.lib.ws.auth.repository.AuthorizationRulesRepository;
import com.bq.corbel.lib.ws.auth.repository.RedisAuthorizationRulesRepository;
import com.bq.corbel.lib.ws.filter.InformationResponseFilter;
import com.bq.corbel.lib.ws.health.AuthorizationRedisHealthCheck;
import com.bq.corbel.lib.ws.redis.GsonRedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.ws.rs.container.ContainerRequestFilter;

/**
 * Created by ruben on 12/01/16.
 */
@Configuration
public class AuthorizationBasicIoc {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationBasicIoc.class);

    @Bean
    public AuthorizationRulesRepository getAuthorizationRulesRepository(RedisTemplate<String, JsonObject> redisTemplate) {
        return new RedisAuthorizationRulesRepository(redisTemplate);
    }

    @Bean
    public RedisTemplate<String, JsonObject> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        final RedisTemplate<String, JsonObject> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GsonRedisSerializer<JsonObject>());
        return template;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig, @Value("${auth.redis.host:@null}") String host,
                                                         @Value("${auth.redis.port:@null}") Integer port, @Value("${auth.redis.password:}") String password) {
        JedisConnectionFactory connFactory = new JedisConnectionFactory(jedisPoolConfig);
        connFactory.setPassword(password);
        if (host != null) {
            connFactory.setHostName(host);
        }
        if (port != null) {
            connFactory.setPort(port);
        }
        return connFactory;
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig(@Value("${auth.redis.maxIdle:@null}") Integer maxIdle,
                                           @Value("${auth.redis.maxTotal:@null}") Integer maxTotal, @Value("${auth.redis.minIdle:@null}") Integer minIdle,
                                           @Value("${auth.redis.testOnBorrow:@null}") Boolean testOnBorrow,
                                           @Value("${auth.redis.testOnReturn:@null}") Boolean testOnReturn,
                                           @Value("${auth.redis.testWhileIdle:@null}") Boolean testWhileIdle,
                                           @Value("${auth.redis.numTestsPerEvictionRun:@null}") Integer numTestsPerEvictionRun,
                                           @Value("${auth.redis.maxWaitMillis:@null}") Long maxWaitMillis,
                                           @Value("${auth.redis.timeBetweenEvictionRunsMillis:@null}") Long timeBetweenEvictionRunsMillis,
                                           @Value("${auth.redis.blockWhenExhausted:@null}") Boolean blockWhenExhausted) {
        JedisPoolConfig config = new JedisPoolConfig();
        if (maxIdle != null) {
            config.setMaxIdle(maxIdle);
        }
        if (maxTotal != null) {
            config.setMaxTotal(maxTotal);
        }
        if (minIdle != null) {
            config.setMinIdle(minIdle);
        }
        if (testOnBorrow != null) {
            config.setTestOnBorrow(testOnBorrow);
        }
        if (testOnReturn != null) {
            config.setTestOnReturn(testOnReturn);
        }
        if (testWhileIdle != null) {
            config.setTestWhileIdle(testWhileIdle);
        }
        if (numTestsPerEvictionRun != null) {
            config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        }
        if (timeBetweenEvictionRunsMillis != null) {
            config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        }
        if (maxWaitMillis != null) {
            config.setMaxWaitMillis(maxWaitMillis);
        }
        if (blockWhenExhausted != null) {
            config.setBlockWhenExhausted(blockWhenExhausted);
        }
        return config;
    }

    @Bean
    public AuthorizationRulesService authorizationRulesService(AuthorizationRulesRepository authorizationRulesRepository) {
        return new DefaultAuthorizationRulesService(authorizationRulesRepository);
    }

    @Bean
    public AuthorizationInfoProvider getAuthorizationInfoProvider() {
        return new AuthorizationInfoProvider();
    }

    @Bean
    public AuthorizationRedisHealthCheck getAuthorizationRedisHealthCheck(RedisTemplate<String, JsonObject> redisTemplate) {
        return new AuthorizationRedisHealthCheck(redisTemplate);
    }

    @Bean
    public InformationResponseFilter getRequestInformationRequestFilter() {
        return new InformationResponseFilter();
    }

}
