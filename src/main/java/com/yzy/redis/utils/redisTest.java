package com.yzy.redis.utils;

import com.yzy.redis.redis.redisClient.RedisClientService;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class redisTest {

  @Autowired
  private static RedisClientService redisClientService;
  public static void main(String[] args) {

    //模拟2个线程
    for (int i = 1; i <= 1; i++) {

      //可以开2个IDE，分别测试以下三个方法
      //打开2个IDE同时执行时，这里可以分别取不同名，区分
      new Thread("IDE-ONE-"+i) {
        @Override
        public void run() {

          /**
           * 测试testLock结果，每个IDE中线程，依次排队等待获取锁。然后执行任务
           */
//                    testLock("redissonlocktest_testkey");

          /**
           * 测试testTryLock结果，每个IDE中线程，在TryLock的等待时间范围内，若获取到锁，返回true,则执行任务;若获取不到，则返回false，直接返回return;
           */
          testTryLock("redissonlocktest_testkey");

          /**
           * 测试testSyncro结果，IDE之间的线程互不影响，同一个IDE中的线程排队值执行，不同IDE之间的互补影响，可同时执行
           */
//                    testSyncro("redissonlocktest_testkey");
        }
      }.start();
    }

  }

  public static void testTryLock(String key){
    RLock lock = redisClientService.getRLock(key);
    try {
     boolean result = lock.tryLock(1, 10, TimeUnit.SECONDS);
      System.out.println(result);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
