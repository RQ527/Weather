package com.example.weather.utils;

import com.example.weather.R;

/**
 * ...
 * 根据天气选图片的工具
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/28
 */
public class SelectUtils {
    /**
     * 通过天气转换成图片资源
     * @param weather 天气
     * @param when 白天还是晚上
     * @return
     */
    public static int selectWeatherPicture(String weather,String when){
        switch (weather){
            case "晴":
                if (when.equals("白天")) return R.drawable.sunny1;
                if (when.equals("晚上")) return R.drawable.sunny2;
                break;
            case "阴":
                return R.drawable.overcast;
            case "多云":
                if (when.equals("白天")) return R.drawable.cloudy1;
                if (when.equals("晚上")) return R.drawable.cloudy2;
                break;
            case "大雨":
            case "中雨":
            case "小雨":
            case "暴雨":
                return R.drawable.rainy;
            case "霾":
                return R.drawable.haze;
            case "雾":
                return R.drawable.fog;
            case "雨夹雪":
                return R.drawable.sleet;
            case "小雪":
            case "中雪":
            case "大雪":
            case "暴雪":
                return R.drawable.snowy;

        }
        return R.drawable.test;
    }

    /**
     * 通过天气挑选背景
     * @param weather
     * @param when
     * @return
     */
    public static int selectWeatherBackground(String weather,String when){
        switch (weather){
            case "晴":
                if (when.equals("白天")) return R.drawable.sunny_bg1;
                if (when.equals("晚上")) return R.drawable.sunny_bg2;
            case "阴":
                return R.drawable.overcast_bg;
            case "多云":
                if (when.equals("白天")) return R.drawable.cloudy_bg;
                if (when.equals("晚上")) return R.drawable.cloudy_bg2;
            case "大雨":
            case "中雨":
            case "小雨":
            case "暴雨":
                return R.drawable.rainy_bg;
            case "雾霾":
            case "霾":
                if (when.equals("白天")) return R.drawable.haze_bg;
                if (when.equals("晚上")) return R.drawable.night;
            case "雾":
                if (when.equals("白天")) return R.drawable.fog_bg;
                if (when.equals("晚上")) return R.drawable.night;
            case "雨夹雪":
            case "小雪":
            case "中雪":
            case "大雪":
            case "暴雪":
                return R.drawable.snowy_bg;
        }
        return R.drawable.overcast_bg;
    }
}
