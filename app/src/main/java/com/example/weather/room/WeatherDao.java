package com.example.weather.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weather.bean.Weather;

/**
 * ...
 *
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/16
 */
@Dao
public interface WeatherDao {
    @Insert
    void insertWeather(Weather... weathers);
    @Delete
    void deleteWeather(Weather... weathers);
    @Update
    void upDateWeather(Weather... weathers);
    @Query("SELECT * FROM weather WHERE city = :city")
    Weather getWeatherByCity(String city);


}
