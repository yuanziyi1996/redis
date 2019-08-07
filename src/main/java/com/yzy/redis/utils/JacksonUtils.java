package com.yzy.redis.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.codec.JsonJacksonCodec;

public class JacksonUtils {
  private final static ObjectMapper objMapper = new ObjectMapper();

  private final static JsonJacksonCodec jsonJacksonCodec = new JsonJacksonCodec(
    JacksonUtils.getObjMapper());

  static {
    objMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
    objMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
    objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  public static JsonJacksonCodec getJsonJacksonCodec() {
    return jsonJacksonCodec;
  }

  public static ObjectMapper getObjMapper() {
    return objMapper;
  }
}
