package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.weather.adapter.FragmentPagerAdapter;
import com.example.weather.base.BaseActivity;
import com.example.weather.bean.Weather;
import com.example.weather.room.IDispose;
import com.example.weather.room.MyDataBase;
import com.example.weather.room.WeatherDao;
import com.example.weather.utils.RoomUtils;
import com.example.weather.view.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RQ";
    private static Context context;//全局获取上下文
    private ViewPager2 mViewPager;
    private List<HomeFragment> fragments;
    private MyDataBase myDataBase;
    private WeatherDao weatherDao;
    private Button mButton;
    private LinearLayout mLinearLayout;

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
//        init();
        initView();
//        Weather weather = (Weather) getIntent().getSerializableExtra("weather");
//        List<Weather> weathers = (List<Weather>) getIntent().getSerializableExtra("weathers");
//        Log.d(TAG, "onCreate: weather:"+weather);
//        Log.d(TAG, "onCreate: weathers:"+weathers);
//        if (weather!=null){
//            myDiagram.setWeather(weather);
//            myDiagram.initWeather();
//            myDiagram.initPath();
//            myDiagram.invalidate();
//        }else if (weathers!=null){
//            myDiagram.setWeather(weathers.get(0));
//            myDiagram.initWeather();
//            myDiagram.initPath();
//            myDiagram.invalidate();
//        }
        RoomUtils.queryAll(new IDispose() {
            @Override
            public void runOnUi(Weather weather) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("RestrictedApi")
            @Override
            public void runOnUi(List<Weather> weathers) {
                for (Weather weather:weathers){
                    HomeFragment fragment = new HomeFragment();
                    fragment.setWeather(weather);
                    fragments.add(fragment);
                }
                FragmentStateAdapter adapter = new FragmentPagerAdapter(MainActivity.this,fragments);
                mViewPager.setAdapter(adapter);

            }
        },weatherDao);

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        return true;
    }

    private void initView() {
        mViewPager = findViewById(R.id.vp_home_fragment);

        mButton = findViewById(R.id.bt_toolbar_city);
        mButton.setOnClickListener(this);
        fragments = new ArrayList<>();
        mLinearLayout = findViewById(R.id.ll_home_weatherBackground);

    }


    public static Context getContext() {
        return context;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_toolbar_city:
                Intent intent = new Intent(MainActivity.this,ManageActivity.class);
                startActivity(intent);
                break;
        }
    }





}




