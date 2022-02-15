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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
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
    private ViewPager2 mViewPager;
    private List<HomeFragment> fragments;//viewpager的数据源
    private MyDataBase myDataBase;
    private WeatherDao weatherDao;
    private Button addCityButton;
    private SwipeRefreshLayout refreshLayout;
    private FragmentStateAdapter adapter;
    private LinearLayout pointLinearLayout;//tabLayout的指示点
    @SuppressLint("StaticFieldLeak")
    private static Context context;//全局获取上下文
    private int prePosition = 0;//上一页的位置
    private int mState = -1;//viewpager的状态值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //隐藏actionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();
    }
    //供fragment获取viewpager的状态
    public int getState() {
        return mState;
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
        addCityButton = findViewById(R.id.bt_toolbar_city);
        addCityButton.setOnClickListener(this);
        fragments = new ArrayList<>();
        //为fragment加载数据
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
                    //添加指示点
                    ImageView point = new ImageView(MainActivity.this);
                    point.setBackgroundResource(R.drawable.point_selector);
                    //设置每个指示点的大小
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10,10);
                    //设置第一个高亮
                    if (i==0){
                        point.setEnabled(true);
                    }else {
                        point.setEnabled(false);
                        //设置point的间距
                        params.leftMargin = 10;
                    }


                    point.setLayoutParams(params);

                    pointLinearLayout.addView(point);
                }
                adapter = new FragmentPagerAdapter(MainActivity.this,fragments);
                mViewPager.setAdapter(adapter);
                //viewpager设置监听
                mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        //设置当前页面的tabLayout高亮
                        pointLinearLayout.getChildAt(prePosition).setEnabled(false);
                        pointLinearLayout.getChildAt(position).setEnabled(true);
                        prePosition = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);
                        mState = state;
                        //处理refreshLayout与viewpager的滑动冲突
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

    /**
     * 刷新
     */
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
            runOnUiThread(()->Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_SHORT).show());
        }).start();
    }

    /**
     * 更新weather数据
     * @param city 城市
     * @param position 位置
     * @throws Exception
     */
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
                        //在主线程更新UI数据
                        runOnUiThread(() -> {
                            Log.d(TAG, "run: "+weather2.getCity());
                            fragments.get(position).setData(weather2);
                        });
                        }
                    }
                });

    }

    /**
     * 全局获取context
     * @return
     */
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

    /**
     * 当切换至当前activity时，判断是否有其他activity传递数据过来
     */
    @Override
    protected void onResume() {
        super.onResume();
        //获取intent的数据
        String position = getIntent().getStringExtra("position");
        //用过得数据当然要扔掉
        getIntent().removeExtra("position");
        if (position != null) {
            Integer index = Integer.valueOf(position);
            //viewpager的跳转
            mViewPager.postDelayed(() -> mViewPager.setCurrentItem(index), 10);
        }
    }

    /**
     * 这是当其他activity销毁时启动的方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 0:
            if (data != null) {
                //获取点击item和删除item的数据
                String position = data.getStringExtra("position");
                String position2 = data.getStringExtra("position2");
                if (position != null || position2 != null) {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    //如果是点击则跳转
                    if (position != null) {
                        intent.putExtra("position", position);
                    }
                    //如果删除，则刷新
                    startActivity(intent);
                    finish();
                }

            }
            break;

        }
    }
}




