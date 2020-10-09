package com.hotel.cloud.common.utils;

public class CommonUtils {

    public static boolean isEmpty(Object[] arr) {
        return null == arr || arr.length == 0;
    }

    public static boolean isNotEmpty(Object[] arr) {
        return !isEmpty(arr);
    }

}
