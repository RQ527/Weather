package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.weather.adapter.FragmentPagerAdapter;
import com.example.weather.base.BaseActivity;
import com.example.weather.bean.Weather;
import com.example.weather.room.IDispose;
import com.example.weather.room.MyDataBase;
import com.example.weather.room.WeatherDao;
import com.example.weather.utils.NetUtils;
import com.example.weather.utils.RoomUtils;
import com.example.weather.view.HomeFragment;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "RQ";
    private static Context context;//全局获取上下文
    private ViewPager2 mViewPager;
    private List<HomeFragment> fragments;
    private MyDataBase myDataBase;
    private WeatherDao weatherDao;
    private Button mButton;
    private ConstraintLayout background;
    private SwipeRefreshLayout refreshLayout;
    private FragmentStateAdapter adapter;
    private LinearLayout pointLinearLayout;
    private int prePosition = 0;
    private int mState = -1;

    public int getState() {
        return mState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initView();

    }

    private void initView() {
        pointLinearLayout = findViewById(R.id.ll_home_point);
        context = getApplicationContext();
        myDataBase = MyDataBase.getInstance(this);
        weatherDao = myDataBase.getWeatherDao();
        mViewPager = findViewById(R.id.vp_home_fragment);
        mViewPager.setOffscreenPageLimit(10);
        refreshLayout = findViewById(R.id.srl_main_refresh);
        refreshLayout.setColorSchemeResources(R.color.blue);
        refreshLayout.setOnRefreshListener(() -> {
            refresh();
        });
        mButton = findViewById(R.id.bt_toolbar_city);
        mButton.setOnClickListener(this);
        fragments = new ArrayList<>();

        background = findViewById(R.id.cl_activity_main_bg);


        RoomUtils.queryAll(new IDispose() {
            @Override
            public void runOnUi(Weather weather) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("RestrictedApi")
            @Override
            public void runOnUi(List<Weather> weathers) {
                for (int i =0;i<weathers.size();i++){
                    Weather weather = weathers.get(i);
                    HomeFragment fragment = new HomeFragment();
                    fragment.setWeather(weather);
                    fragments.add(fragment);

                    ImageView point = new ImageView(MainActivity.this);
                    point.setBackgroundResource(R.drawable.point_selector);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10,10);

                    if (i==0){
                        point.setEnabled(true);
                    }else {
                        point.setEnabled(false);
                        params.leftMargin = 10;
                    }


                    point.setLayoutParams(params);

                    pointLinearLayout.addView(point);
                }
                adapter = new FragmentPagerAdapter(MainActivity.this,fragments);
                mViewPager.setAdapter(adapter);
                mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        pointLinearLayout.getChildAt(prePosition).setEnabled(false);
                        pointLinearLayout.getChildAt(position).setEnabled(true);
                        prePosition = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                        mState = state;
                        if (state==ViewPager2.SCROLL_STATE_DRAGGING){
                            runOnUiThread(() -> refreshLayout.setEnabled(false));

                        }
                        if (state==ViewPager2.SCROLL_STATE_SETTLING||state==ViewPager2.SCROLL_STATE_IDLE){
                            runOnUiThread(() -> refreshLayout.setEnabled(true));

                        }
                    }
                });

            }
        },weatherDao);


    }

    private void refresh() {
        new Thread(() -> {
            for (int i = 0;i<fragments.size();i++){
                try {
                    upDateWeather(fragments.get(i).getWeather().getCity(), i);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            refreshLayout.setRefreshing(false);

        }).start();
    }

    private void upDateWeather(String city, int position) throws Exception {
        NetUtils.sendRequest("https://v2.alapi.cn/api/tianqi", "POST", "city",city
                , new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Gson gson = new Gson();
                            Weather weather2 = gson.fromJson(response.body().string(), Weather.class);
                            weather2.setCity(fragments.get(position).getWeather().getCity());

                        runOnUiThread(() -> {
                            Log.d(TAG, "run: "+weather2.getCity());
                            fragments.get(position).setData(weather2);
                        });
                        }
                    }
                });

    }


    public static Context getContext() {
        return context;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_toolbar_city:
                Intent intent = new Intent(MainActivity.this,ManageActivity.class);
                startActivityForResult(intent,0);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String position = getIntent().getStringExtra("position");
        getIntent().removeExtra("position");
        Log.d(TAG, "onResume: "+position);
        if (position != null) {
            Integer index = Integer.valueOf(position);
            mViewPager.postDelayed(() -> mViewPager.setCurrentItem(index), 10);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0:
            if (data != null) {
                String position = data.getStringExtra("position");
                String position2 = data.getStringExtra("position2");
                if (position != null || position2 != null) {
//                    index = Integer.valueOf(position);
//                    if (index >= fragments.size()) {
//                        Log.d(TAG, "onActivityResult: "+"yyyy");
//                        adapter.notifyDataSetChanged();
//                    }
//                    mViewPager.postDelayed(() -> mViewPager.setCurrentItem(index), 10);
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    if (position != null) {
                        intent.putExtra("position", position);
                    }
                    startActivity(intent);
                    finish();
                }

            }
            break;


        }
    }
}




