package com.yzy.redis.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionUtil {
  private CollectionUtil() {
  }

  public static boolean isEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  public static boolean isNotEmpty(Collection<?> collection) {
    return !isEmpty(collection);
  }

  public static boolean isEmpty(Map<?, ?> map) {
    return map == null || map.isEmpty();
  }

  public static boolean isNotEmpty(Map<?, ?> map) {
    return !isEmpty(map);
  }


  public static boolean isEmpty(Object[] objects) {
    return (objects == null || objects.length == 0);
  }

  public static boolean isNotEmpty(Object[] objects) {
    return !isEmpty(objects);
  }


  public static <T> T getFirst(List<T> values){

    return isNotEmpty(values) ? values.get(0) : null;

  }

  /**
   * 数组转换为List
   */
  public static List<?> arrayToList(Object[] array) {
    if (isEmpty(array)) {
      return Collections.emptyList();
    }
    return Arrays.asList(array);
  }

  /**
   * 集合分割成字符串.
   *
   * @param collections 集合对象
   * @param separator 分隔符
   * @author rui
   */
  public static String split(Collection<?> collections, String separator) {

    Object[] array = collections.toArray(new Object[0]);
    int length = array.length;
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      stringBuilder.append(array[i]);
      if (i != length - 1) {
        stringBuilder.append(separator);
      }
    }
    return stringBuilder.toString();
  }

  /**
   * 获取Collection中的最后一个.
   *
   * @param collection collection
   * @param <T> Class Type
   * @return last element
   */
  public static <T> T getLast(Collection<T> collection) {
    Assert.notEmpty(collection, "Collection has not element, can not find last");

    Iterator<T> iterator = collection.iterator();
    T last = null;
    while (iterator.hasNext()) {
      last = iterator.next();
    }
    return last;
  }

  /**
   * 以对象的某个属性为key进行排序.
   *
   * @author shixinghui
   */
  public static <T, R extends Comparable> List<T> sort(List<T> list, Function<T, R> function) {
    return CollectionUtil.sort(list, function, SortOrder.ASC);
  }

  /**
   * 以对象的某个属性为key进行排序.
   *
   * @param list the list need to be sorted
   * @param function the lambda
   * @param order ASC or DESC
   * @author shixinghui
   */
  @SuppressWarnings("all")
  public static <T, R extends Comparable> List<T> sort(List<T> list, Function<T, R> function,
                                                       SortOrder order) {
    if (!CollectionUtil.isEmpty(list)) {
      return list.parallelStream()
        .filter(StreamUtil.distinctByKey(function))
        .sorted(CollectionUtil.makeComparable(function, order))
        .collect(Collectors.toList());
    }
    return Collections.EMPTY_LIST;
  }

  public static Map<String, String> makeLinkedHashMap(String[] keys, String[] values) throws
    IOException {
    Map<String, String> map = new LinkedHashMap<>(keys.length);
    for (int i = 0; i < keys.length && i < values.length; i++) {
      if (map.containsKey(keys[i])) {
        throw new IOException();
      }
      if (!StringUtils.isEmpty(keys[i])) {
        map.put(keys[i], values[i]);
      }
    }
    return map;
  }

  @SuppressWarnings("unchecked")
  private static <T, R extends Comparable> Comparator<T> makeComparable(
    Function<T, R> function, SortOrder order) {
    Objects.requireNonNull(function, "function is not define");

    return (o1, o2) -> {
      String nullMessage = "the list have null element";
      Objects.requireNonNull(o1, nullMessage);
      Objects.requireNonNull(o2, nullMessage);

      R r1 = function.apply(o1);
      R r2 = function.apply(o2);
      if (order == SortOrder.DESC) {
        return -1 * r1.compareTo(r2);
      } else {
        return r1.compareTo(r2);
      }
    };
  }

  public enum SortOrder {
    /*
    升序.
     */
    ASC,
    /*
    降序.
     */
    DESC
  }

}

