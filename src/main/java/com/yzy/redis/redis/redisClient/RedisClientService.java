package com.yzy.redis.redis.redisClient;

import com.google.common.collect.Iterables;
import org.redisson.RedissonKeys;
import org.redisson.api.*;
import org.redisson.command.CommandAsyncExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
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
  public boolean setExpireTime(String key,long timeToLive) {
    RBucket<String> rBucket = redissonClient.getBucket(key);
    return rBucket.expire(timeToLive,TimeUnit.SECONDS);
  }

  /**
   * 返回给定键 key 的旧值。
   * 如果键 key 没有旧值， 也即是说， 键 key 在被设置之前并不存在， 那么命令返回 nil 。
   * 当键 key 存在但不是字符串类型时， 命令返回一个错误。
   * @param key
   * @param value
   * @return
   */
  @Override
  public String getSet(String key, String value) {
    if(key ==null || value == null){
      throw new IllegalArgumentException("key or value is null");
    }
    RBucket<String> rBucket = redissonClient.getBucket(key);
    return rBucket.getAndSet(value);
  }

  /**
   * Redis hash 是一个string类型的field和value的映射表，hash特别适合用于存储对象。
   * hash 有两层 如
   *   objectName {
   *     name : name'value,
   *     id   : id'value
   *   }
   *  hash 的 key 就是 objectName
   *         filed 就是 name,id等字段
   *         value 分别是name,id 所对应的值。
   */

  //得到所有的 filed 属性
  @Override
  public Set<String> hkeys(String key) {
    RMap<String,String> rMap = redissonClient.getMap(key);
    return rMap.readAllKeySet();
  }

  /**
   * hash 删掉其中的 某些field
   * @param key
   * @param entityKey
   * @return
   */
  @Override
  public Long hdel(String key, List<String> entityKey) {
    RMap<String,String> rMap = redissonClient.getMap(key);
//    return rMap.fastRemove(entityKey.toArray(new String[entityKey.size()]));
//    两条语句效果一样
    return rMap.fastRemove(Iterables.toArray(entityKey,String.class));
  }

  @Override
  public Boolean hsetnx(String key, String entityKey, String entityValue) {
    return null;
  }

  /**
   * hash 设置单个的 key field value
   * @param key
   * @param entityKey
   * @param entityValue
   */
  @Override
  public void hset(String key, String entityKey, String entityValue) {
    RMap<String,String> rMap = redissonClient.getMap(key);
    rMap.put(entityKey,entityValue);
  }

  /**
   * hash 设置 一个对象，key + map 的形式
   * @param key
   * @param values
   */
  @Override
  public void hmset(String key, Map<String, String> values) {
    RMap<String,String> rMap = redissonClient.getMap(key);
    rMap.putAll(values);
  }

  /**
   * hash 获得 key 其中的一个field
   * @param key
   * @param field
   * @return
   */
  @Override
  public String hget(String key,String field) {
    RMap<String,String> rMap = redissonClient.getMap(key);
    return rMap.get(field);
  }

  /**
   * hash  获得这个key下的所有属性
   * @param key
   * @return
   */
  @Override
  public Map<String, String> hgetAll(String key) {
    RMap<String,String> rMap = redissonClient.getMap(key);
    return rMap.readAllMap();
  }

  @Override
  public <T> T get(String key, Class<T> clazz) {
    return null;
  }

  @Override
  public <T> List<T> getList(String key, Class<T> clazz) {
    return null;
  }

  @Override
  public long ttl(String key) {
    return 0;
  }

  @Override
  public Boolean del(String key) {
    return null;
  }

  @Override
  public boolean delWithRetry(String key, int retryTimes) {
    return false;
  }

  @Override
  public boolean delWithRetry(String key) {
    return false;
  }

  @Override
  public boolean setWithRetry(String key, String value, int retryTimes, int expireTime, String... params) {
    return false;
  }

  @Override
  public boolean setWithRetry(String key, Object value, int retryTimes, int expireTime, String... params) {
    return false;
  }

  @Override
  public boolean setWithRetry(String key, String value, int expireTime) {
    return false;
  }

  @Override
  public boolean setWithRetry(String key, Object value, int expireTime) {
    return false;
  }

  @Override
  public Long incr(String key) {
    return null;
  }

  @Override
  public Long decr(String key) {
    return null;
  }

  @Override
  public Long incrBy(String key, int Integer) {
    return null;
  }

  @Override
  public Set<String> keys(String pattern) {
    return null;
  }

  @Override
  public long delKeys(String pattern) {
    return 0;
  }

  @Override
  public void sadd(String key, Collection<String> members) {

  }

  @Override
  public void sadd(String key, int seconds, Collection<String> members) {

  }

  @Override
  public Set<String> smembers(String key) {
    return null;
  }

  @Override
  public void srem(String key, Collection<String> members) {

  }

  @Override
  public boolean sismember(String key, String member) {
    return false;
  }

  @Override
  public Boolean rpush(String key, Collection<String> strings) {
    return null;
  }

  @Override
  public Boolean lrem(String key, String value) {
    return null;
  }

  @Override
  public List<String> lrange(String key) {
    return null;
  }

  @Override
  public Object lpop(String key) {
    return null;
  }

  @Override
  public <E> E qpop(String key) {
    return null;
  }

  @Override
  public Integer qlen(String key) {
    return null;
  }

  @Override
  public <E> Boolean qpush(String key, E element) {
    return null;
  }

}
