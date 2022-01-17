package com.example.weather.utils;

import android.os.AsyncTask;

import com.example.weather.bean.Weather;
import com.example.weather.room.IDispose;
import com.example.weather.room.WeatherDao;

/**
 * ...
 *
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/17Q2
 */

/**
 * 操作Room的工具
 */
public class RoomUtils {
    /**
     * 增添数据的方法
     * @param weatherDao 用于操作Room的Dao对象
     * @param weather 添加的天气对象
     */
    public static void insert(WeatherDao weatherDao,Weather weather){
        new InsertWeatherTask(weatherDao).execute(weather);
    }

    /**
     * 删除数据的方法
     * @param weatherDao 用于操作Room的Dao对象
     * @param city 删除的天气对象
     */
    public static void delete(WeatherDao weatherDao,String city){
        new DeleteWeatherTask(weatherDao,city).execute();
    }

    /**
     * 查询数据的方法
     * @param dispose 回调查询结果的接口实例
     * @param weatherDao 用于操作Room的Dao对象
     * @param city 查询的城市
     */
    public static void query(IDispose dispose,WeatherDao weatherDao,String city){
        new GetWeatherTask(weatherDao, city, dispose).execute();
    }

    /**
     * 更新数据的方法
     * @param weatherDao 用于操作Room的Dao对象
     * @param weather 新的的天气对象
     */
    public static void update(WeatherDao weatherDao,Weather weather,String city){
        //更新方法有问题，所以我采用删除数据库再添加新数据的方法更新
        delete(weatherDao,city);
        insert(weatherDao,weather);
    }

    //用于查询数据库的异步类
    static class GetWeatherTask extends AsyncTask<Void,Void,Void> {


        private Weather weather;
        private WeatherDao weatherDao;
        private IDispose dispose;
        private String city;

        public GetWeatherTask(WeatherDao weatherDao,String city ,IDispose dispose) {
            this.weatherDao = weatherDao;
            this.dispose = dispose;
            this.city = city;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            weather = weatherDao.getWeatherByCity(city);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            dispose.runOnUi(weather);
            super.onPostExecute(unused);
        }
    }

    //用于添加数据库的异步类
    static class InsertWeatherTask extends AsyncTask<Weather,Void,Void> {

        private WeatherDao weatherDao;

        public InsertWeatherTask(WeatherDao weatherDao) {
            this.weatherDao = weatherDao;
        }

        @Override
        protected Void doInBackground(Weather... weathers) {
            weatherDao.insertWeather(weathers);
            return null;
        }
    }

    //用于删除数据库的异步类
    static class DeleteWeatherTask extends AsyncTask<Void,Void,Void> {

        private WeatherDao weatherDao;
        private String city;

        public DeleteWeatherTask(WeatherDao weatherDao,String city) {
            this.weatherDao = weatherDao;
            this.city = city;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            weatherDao.deleteByCity(city);
            return null;
        }
    }

}
