package com.yzy.redis.JackSon;


import com.google.common.base.Throwables;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;


import com.yzy.redis.utils.CollectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * copy com.dialer.server.util.JacksonUtils
 */
public class JacksonUtil {

  private final static ObjectMapper objMapper = new ObjectMapper();

  static {
  }

  public static Object json2Object(String json, Class cls) throws IOException {
    ObjectMapper om = new ObjectMapper();
    Object obj = om.readValue(json, cls);
    return obj;
  }

  /**
   * 调用get方法生成json字符串 <br>
   * 2015年1月27日:下午12:26:55<br>
   * <br>
   */
  public static String toJson(Object obj) {
    try {
      return objMapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * 转换成jsonnode
   */
  public static JsonNode toJsonNode(String jsonText) {
    JsonNode jsonNode = null;
    try {
      jsonNode = objMapper.readTree(jsonText);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    return jsonNode;
  }

  public static Map<String, Object> toMap(Object obj) {
    try {
      if (null == obj) {
        return new HashMap<String, Object>();
      }
      String jsonStr = objMapper.writeValueAsString(obj);
      Map<String, Object> map = objMapper.readValue(jsonStr, Map.class);
      return map;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public static String getNodeText(JsonNode node, String path) {
    return node.has(path) ? node.get(path).asText().trim() : StringUtils.EMPTY;
  }

  public static List<String> getJsonStrListFromJson(String json) throws IOException {
    JsonNode arrayNode = objMapper.readTree(json);
    if (arrayNode.isArray()) {
      List<String> resultList = new ArrayList<>();
      for (JsonNode jsonNode : arrayNode) {
        resultList.add(jsonNode.toString());
      }
      return resultList;
    } else {
      return Collections.emptyList();
    }
  }

  public static void main(String[] args) {
    String jsonArrayStr = "[{\"dayOfWeek\":\"Sunday\",\"startTime\":32400000,\"endTime\":61200000,\"checked\":false}," +
      "{\"dayOfWeek\":\"Monday\",\"startTime\":32400000,\"endTime\":61200000,\"checked\":true}," +
      "{\"dayOfWeek\":\"Tuesday\",\"startTime\":32400000,\"endTime\":61200000,\"checked\":true}," +
      "{\"dayOfWeek\":\"Wednesday\",\"startTime\":32400000,\"endTime\":61200000,\"checked\":true}," +
      "{\"dayOfWeek\":\"Thursday\",\"startTime\":32400000,\"endTime\":61200000,\"checked\":true}," +
      "{\"dayOfWeek\":\"Friday\",\"startTime\":32400000,\"endTime\":61200000,\"checked\":true}," +
      "{\"dayOfWeek\":\"Saturday\",\"startTime\":32400000,\"endTime\":61200000,\"checked\":false}]";

    try {
      List<String> jsonNodeStrList = JacksonUtil.getJsonStrListFromJson(jsonArrayStr);
      System.out.println(jsonNodeStrList);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 对obj对象进行序列话，序列化是依据jsonViewClazz的配置
   */
  public static <T> String toJson(Object obj, Class<T> jsonViewClazz) {
    try {
      return objMapper.writerWithView(jsonViewClazz).writeValueAsString(
        obj);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 利用Jackson序列化时，指定clazz类型及其需要包含的输出属性
   *
   * @param obj           需要序列化的对象
   * @param clazz         指定的类型
   * @param includeFileds 指定的需要输出的属性
   */
  @SuppressWarnings("rawtypes")
  public static String toJsonWithInclude(Object obj, Class clazz,
                                         String... includeFileds) {
    Map<Class, Set<String>> include = new HashMap<>();
    include.put(clazz, new HashSet<>(Arrays.asList(includeFileds)));
    return toJson(obj, include, null);
  }


  /**
   * 利用Jackson序列化时，指定多个类型及其需要输出的属性
   *
   * @param obj     需要序列化的对象
   * @param include 每种类型对应的需要输出的属性
   */
  @SuppressWarnings("rawtypes")
  public static String toJsonWithInclude(Object obj,
                                         Map<Class, Set<String>> include) {
    return toJson(obj, include, null);
  }


  /**
   * 利用Jackson序列化时，指定clazz代表的类型需要过滤掉得属性
   *
   * @param obj           需要序列化的对象
   * @param clazz         指定的类型
   * @param excludeFields 需要排除掉得字段
   */
  @SuppressWarnings("rawtypes")
  public static String toJsonWithExclude(Object obj, Class clazz,
                                         String... excludeFields) {
    Map<Class, Set<String>> exclude = new HashMap<Class, Set<String>>();
    exclude.put(clazz, new HashSet<String>(Arrays.asList(excludeFields)));
    return toJson(obj, null, exclude);
  }

  /**
   * 利用Jackson序列化时，指定clazz代表的类型需要过滤掉得属性
   *
   * @param obj           需要序列化的对象
   * @param clazz         指定的类型
   * @param excludeFields 需要排除掉得字段,传入Set<String>
   */
  @SuppressWarnings("rawtypes")
  public static String toJsonWithExcludeSet(Object obj, Class clazz,
                                            Set<String> excludeFields) {
    Map<Class, Set<String>> exclude = new HashMap<Class, Set<String>>();
    exclude.put(clazz, excludeFields);
    return toJson(obj, null, exclude);
  }


  /**
   * 利用Jackson序列化时，指定需要多个类型及其需要过滤掉得属性
   *
   * @param obj     序列化的对象
   * @param exclude 每种类型对应的需要排除的属性
   */
  @SuppressWarnings("rawtypes")
  public static String toJsonWithExclude(Object obj,
                                         Map<Class, Set<String>> exclude) {
    return toJson(obj, null, exclude);
  }

  /**
   * 利用Jackson序列化时，指定各种类型及其对应的过滤条件<br> <br> include exclude可以其中之一为空或者同时为空<br>
   * include为空只过滤exclude<br> exclude为空，只根据include的配置输出字段<br> 同时为空时不进行过滤
   *
   * @param obj     需要序列化的对象
   * @param include 指定class序列化时需要包含的属性
   * @param exclude 指定class序列化时需要排除的属性
   * @return 根据include exclude进行属性的过滤后的对象生成的json 串
   */
  @SuppressWarnings({"serial", "rawtypes"})
  public static String toJson(Object obj, Map<Class, Set<String>> include,
                              Map<Class, Set<String>> exclude) {

    if ((null == include || include.isEmpty())
      && (null == exclude || exclude.isEmpty())) {
      toJson(obj);
    }

    ObjectMapper mapper = new ObjectMapper();

    // 设置包含过滤器
    FilterProvider filters = new SimpleFilterProvider();
    if (null != include && !include.isEmpty()) {
      for (Map.Entry<Class, Set<String>> entry : include.entrySet()) {
        Class clazz = entry.getKey();
        Set<String> includeFileds = entry.getValue();
        ((SimpleFilterProvider) filters).addFilter(clazz.getName(),
          SimpleBeanPropertyFilter.filterOutAllExcept(includeFileds));
      }
    }

    // 设置排除过滤器
    if (null != exclude && !exclude.isEmpty()) {
      for (Map.Entry<Class, Set<String>> entry : exclude.entrySet()) {
        Class clazz = entry.getKey();
        Set<String> excludeFileds = entry.getValue();
        ((SimpleFilterProvider) filters).addFilter(clazz.getName(),
          SimpleBeanPropertyFilter.serializeAllExcept(excludeFileds));
      }
    }
    mapper.setFilterProvider(filters);

    // 都是有哪些过滤器名
    final Set<String> filterNames = new HashSet<String>();
    if (null != include && !include.isEmpty()) {
      for (Class clazz : include.keySet()) {
        filterNames.add(clazz.getName());
      }
    }
    if (null != exclude && !exclude.isEmpty()) {
      for (Class clazz : exclude.keySet()) {
        filterNames.add(clazz.getName());
      }
    }

    mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
      @Override
      public Object findFilterId(Annotated ac) {
        String name = ac.getName();
        if (filterNames.contains(name)) {
          return name;
        } else {
          return null;
        }
      }
    });

    try {
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      return null;
    }
  }


  /**
   * 从json字符串中获取指定key的node
   */
  public static JsonNode findJsonNodeByKey(String jsonText, String key) {

    List<JsonNode> jsonNodes = findJsonNodesByKey(jsonText, key);
    return CollectionUtil.isNotEmpty(jsonNodes) ? jsonNodes.get(0) : null;
  }

  public static List<JsonNode> findJsonNodesByKey(String jsonText, String key) {

    JsonNode jsonNode = toJsonNode(jsonText);
    return Optional.ofNullable(jsonNode).map(o -> o.findValues(key))
      .orElseGet(() -> Collections.emptyList());
  }

  /**
   * 转换json为clazz. <br> <strong>依赖get和set方法</strong> <br> 2015年1月27日:下午12:26:18<br> <br>
   */
  public static <T> T fromJson(String jsonText, Class<T> clazz) throws
    Exception {
    if (jsonText == null || "".equals(jsonText)) {
      return null;
    }
    return objMapper.readValue(jsonText, clazz);
  }

  /**
   * 转换为集合类型的对象集合 <strong>依赖get和set方法</strong> <br> 2015年3月10日:上午11:19:14<br> <br>
   * <strong>example:</strong>
   *
   * <pre>
   * JacksonUtils.fromJson(jsonText, new TypeReference&lt;List&lt;FeedImage&gt;&gt;() {
   * });
   * </pre>
   *
   * @param valueTypeRef org.codehaus.jackson.type.TypeReference
   */
  public static <T> T fromJson(String jsonText, TypeReference<T> valueTypeRef)
    throws IOException {
    if (jsonText == null || "".equals(jsonText)) {
      return null;
    }
    return objMapper.readValue(jsonText, valueTypeRef);
  }

  public static <T> List<T> fromJson2List(String jsonText, Class<T> clazz)
    throws IOException {
    if (jsonText == null || "".equals(jsonText)) {
      return null;
    }
    List<T> objList = null;
    JavaType t = objMapper.getTypeFactory().constructParametricType(
      List.class, clazz);
    objList = objMapper.readValue(jsonText, t);
    return objList;
  }

  public static boolean isJson(String jsonText) {
    if (StringUtils.isEmpty(jsonText)) {
      return false;
    }
    try {
      objMapper.readTree(jsonText);
      return true;
    } catch (Exception e) {
      return false;
    }
  }


  private JacksonUtil() {
  }
}

