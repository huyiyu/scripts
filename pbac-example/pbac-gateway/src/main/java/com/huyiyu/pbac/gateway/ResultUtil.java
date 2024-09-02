package com.huyiyu.pbac.gateway;

import com.huyiyu.pbac.gateway.domain.R;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;

public class ResultUtil {
  public static <T> ParameterizedTypeReference<R<T>> resultType(Class<T> clazz) {
    ParameterizedType parameterizedType = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
        Type[] types = new Type[1];
        types[0] = clazz;
        return types;
      }

      @Override
      public Type getRawType() {
        return R.class;
      }

      @Override
      public Type getOwnerType() {
        return R.class;
      }
    };
    return ParameterizedTypeReference.forType(parameterizedType);
  }
}
