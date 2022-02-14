package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.weather.base.BaseActivity;
import com.example.weather.bean.Weather;
import com.example.weather.room.IDispose;
import com.example.weather.room.MyDataBase;
import com.example.weather.room.WeatherDao;
import com.example.weather.utils.NetUtils;
import com.example.weather.utils.RoomUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoadingActivity extends BaseActivity {

    private static final String TAG = "RQ";
    private MyDataBase myDataBase;

    private WeatherDao weatherDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        myDataBase = MyDataBase.getInstance(this);
        weatherDao = myDataBase.getWeatherDao();

        new Thread(() -> {
            update();

            RoomUtils.queryAll(new IDispose() {
                @Override
                public void runOnUi(Weather weather) {

                }

                @Override
                public void runOnUi(List<Weather> weathers) {
                    if (weathers.size() == 0) {
                        Intent intent = new Intent(LoadingActivity.this, AddWeatherActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            }, weatherDao);
        }).start();

    }

    private void update() {
        RoomUtils.queryAll(new IDispose() {
            @Override
            public void runOnUi(Weather weather) {

            }

            @Override
            public void runOnUi(List<Weather> weathers) throws Exception {
                for (Weather weather : weathers) {

                    NetUtils.sendRequest("https://v2.alapi.cn/api/tianqi", "POST", "city", weather.getCity()
                            , new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        String json = response.body().string();
                                        Log.d(TAG, "onResponse: "+json);
                                        Gson gson = new Gson();
                                        Weather weather2 = gson.fromJson(json, Weather.class);
                                        weather2.setCity(weather.getCity());
                                        RoomUtils.update(weatherDao, weather2, weather2.getCity());
                                    }
                                }
                            });
                    Thread.sleep(500);
                }
            }
        }, weatherDao);

    }

}