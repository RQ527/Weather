package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather.bean.Weather;
import com.example.weather.room.MyDataBase;
import com.example.weather.room.WeatherDao;
import com.example.weather.utils.NetUtils;
import com.example.weather.utils.RoomUtils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddWeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditText;
    private Button mButton;
    private MyHandler mHandler;
    private WeatherDao weatherDao;
    private MyDataBase myDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weather);

        initView();
    }

    private void initView() {
        myDataBase = MyDataBase.getInstance(this);
        weatherDao = myDataBase.getWeatherDao();
        mHandler = new MyHandler();
        mEditText = findViewById(R.id.editText2);
        mButton = findViewById(R.id.button2);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button2:
                try {
                    run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    private void run() throws Exception {
        NetUtils.sendRequest("https://v2.alapi.cn/api/tianqi", "POST", "city", mEditText.getText().toString()
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

    /**
     * 自定义Handler处理网络请求结果
     */
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            String responseData = msg.obj.toString();

            Gson gson = new Gson();
            Weather weather = gson.fromJson(responseData, Weather.class);
            weather.setCity(mEditText.getText().toString());
            if (weather != null) {
                RoomUtils.insert(weatherDao, weather);
                Intent intent = new Intent(AddWeatherActivity.this, MainActivity.class);
                intent.putExtra("weather", weather);
                startActivity(intent);
                finish();
            }
        }
    }
}