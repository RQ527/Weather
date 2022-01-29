package com.example.weather.utils;

/**
 * ...
 * 时间转换成秒的工具
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/28
 */
public class TimeUtils {
    /**
     * 时间转换成分钟，用来比较时间大小
     * @param time
     * @return
     */
    public static int TimeToMinutes(String time){
        String hour = time.substring(0, 2);
        String minutes = time.substring(3, 5);
        return Integer.valueOf(hour)*60+Integer.valueOf(minutes);
    }
}
