package com.yzy.redis;

import com.yzy.redis.redis.redisClient.RedisClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * @author ziyi.yuan
 * @date 2019-08-08 17:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class hashTest {
  @Autowired
  private RedisClientService redisClientService;

  @Test
  public void hashsetTest(){
    String key = "test_hash_set_key";
    String field = "test_hash_set_field";
    String value = "test_hash_set_value";
    redisClientService.hset(key,field,value);
    String result = redisClientService.hget(key,field);
    log.info("hash 测试 结果 ={}",result);
  }

  @Test
  public void hash_hgetAll(){
    String key = "test_hash_set_key";
    String filed1 = "test_hash_set_field1";
    String value1 = "test_hash_set_value";
    String filed2 = "test_hash_set_field2";
    String value2 = "test_hash_set_valeu2";
    redisClientService.hset(key,filed1,value1);
    redisClientService.hset(key,filed2,value2);
    Object result = redisClientService.hkeys(key);
    log.info("使用hset hkeys操作，返回值={}",result);
    //设置值存活 5 秒
    redisClientService.setExpireTime(key,5);
    try {
      Thread.sleep(6000);
      result= redisClientService.hkeys(key);
      log.info("将key 从 redis中删除，预期返回null ，result={}",result);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    Map<String,String> rmap = new HashMap<>();
    rmap.put(filed1,value1);
    rmap.put(filed2,value2);
    redisClientService.hmset(key,rmap);
    result = redisClientService.hkeys(key);
    log.info("使用hmset 批量写入redis,预期获取到所有的 filed值，结果 ={}",result);
    redisClientService.setExpireTime(key,5);
  }

  @Test
  public void hash_hdel(){
    String key = "test_hash_hdel_key";
    String filed1 = "test_hash_hdel_field1";
    String value1 = "test_hash_hdel_value";
    String filed2 = "test_hash_hdel_field2";
    String value2 = "test_hash_hdel_valeu2";
    Map<String,String> rmap = new HashMap<>();
    rmap.put(filed1,value1);
    rmap.put(filed2,value2);
    redisClientService.hmset(key,rmap);
    redisClientService.hdel(key, Arrays.asList(filed1));
    Object result = redisClientService.hgetAll(key);
    log.info("hdel 删掉filed1 后的 预期结果只剩下field2及value ={}",result);
  }
}
