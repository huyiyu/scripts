package com.huyiyu.pbac.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

public class JsonUtil {

  private static final ObjectMapper objectMapper;

  static {
    DateTimeFormatter dateTimeFormat= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    DateTimeFormatter timeFormat= DateTimeFormatter.ofPattern("HH:mm:ss");
    DateTimeFormatter dateForamt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    objectMapper = new Jackson2ObjectMapperBuilder()
        .serializers(new LocalDateSerializer(dateForamt))
        .serializers(new LocalTimeSerializer(timeFormat))
        .serializers(new LocalDateTimeSerializer(dateTimeFormat))
        .deserializers(new LocalDateDeserializer(dateForamt))
        .deserializers(new LocalTimeDeserializer(timeFormat))
        .deserializers(new LocalDateTimeDeserializer(dateTimeFormat))
        .timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
        .createXmlMapper(false)
        .serializationInclusion(Include.NON_ABSENT)
        .build();
  }


  public static String object2Json(Object ok) {
    try {
      return objectMapper.writer()
          .writeValueAsString(ok);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T json2Object(String json, Class<T> clazz) {
    try {
      return objectMapper.reader().readValue(json, clazz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> List<T> json2List(String json, Class<T> stringClass) {
    try {
      return objectMapper.readerForListOf(stringClass).readValue(json);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] object2Byte(Object object) {
    try {
      return objectMapper.writer()
          .writeValueAsBytes(object);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
