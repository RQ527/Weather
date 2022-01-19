package com.example.weather.room;

import com.example.weather.bean.Weather;

import java.util.List;

/**
 * ...
 *
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/17
 */
public interface IDispose {
    /**
     * 如果查询的是一个weather则使用此方法
     * @param weather
     */
    void runOnUi(Weather weather);

    /**
     * 如果查询的是多个weather方法则使用此方法
     * @param weathers
     */
    void runOnUi(List<Weather> weathers);
}
