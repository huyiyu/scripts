package com.huyiyu.pbac.core.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtil {

  public static Date onFutureByDuration(Duration duration) {
    return Date.from(LocalDateTime.now()
        .plus(duration)
        .atZone(ZoneOffset.systemDefault())
        .toInstant());
  }

  public static LocalDateTime dateToLocalDateTime(Date date) {
    return date.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDateTime();
  }


  public static boolean isAfter(Date expirationTime, Date date) {
    return dateToLocalDateTime(expirationTime).isAfter(dateToLocalDateTime(date));
  }

  public static boolean isBefore(Date expirationTime, Date date) {
    return dateToLocalDateTime(expirationTime).isBefore(dateToLocalDateTime(date));
  }
}
