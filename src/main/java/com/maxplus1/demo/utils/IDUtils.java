package com.maxplus1.demo.utils;

import java.util.UUID;

/**
 * Created by xiaolong.qiu on 2016/8/1.
 */
public enum IDUtils {
    ;
    public static String getUuid(){
       String uuid = UUID.randomUUID().toString();
        return uuid.substring(0,8)+uuid.substring(9,13)+uuid.substring(14,18)+uuid.substring(19,23)+uuid.substring(24);
    }
}
