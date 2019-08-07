package com.yzy.redis.redis.redisClient;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;

public interface redisClient {
  RLock   getRLock(String key);

  String get(String key);

  void set(String key,Object value);

  boolean isExist(String key);

  boolean setex(String key,String value,int expireTime);

  boolean setnx(String key,String value);

  void hset(String key,String field,String entityValue);

  String hget(String key,String field);
}
