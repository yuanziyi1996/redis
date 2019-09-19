package com.yzy.redis;

import com.yzy.redis.redis.redisClient.RedisClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author ziyi.yuan
 * @date 2019-08-08 16:34
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class StringTest {
  @Autowired
  private RedisClientService redisClientService;

  @Test
  public void getSetTest(){
    String key = "get_set_key_not_exist_before";
    String value = "get_set_value_not_exist_before";
    log.info("测试key在测试之前不存在，预期返回 null，key存在则预期返回value");
    String result = redisClientService.getSet(key,value);
    //设置20秒过期 之后这个key在库里还是不存在
    redisClientService.setex(key,value,20);
    log.info("测试结果 result={}",result);
  }
}

