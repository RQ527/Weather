package com.example.weather.room

import androidx.room.TypeConverter
import com.example.weather.bean.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * ...
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/17
 */
class MyConverters {
    @TypeConverter
    fun stringToWeather(value:String):Weather.Data{
        val type = object :TypeToken<Weather.Data>(){

        }.type
        return Gson().fromJson(value,type)
    }

    @TypeConverter
    fun weatherToString(weather: Weather.Data):String{
        val gson = Gson()
        return gson.toJson(weather)
    }
}