package com.example.weather;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.bean.Weather;
import com.example.weather.room.IDispose;
import com.example.weather.room.MyDataBase;
import com.example.weather.room.WeatherDao;
import com.example.weather.utils.RoomUtils;

import java.io.Serializable;
import java.util.List;

public class LoadingActivity extends AppCompatActivity {

    private static final String TAG = "RQ";
    private MyDataBase myDataBase;

    private WeatherDao weatherDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        myDataBase = MyDataBase.getInstance(this);
        weatherDao = myDataBase.getWeatherDao();

        RoomUtils.queryAll(new IDispose() {
            @Override
            public void runOnUi(Weather weather) {

            }

            @Override
            public void runOnUi(List<Weather> weathers) {
                if (weathers.size()==0){
                    Intent intent = new Intent(LoadingActivity.this,AddWeatherActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(LoadingActivity.this,MainActivity.class);
                    intent.putExtra("weathers", (Serializable) weathers);

                    startActivity(intent);
                    finish();
                }
            }
        }, weatherDao);
    }
}