package com.yzy.redis.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DistributedLock {

  /**
   * namespace
   */
  String value() default "";

  /**
   * key 支持从参数中获取
   */
  String key() default "";

  long keepMills() default 30000;

  long sleepMills() default 200;

  long waitMills() default 1000;

  int retryTimes() default 5;

  LockFailAction action() default LockFailAction.CONTINUE;

  enum LockFailAction {GIVE_UP, CONTINUE}
}