package com.yzy.redis.utils;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StreamUtil {

  /**
   * 将ist转化为以子属性为Key（包含去重操作）.
   */
  public static <T, K> Map<K, T> mapByFunction(List<T> list, Function<T, K> keyFunction) {
    return Optional.ofNullable(list)
      .map(listOp -> listOp.parallelStream().filter(StreamUtil.distinctByKey(keyFunction)))
      .map(stream -> stream.collect(Collectors.toMap(keyFunction, value -> value)))
      .orElse(Collections.emptyMap());
  }

  /**
   * 以List中的对象的(非空)子属性为元素，组成新的List.
   */
  public static <T, R> List<R> listByFunction(List<T> list, Function<T, R> function) {
    return Optional.ofNullable(list)
      .map(listOp -> listOp.parallelStream().filter(value ->
        function.apply(value) != null).map(function).collect(Collectors.toList()))
      .orElse(Collections.emptyList());
  }

  public static <T, R> Set<R> setByFunction(Collection<T> list, Function<T, R> function) {
    return Optional.ofNullable(list)
      .map(listOp -> listOp.parallelStream().filter(value ->
        function.apply(value) != null).map(function).collect(Collectors.toSet()))
      .orElse(Collections.emptySet());
  }

  /**
   * 以Function的返回值为Key，为IOStream生成filter的Predicate参数, 去除Key为NUll的元素.
   */
  public static  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  public static <T> Integer count(List<T> list, Predicate<T> predicate) {
    if (CollectionUtil.isNotEmpty(list)) {
      return list.stream()
        .filter(Objects::nonNull)
        .filter(predicate)
        .collect(Collectors.toList())
        .size();
    }
    return 0;
  }

  public static  <T> T getFirst(List<T> list) {
    if (CollectionUtil.isNotEmpty(list)){
      return list.get(0);
    }
    return null;
  }

  public static <T> T findOne(List<T> list, Predicate<T> predicate) {
    T result = null;
    if (CollectionUtil.isNotEmpty(list)) {
      result = list.stream()
        .filter(predicate)
        .findFirst()
        .orElse(null);
    }
    return result;
  }
}

