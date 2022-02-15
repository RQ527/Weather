package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class AddWeatherActivity extends BaseActivity implements View.OnClickListener {
    private EditText mEditText;
    private Button addButton;
    private Button backButton;
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
        addButton = findViewById(R.id.bt_addWeather_add);
        backButton = findViewById(R.id.bt_addWeather_back);
        backButton.setOnClickListener(this);
        addButton.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_addWeather_add:
                //防止连击多次添加数据
                addButton.setClickable(false);
                try {
                    RoomUtils.queryAll(new IDispose() {
                        @Override
                        public void runOnUi(Weather weather) {

                        }
                        @Override
                        public void runOnUi(List<Weather> weathers) throws Exception {
                            //是否访问获取数据
                            boolean isRequest = true;
                            if (weathers.size()==8){
                                isRequest = false;
                                Toast.makeText(AddWeatherActivity.this, "城市数量已达上限，请删除一些城市再添加。"
                                        , Toast.LENGTH_SHORT).show();
                                addButton.setClickable(true);
                            }else {
                                //判断是否已存在城市
                                for (Weather weather : weathers) {
                                    if (weather.getCity().equals(mEditText.getText().toString())) {
                                        Toast.makeText(AddWeatherActivity.this, "城市已存在", Toast.LENGTH_SHORT).show();
                                        isRequest = false;
                                        addButton.setClickable(true);
                                        break;
                                    }
                                }
                            }
                                if (isRequest){
                                    request();
                                }
                        }
                    },weatherDao);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bt_addWeather_back:
                finish();
                break;
        }
    }

    /**
     * 访问数据
     * @throws Exception
     */
    private void request() throws Exception {
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
            Weather weather = gson.fromJson(responseData, Weather.class);//剥壳
            if (!(weather.getData().getCity()).equals(mEditText.getText().toString())){
                Toast.makeText(AddWeatherActivity.this, "输入格式错误，请按要求输入。", Toast.LENGTH_SHORT).show();
                addButton.setClickable(true);
            }else {
                weather.setCity(mEditText.getText().toString());
                if (weather != null) {
                    RoomUtils.insert(weatherDao, weather);
                    //判断是从哪个activity跳转过来
                    String flag = getIntent().getStringExtra("flag");
                    if (flag != null) {
                        Intent intent;
                        if (flag.equals("manage")) {
                            Intent intent1 = new Intent();
                            intent1.putExtra("weather",weather);
                            setResult(0,intent1);
                            Toast.makeText(AddWeatherActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            intent = new Intent(AddWeatherActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(AddWeatherActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                    finish();
                }
            }
        }
    }
}