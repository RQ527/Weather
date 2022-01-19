package com.example.weather.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.weather.bean.Weather;

import java.util.List;

/**
 * ...
 * 数据库操作接口
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/16
 */
@Dao
public interface WeatherDao {

    @Insert
    void insertWeather(Weather... weathers);

    @Query("SELECT * FROM weather WHERE city = :city")
    Weather getWeatherByCity(String city);

    @Query("SELECT * FROM weather")
    List<Weather> getAllWeathers();

    @Query("DELETE FROM weather WHERE city = :city")
    void deleteByCity(String city);





}
