package com.example.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.weather.base.BaseActivity;
import com.example.weather.bean.Weather;
import com.example.weather.room.MyDataBase;
import com.example.weather.room.WeatherDao;
import com.example.weather.utils.NetUtils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private EditText editText;
    private Button button;
    private TextView textView;
    private Handler mHandler;
    private WeatherDao weatherDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyDataBase dataBase = MyDataBase.getInstance(this);
        weatherDao = dataBase.getWeatherDao();

        initView();



    }

    private void initView() {
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        mHandler = new MyHandler();

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                try {
                    run();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    private void run() throws Exception {
        NetUtils.sendRequest("https://v2.alapi.cn/api/tianqi", "POST", "city", editText.getText().toString()
                , new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Message message = new Message();
                            message.obj = response.body().string();
                            mHandler.sendMessage(message);
                        }
                    }
                });
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            String responseData = msg.obj.toString();

            Gson gson = new Gson();
            Weather weather = gson.fromJson(responseData,Weather.class);
            weather.setCity(editText.getText().toString());
            weather.setId(0);

            Weather.Data data =  weather.getData();
            String air = data.getAir();
            String city = weather.getCity();
            textView.setText(city);

            new InsertWeather(weatherDao).execute(weather);

        }
    }

    class InsertWeather extends AsyncTask<Weather,Void,Void> {

        private WeatherDao weatherDao;

        public InsertWeather(WeatherDao weatherDao) {
            this.weatherDao = weatherDao;
        }

        @Override
        protected Void doInBackground(Weather... weathers) {
            weatherDao.insertWeather(weathers);
            return null;
        }
    }

    class GetWeather extends AsyncTask<Void,Void,Void>{

        private WeatherDao weatherDao;

        public GetWeather(WeatherDao weatherDao) {
            this.weatherDao = weatherDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            weatherDao.getWeatherByCity("")
            return null;
        }
    }

}
