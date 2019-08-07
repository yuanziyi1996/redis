package com.yzy.redis;

import java.util.concurrent.TimeUnit;

import com.yzy.redis.utils.RedissLockUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.RedissonMultiLock;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
/**
 * @author libo
 * @ClassName RedissonLockDemo
 * @Description: 分布式锁demo
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class,webEnvironment = SpringBootTest.WebEnvironment.NONE)
@WebAppConfiguration
public class RedissonLockDemo {

  /***
   * @Author libo
   * @Description // Redisson可重入锁测试，实现了java对象的lock接口
   * @return
   */
  @Test
  public void testReentrantLock() throws InterruptedException {
    RLock lock =  RedissLockUtil.getLock("lockKey");
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
    Thread.sleep(10000);
    System.out.println("主线程在这里结束： <<==="+Thread.currentThread().getName()
      +"用时"+(System.currentTimeMillis()-mainThreadStart));
  }
  /***
   * @Author libo
   * @Description // 为分布式锁提供异步执行的方法
   * @return void
   */
  @Test
  public void testAsyncReentrantLock() throws InterruptedException {
    for(int i =0;i<3;i++){
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
//                    lock.tryLockAsync();
//                    //加锁10后自动解锁
//                    lock.tryLockAsync(10, TimeUnit.SECONDS);
//                    lock.tryLockAsync(3, 20, TimeUnit.SECONDS);
            /**
             *
             *    异步锁，就是多个线程，同时访问，谁得到就是谁的。资源只有一个只能
             * 被一个线程获取，其他没有获取到资源的线程就不再获取了；
             *    他跟公平锁的区别是：公平锁一次只有一个线程，谁先获取到资源
             * 那就是谁的，没有获取到的在等待时间内要一直不断尝试获取，知道
             * 等待时间过期或者是获取到资源才结束
             */
            RFuture <Boolean>  res  = RedissLockUtil.tryLockAsync("getLock",3, 4, TimeUnit.SECONDS);
            System.out.println(Thread.currentThread().getName()+":"+res.get());
            if(res.get()){
              System.out.println(Thread.currentThread().getName()+"这个是获取到锁之后，异步调用的方法");
            }
          }catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).start();
    }

    System.out.println("这是主线程的方法");
    Thread.sleep(15000);
    System.out.println("主线程结束");
  }

  /**
   * 公平锁（Fair Lock）
   * Redisson分布式可重入公平锁也是实现了java.util.concurrent.locks.Lock接口的一种RLock对象。
   * 在提供了自动过期解锁功能的同时，保证了当多个Redisson客户端线程同时请求加锁时，优先分配给先发出请求的线程。
   * @param redisson
   */

  @Test
  public void testFairLock() throws InterruptedException {
    RLock fairLock = RedissLockUtil.getFairLock("getLock");
    //执行.start()的顺序 不代表真正的执行顺序。
    new Thread(new MyRunner("线程A",fairLock)).start();
    new Thread(new MyRunner("线程B",fairLock)).start();
    Thread.sleep(11000);
  }

  class  MyRunner implements Runnable{
    String threadName;
    RLock rLock;

    public MyRunner(String threadName,RLock rLock){
      this.threadName = threadName;
      this.rLock = rLock;
    }

    public String getThreadName() {
      return threadName;
    }

    public void setThreadName(String threadName) {
      this.threadName = threadName;
    }

    public RLock getrLock() {
      return rLock;
    }

    public void setrLock(RLock rLock) {
      this.rLock = rLock;
    }
    @Override
    public void run() {
      try{
                /*
                fairLock.lock();
                // 支持过期解锁功能, 10秒钟以后自动解锁,无需调用unlock方法手动解锁
                fairLock.lock(10, TimeUnit.SECONDS);*/
        // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
        boolean res = rLock.tryLock(100, 8, TimeUnit.SECONDS);
        if (res) {
          System.out.println("我获取锁成功 我的线程名称:"+getThreadName());
        }
      }catch (Exception e){
        e.printStackTrace();
      }
    }
  }

  /**
   * 联锁（MultiLock）
   * Redisson的RedissonMultiLock对象可以将多个RLock对象关联为一个联锁，
   * 每个RLock对象实例可以来自于不同的Redisson实例
   */
  @Test
  public void testMultiLock(){
    RLock lock1 = RedissLockUtil.getLock("lock1");
    RLock lock2 = RedissLockUtil.getLock("lock2");
    RLock lock3 = RedissLockUtil.getLock("lock3");
    RedissonMultiLock multiLock = new RedissonMultiLock(lock1,lock2,lock3);
    try{
      //multiLock.lock();
      //multiLock.lock(100,TimeUnit.SECONDS);
      boolean res = multiLock.tryLock(10, 10, TimeUnit.SECONDS);
      if(res){
        System.out.println("这是联锁测试");
      }
    }catch (Exception e){
      System.out.println("获取不到锁，发生异常");
      e.printStackTrace();
    }finally {
      try{
        multiLock.unlock();
      }catch (Exception e){
        System.out.println("获取不到锁，解锁发生异常");
      }
    }
  }

  @Test
  public void testAlwaysLock() throws InterruptedException {
    RLock lock =  RedissLockUtil.getLock("lock1");
    new Thread(new Runnable() {
      @Override
      public void run() {
        // 1. 最常见的使用方法
        // lock.lock();
        // 2. 支持过期解锁功能,10秒钟以后自动解锁, 无需调用unlock方法手动解锁
        // lock.lock(10, TimeUnit.SECONDS);
        // 3. 尝试加锁，最多等待3秒，上锁以后10秒自动解锁
        try {
          boolean res = lock.tryLock();
          if(res){
            System.out.println(Thread.currentThread().getName()+"线程获取锁成功");
            //获得锁，执行业务
          }else {
            System.out.println(Thread.currentThread().getName()+"线程获取锁失败");
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }).start();
    Thread.sleep(1000000);
  }
  /**
   *  红锁（RedLock）
   *  Redisson的RedissonRedLock对象实现了Redlock介绍的加锁算法。
   *  该对象也可以用来将多个RLock对象关联为一个红锁，每个RLock对象实例可以来自于不同的Redisson实例
   */
  @Test
  public void testRedLock(){
    RLock lock1 = RedissLockUtil.getLock("lock1");
    RLock lock2 = RedissLockUtil.getLock("lock2");
    RLock lock3 = RedissLockUtil.getLock("lock3");
    RedissonRedLock lock = new RedissonRedLock(lock1, lock2, lock3);
    try {
      // 同时加锁：lock1 lock2 lock3, 红锁在大部分节点上加锁成功就算成功。
      lock.lock();
      // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
      boolean res = lock.tryLock(10, 10, TimeUnit.SECONDS);
      if (res) {
        System.out.println("这是红锁测试");
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }
  }

  /***
   * @Author libo
   * @Description //读写锁测试
   * @return
   */
  @Test
  public void testRWlock() throws InterruptedException {
    RReadWriteLock rwlock = RedissLockUtil.getReadWriteLock("lockKey");
    for (int i = 0; i < 3; i++) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            boolean redRes = rwlock.readLock().tryLock(5, 10, TimeUnit.SECONDS);
            if(redRes){
              System.out.println(Thread.currentThread().getName()+"线程获取读锁成功");
            }else{
              System.out.println(Thread.currentThread().getName()+"线程获取读锁失败");
            }
            boolean writeRes = rwlock.writeLock().tryLock(10, 10, TimeUnit.SECONDS);
            if (writeRes){
              System.out.println(Thread.currentThread().getName()+"线程获取写锁成功");
            }else {
              System.out.println(Thread.currentThread().getName()+"线程获取写锁失败");
            }
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }).start();
    }
    Thread.sleep(100000);
  }

  /***
   * @Author libo
   * @Description //信号量测试
   * @return
   */
  @Test
  public void testSemaphore() throws InterruptedException {
    RSemaphore semaphore = RedissLockUtil.getSemaphore("semaphore ");
    semaphore.trySetPermits(10);
    Driver driver = new Driver(semaphore);
    for (int i = 0; i < 5; i++) {
      (new Car(driver)).start();
    }
    Thread.sleep(10000);
  }

  /***
   * @Author libo
   * @Description //RCountDownLatch测试
   * @return
   */
  @Test
  public void testCountDownLatch() throws InterruptedException {
    RCountDownLatch countDownlatch = RedissLockUtil.getCountDownLatch("countDownlatch ");
    countDownlatch.trySetCount(5);
    Driver driver = new Driver(countDownlatch);
    for (int i = 0; i < 5; i++) {
      (new Car(driver)).start();
    }
    System.out.println("主线程阻塞,等待所有子线程执行完成");
    //countDownlatch.await()使得主线程阻塞直到countDownlatch.countDown()为零才继续执行
    countDownlatch.await();
    System.out.println("所有线程执行完成!");
    Thread.sleep(10000);
  }


  class Driver {
    private RSemaphore semaphore;
    private RCountDownLatch rCountDownLatch;

    public Driver(RSemaphore semaphore){
      this.semaphore = semaphore;
    }
    public Driver(RCountDownLatch rCountDownLatch){
      this.rCountDownLatch = rCountDownLatch;
    }
    public void driveCar() {
      try {
        // 从信号量中获取一个允许机会
        semaphore.acquire();
        System.out.println(Thread.currentThread().getName() + " start at " + System.currentTimeMillis());
        Thread.sleep(1000);
        System.out.println(Thread.currentThread().getName() + " stop at " + System.currentTimeMillis());
        // 释放允许，将占有的信号量归还
        semaphore.release();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    public void driveCar2() {
      System.out.println( Thread.currentThread().getName() +" start at "+ System.currentTimeMillis());
      // 每个独立子线程执行完后,countDownLatch值减1
      rCountDownLatch.countDown();
    }
  }
  class Car extends Thread{
    private Driver driver;

    public Car(Driver driver) {
      super();
      this.driver = driver;
    }

    public void run() {
      //driver.driveCar();
      driver.driveCar2();
    }
  }
}
