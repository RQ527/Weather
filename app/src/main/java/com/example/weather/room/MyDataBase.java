package com.example.weather.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.weather.bean.Weather;

/**
 * ...
 *
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/16
 */
@Database(entities = {Weather.class},version = 1,exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {

    private static final String DATABASE_NAME = "my_db.db";
    private static MyDataBase mInstance;



    public static synchronized MyDataBase getInstance(Context context){
        if (mInstance==null){
            mInstance = Room.databaseBuilder(context.getApplicationContext(),MyDataBase.class
                    ,DATABASE_NAME).build();
        }
        return mInstance;
    }

    public abstract WeatherDao getWeatherDao();

}
