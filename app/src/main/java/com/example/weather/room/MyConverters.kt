package com.example.weather.room

import androidx.room.TypeConverter
import com.example.weather.bean.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * ...
 * 自定义类型转换器
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/17
 */
class MyConverters {
    /**
     * json转换成对象
     */
    @TypeConverter
    fun stringToWeather(value:String):Weather.Data{
        val type = object :TypeToken<Weather.Data>(){

        }.type
        return Gson().fromJson(value,type)
    }

    /**
     * 对象转换成json存进数据库
     */
    @TypeConverter
    fun weatherToString(weather: Weather.Data):String{
        val gson = Gson()
        return gson.toJson(weather)
    }
}