package com.huyiyu.pbac.gateway.domain;

import com.huyiyu.pbac.core.exception.BusiPbacException;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class R<T> {

  private static final int SUCCESS_CODE = 0;
  private static final int DEFAULT_FAILURE_CODE = -1;
  private static final String SUCCESS_MSG = "success";

  private T data;
  private String msg;
  private int code;

  public static <T> R<T> ok(T data) {
    return new R<T>()
        .setCode(SUCCESS_CODE)
        .setMsg(SUCCESS_MSG)
        .setData(data);
  }

  public static <T> R<T> fail(String msg) {
    return fail(DEFAULT_FAILURE_CODE, msg);
  }

  public static <T> R<T> fail(int code, String msg) {
    return new R<T>()
        .setCode(code)
        .setMsg(msg);
  }

  public static <T> R<T> ok() {
    return ok(null);
  }


  public T orElseThrowException(){
    if (code==SUCCESS_CODE){
      return data;
    }else {
      throw new RuntimeException("请求出错");
    }
  }

}
