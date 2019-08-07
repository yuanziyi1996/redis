package com.yzy.redis.lock;


import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import com.yzy.redis.annotation.DistributedLock;
import com.yzy.redis.redis.redisClient.RedisClientService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DistributedLockAspectConfiguration {

  @Autowired
  RedisClientService lucrativRedisClient;

//  @Value("${spring.application.name}")
//  private String applicationName;

  @Pointcut("@annotation(com.yzy.redis.annotation.DistributedLock)")
  private void lockPoint() {

  }

  @Around("lockPoint()")
  public Object around(ProceedingJoinPoint pjp) throws Throwable {
    Method method = ((MethodSignature) pjp.getSignature()).getMethod();
    DistributedLock redisLock = method.getAnnotation(DistributedLock.class);

    String key;
    if (StringUtils.isNotBlank(redisLock.key())) {
      Object o = parseSpel(redisLock.key(), ((MethodSignature) pjp.getSignature()).getMethod(),
        pjp.getArgs());

      key = o.toString();
    } else {
      key = redisLock.value()+"empty";
    }

    RLock rLock = lucrativRedisClient.getRLock(key);
    boolean lock = rLock.tryLock(redisLock.waitMills(), redisLock.keepMills(), TimeUnit
      .MILLISECONDS);
    if (!lock) {
      log.info("get lock failed : " + key);
      throw new Exception();
    } else {
      log.info("lock{}", key);
    }

    //得到锁,执行方法，释放锁
    log.info("get lock success : " + key);
    try {
      return pjp.proceed();
    } catch (Exception e) {
      throw e;
    } finally {
      rLock.unlock();
      boolean releaseResult = rLock.isLocked();
      log.info("release lock : " + key + (releaseResult ? " success" : " failed"));
    }
  }


  /**
   * 解析SpEL表达式
   *
   * @param key SpEL表达式
   * @param method 反射得到的方法
   * @param args 反射得到的方法参数
   * @return 解析后SpEL表达式对应的值
   */
  private Object parseSpel(String key, Method method, Object[] args) {
    // 创建解析器
    ExpressionParser parser = new SpelExpressionParser();
    // 通过Spring的LocalVariableTableParameterNameDiscoverer获取方法参数名列表
    LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
    String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
    // 构造上下文
    EvaluationContext context = new StandardEvaluationContext();
    if (args.length == parameterNames.length) {
      for (int i = 0, len = args.length; i < len; i++) {
        // 使用setVariable方法来注册自定义变量
        context.setVariable(parameterNames[i], args[i]);
      }
    }
    return parser.parseExpression(key).getValue(context);
  }
}

