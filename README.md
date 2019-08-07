# redis
   study redis
关于redisson 使用

`RLock lock = redisClientService.getRLock(key);
boolean res = lock.tryLock(3, 30, TimeUnit.SECONDS); `

会报

	`Exception in thread "Thread-3" Exception in thread "Thread-5" Exception in thread "Thread-4" org.redisson.client.RedisException: ERR Error running script (call to f_9401052d872adfd0179ef8c8e8c028512707629a): @user_script:1: WRONGTYPE Operation against a key holding the wrong kind of value . channel: [id: 0x7952380a, L:/127.0.0.1:61935 - R:localhost/127.0.0.1:6379] command: (EVAL), params: [if (redis.call('exists', KEYS[1]) == 0) then redis.call('hset', KEYS[1], ARGV[2], 1); redis.call('pe..., 1, aa, 30000, 1d93b71b-3a0e-47c4-8cb3-538a3bd06104:35]`
	
这个错误。是因为redisson 底层的 lock.getLock 或 lock.tryLock 的加锁方式是 hset 方式不是String 的set 。此时的value 的值是 hash类型。
hash 的set命令是 HSET key field value 他这里有一个 field字段。
而普通的 getlock 是使用的jedis。jedis 在使用getlock 使用的是String类型。

	如果在key已经在reids中存在的情况下
想要在redisson中使用

`RLock lock = redisClientService.getRLock(key);`
`boolean res = lock.tryLock(3, 30, TimeUnit.SECONDS); 
`
不报错的话，可以这样操作。

`String hset_key = "hset_key";`
`redisClientService.hset(hset_key,"field1","value");`
`RLock lock = redisClientService.getRLock(hset_key);`
`boolean res = lock.tryLock(3, 30, TimeUnit.SECONDS);`
