package com.yzy.redis;

import com.yzy.redis.redis.redisClient.RedisClientService;
import io.lettuce.core.ScriptOutputType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import springfox.documentation.annotations.Cacheable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RedisApplicationTests {

  @Test
  public void contextLoads() {
  }
  @Autowired
  RedisClientService redisClientService;

  @Test
  public void redisTest(){

    String value = redisClientService.get("redissonlocktest_testkey");//"\"ajkdfh\""

    String key = "aa";
    boolean flag = redisClientService.isExist(key);
    if(!flag){
      redisClientService.set(key,"key's value");
    }
    System.out.println(value);
    System.out.println("是否存在："+flag);
  }

  public String setAndGetAStringValue(){
    String key  = "String_key";
    redisClientService.setex(key,"String_key's value",-1);
    System.out.println("value : "+redisClientService.get(key));
    return key;
  }
  public String setAndGetAHashValue(){
    String hset_key = "hset_key";
    String field = "field1";
    String value2 = redisClientService.hget(hset_key,field);
    System.out.println("value : "+value2);
    return hset_key;
  }


  @Test
  public void getLockTest() throws InterruptedException{

/*
    这个key 为什么不能再redis 库里有key 记住redis缓存里不能有这个key
    造成这种情况的原因：redisson 底层的getLOKC 使用的是 hset hash 来实现的
    hset 的value是有 field 和value 两个属性构成。
    而普通的rediss.getLock(一般是用jedis）封装的。jedis底层的getLock使用
    String 来构成的，就只有一个value属性
*/

      //value 是 String 类型的会报错
//    String key = setAndGetAStringValue();

      // value 是 hsah 类型的是正常的。
//      String key = setAndGetAHashValue();

      //这个key在redis中本就不存在
      String key ="this_key_not_in_redis";

      for (int i=0;i<3;i++){
        new Thread(new Runnable() {
          @Override
          public void run() {
            // 1. 最常见的使用方法
            // lock.lock();
            // 2. 支持过期解锁功能,10秒钟以后自动解锁, 无需调用unlock方法手动解锁
            // lock.lock(10, TimeUnit.SECONDS);
            // 3. 尝试加锁，最多等待3秒，上锁以后10秒自动解锁

            /*
             * 针对以下tryLock 的理解。有多个线程同时访问一个key资源，等待时间为6秒
             * 解锁key的时间为3秒。三个线程同时访问，第一个拿到资源并上锁的时间是很快的，
             * 这个线程拿到锁以后3秒释放，接下来可以说是第二个线程会立即拿到该资源，因为他的等待时间
             * 一直有，等到前一个线程把资源释放了，他就立马获取到这个资源。
             * 注意这里还有一个问题，我让主线程sleep 了 10000 ms 就是为了让这三个线程
             * 在主线程还没结束的时间内 都有机会去获取这个资源。要是主线程提前结束了，那么
             * 还在等待的线程就不会继续等待，直接死掉。
             *
             * */
            try {
              long startTime = System.currentTimeMillis();
              System.out.println(Thread.currentThread().getName()+"线程开始时间"+startTime);
              RLock lock =  redisClientService.getRLock(key);
              boolean res = lock.tryLock(6, 3, TimeUnit.SECONDS);
              if(res){
                System.out.println(Thread.currentThread().getName()+"线程获取锁成功");
                //获得锁，执行业务
              }else {
                System.out.println(Thread.currentThread().getName()+"线程获取锁失败");
              }
              System.out.println(Thread.currentThread().getName()
                +"线程总共耗时"+(System.currentTimeMillis()-startTime));

            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }).start();
      }
    //过期自动解锁,无需手动解锁
    //lock.unlock();
    long mainThreadStart = System.currentTimeMillis();
    System.out.println("这里是主线程开始： ===>>"+Thread.currentThread().getName()
      +"开始时间"+mainThreadStart);
    //让主线程睡一下，使得每个线程都可能去请求锁，避免主线程提前结束，三个线程都还有 没请求过锁的
    //情况发生
    Thread.sleep(10000);
    System.out.println("主线程在这里结束： <<==="+Thread.currentThread().getName()
      +"用时"+(System.currentTimeMillis()-mainThreadStart));
  }

  @Test
  public void setex_Test(){
    String key = "setex";
    String value = "setex_value";
    boolean flag = redisClientService.setex(key,value,20);
    System.out.println(flag);
  }


  @Test
  public void setnx_Test(){
    String key = "setnx";
    String value = "setnx_value";
    boolean flag = redisClientService.setnx(key,value);
    System.out.println(flag);
  }

  @Test
  public void kktEST(){
    boolean a = false;
    boolean b = true;
    boolean c = false;
    if(a || b && c){
      System.out.println("in");
    }else {
      System.out.println("pout");
    }

  }

  @Test
  public void AsyncReentrantLockTest() {
    RLock lock = redisClientService.getRLock("anyLock");
    try{
      lock.lockAsync();
      lock.lockAsync(10, TimeUnit.SECONDS);
      Future<Boolean> res = lock.tryLockAsync(3, 10, TimeUnit.SECONDS);
      if(res.get()){
        System.out.println("这个是获取到锁之后，异步调用的方法");
        // do your business
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
  }


}
