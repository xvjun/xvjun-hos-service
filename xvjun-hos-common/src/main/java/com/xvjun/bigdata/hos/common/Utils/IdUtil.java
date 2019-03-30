package com.xvjun.bigdata.hos.common.Utils;

import java.util.UUID;

public class IdUtil {


  public static String uuid() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
