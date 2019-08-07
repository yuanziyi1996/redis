package com.yzy.redis.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzy.redis.redis.redisClient.RedisClientService;
import com.yzy.redis.utils.JacksonUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@ComponentScan(basePackages = "com.yzy.redis.redis")
public class RedisConstant {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private String redisPort;

  @Bean
  public RedisClientService yzyRedisClient(){
    Config config = new Config();

//指定编码，默认编码为org.redisson.codec.JsonJacksonCodec
    config.setCodec(JacksonUtils.getJsonJacksonCodec());
//    config.setCodec(new org.redisson.client.codec.StringCodec());
    String address = "redis://" + redisHost + ":" + redisPort;
    config.useSingleServer().setAddress(address);
    RedissonClient redissonClient = Redisson.create(config);

    RedisClientService redisClientService = new RedisClientService(redissonClient);
    return redisClientService;
  }

  @Bean
  public CacheManager cacheManager(RedisConnectionFactory factory) {
    RedisSerializer<String> redisSerializer = new StringRedisSerializer();
    Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

    //解决查询缓存转换异常的问题
    ObjectMapper om = new ObjectMapper();
    om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    jackson2JsonRedisSerializer.setObjectMapper(om);

    // 配置序列化（解决乱码的问题）
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
      .entryTtl(Duration.ZERO)
      .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
      .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
      .disableCachingNullValues();

    RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
      .cacheDefaults(config)
      .build();
    return cacheManager;
  }

}
