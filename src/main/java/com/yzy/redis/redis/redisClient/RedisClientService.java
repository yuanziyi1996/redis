package com.yzy.redis.redis.redisClient;

import org.redisson.RedissonKeys;
import org.redisson.api.*;
import org.redisson.command.CommandAsyncExecutor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class RedisClientService implements redisClient{

  private RedissonClient redissonClient;


  public RedisClientService(RedissonClient redissonClient){
    this.redissonClient=redissonClient;
  }

  @Override
  public RLock getRLock(String key) {
    return redissonClient.getLock(key);
  }

  @Override
  public String get(String key) {
    RBucket<String> bucket = redissonClient.getBucket(key);
    return bucket.get();
  }

  @Override
  public void set(String key, Object value) {
    RBucket<Object> bucket = redissonClient.getBucket(key);
    bucket.set(value);
  }

  @Override
  public boolean isExist(String key) {
    return redissonClient.getBucket(key).isExists();
  }

  @Override
  public boolean setex(String key, String value, int expireTime) {

    try{
      RBucket<Object> bucket = redissonClient.getBucket(key);
      bucket.set(value,expireTime,TimeUnit.SECONDS);
      return true;
    }catch (Exception e){
      System.out.println("setex error.");
      return false;
    }
  }

  @Override
  public boolean setnx(String key, String value) {
    RBucket<String> rBucket = redissonClient.getBucket(key);
    return rBucket.trySet(value);
  }

  @Override
  public void hset(String key, String entityKey, String entityValue) {
    RMap<String,String> rMap = redissonClient.getMap(key);
    rMap.put(entityKey,entityValue);
  }

  @Override
  public String hget(String key,String field) {
    RMap<String,String> rMap = redissonClient.getMap(key);
    return rMap.get(field);
  }

}
