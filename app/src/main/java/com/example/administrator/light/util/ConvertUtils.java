package com.example.administrator.light.util;

/**
 * Created by JO on 2016/5/21.
 */
public class ConvertUtils {

    //Convert
    public static int boolToInt(boolean b) {
        int value;
        if(b) {
            value = 1;
        } else {
            value = 0;
        }
        return value;
    }

}
