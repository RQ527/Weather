package com.example.weather;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.example.weather.base.BaseActivity;
import com.example.weather.bean.Weather;
import com.example.weather.room.MyDataBase;
import com.example.weather.room.WeatherDao;
import com.example.weather.utils.NetUtils;
import com.example.weather.utils.RoomUtils;
import com.example.weather.view.MyDiagram;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RQ";
    private EditText editText;
    private Button button;
    private TextView textView;

    private Handler mHandler;
    private static Context context;//全局获取上下文

    private MyDataBase myDataBase;

    private WeatherDao weatherDao;
    private MyDiagram myDiagram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        myDataBase = MyDataBase.getInstance(this);
        weatherDao = myDataBase.getWeatherDao();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        init();
        initView();
        Weather weather = (Weather) getIntent().getSerializableExtra("weather");
        List<Weather> weathers = (List<Weather>) getIntent().getSerializableExtra("weathers");
        Log.d(TAG, "onCreate: weather:"+weather);
        Log.d(TAG, "onCreate: weathers:"+weathers);
        if (weather!=null){
            myDiagram.setWeather(weather);
            myDiagram.initWeather();
            myDiagram.initPath();
            myDiagram.invalidate();
        }else if (weathers!=null){
            myDiagram.setWeather(weathers.get(0));
            myDiagram.initWeather();
            myDiagram.initPath();
            myDiagram.invalidate();
        }


    }

    private void init() {

    }

    public static Context getContext() {
        return context;
    }

    /**
     * 初始化...
     */
    private void initView() {
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        mHandler = new MyHandler();
        myDiagram = findViewById(R.id.mydiagram);
        button.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:

                RoomUtils.delete(weatherDao,"拉萨");
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
            weather.setCity(editText.getText().toString());

            myDiagram.setWeather(weather );


        }
    }


}




