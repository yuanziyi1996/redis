package com.yzy.redis.redis.redisClient;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface redisClient {
  RLock   getRLock(String key);

  String get(String key);

  void set(String key,Object value);

  /**
   *在缓存中 是否存在
   */
  boolean isExist(String key);

  boolean setex(String key,String value,int expireTime);

  /**
   * 设定一个值,如果值不存在创建一个返回1,如果值存在不做操作返回0
   */
  boolean setnx(String key,String value);

  /**
   * 设置过期时间
   * @param key
   * @return
   */
  boolean setExpireTime(String key,long timeToLive);


  String getSet(String key, String value  );

  /**
   * 获取一个hash中的所有的key
   */
  Set<String> hkeys(String key  );

  /**
   * 删除一个hash中的一个key
   */
  Long hdel(String key, List<String> entityKey  );

  /**
   * 设置在map中设置一个entityKey, 如果entityKey之前不存在设置成功,返回1 如果entityKey之前存在设置失败,返回0
   *
   * @param key hashmap的键值
   * @param entityKey map中实体的key
   * @param entityValue map中实体的value
   */
  Boolean hsetnx(String key, String entityKey, String entityValue);

  void hset(String key,String field,String entityValue);

  void hmset(String key, Map<String, String> values);

  String hget(String key, String field);

  Map<String, String> hgetAll(String key  );

  /**
   * 获取对象 <br> 2015年1月28日:下午3:02:23<br> <br>
   */
  <T> T get(String key, Class<T> clazz  );

  <T> List<T> getList(String key, Class<T> clazz  );

  long ttl(String key  );


  /**
   * 删除对象 <br> 2015年10月29日:下午5:06:20<br> <br>
   */
  Boolean del(String key  );

  /**
   * 删除对象 尝试retryTimes次 <br>
   */
  boolean delWithRetry(String key, int retryTimes  );

  /**
   * 删除对象 尝试固定次数
   */
  boolean delWithRetry(String key  );

  /**
   * 放入缓存 尝试retryTimes
   */
  boolean setWithRetry(String key, String value, int retryTimes, int expireTime,
                       String... params);

  /**
   * 把Object类型数据放入缓存 尝试retryTimes
   */
  boolean setWithRetry(String key, Object value, int retryTimes, int expireTime,
                       String... params);

  /**
   * 放入缓存 尝试固定次数
   */
  boolean setWithRetry(  String key, String value, int expireTime  );

  /**
   * 把Object类型放入缓存 尝试固定次数
   */
  boolean setWithRetry(String key, Object value, int expireTime  );

  /**
   * @Title: getIncr
   * @Description: Increment the number stored at key by one
   * @param: @param area
   * @param: @param key
   * @param: @return
   */
  Long incr(String key  );

  /**
   * key值减少1
   */
  Long decr(String key  );

  /**
   * @Title: incrBy
   * @Description: the work just like INCR but instead to increment by 1 the increment is integer
   * @param: @param area
   * @param: @param key
   * @param: @param Integer
   * @param: @return
   */
  Long incrBy(String key, int Integer  );

  Set<String> keys(String pattern);

  /**
   * delete by patterns
   *
   * @param pattern pattern
   * @return delete amount
   */
  long delKeys(String pattern);


  /**
   * 向一个set的数据结构增加一些成员，如果set的key不存在会新建一个set
   *
   * @warn 如果要添加的key不是set会报异常
   */
  void sadd(String key, Collection<String> members  );

  /**
   * 添加一个带过期时间的 set
   */
  void sadd(String key, int seconds, Collection<String> members  );

  /**
   * 获取set类型的指定key的所有member
   */
  Set<String> smembers(String key  );

  /**
   * 移除set的数据结构中的一些成员
   */
  void srem(String key, Collection<String> members  );

  /**
   * 判断set中是否包含member成员
   */
  boolean sismember(String key, String member  );
  

  Boolean rpush(String key, Collection<String> strings);

  Boolean lrem(String key, String value);

  List<String> lrange(String key);

  Object lpop(String key);

  <E> E qpop(  String key  );

  Integer qlen(  String key  );

  <E> Boolean qpush(  String key, E element  );
}
