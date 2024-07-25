package com.jiangying.Jyrpc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeGetUtil {
    public static String getTime () {
        // 获取当前时间的毫秒数
        long currentTimeMillis = System.currentTimeMillis();

        // 将毫秒数转换为Date对象
        Date date = new Date(currentTimeMillis);

        // 创建一个SimpleDateFormat对象，指定日期/时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // 格式化Date对象为字符串
        String formattedDate = sdf.format(date);

        return formattedDate;
    }
}