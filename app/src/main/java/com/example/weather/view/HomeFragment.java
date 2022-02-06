package com.example.weather.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.weather.R;
import com.example.weather.bean.Weather;


public class HomeFragment extends Fragment {

    private static final String TAG = "RQ";
    private MyDiagram mMyDiagram;
    private MyScrollView mScrollView;
    private TextView pmTextView;
    private TextView airTextView;
    private TextView tempTextView;
    private TextView visibilityTextView;
    private TextView weatherTextView;
    private TextView minToMaxTextView;
    private LinearLayout mLinearLayout;
    private Weather weather;
    private ConstraintLayout mConstraintLayout;
    private ImageView pressureImageView;
    private ImageView humidityImageView;
    private ImageView raysImageView;
    private ImageView visibilityImageView;
    private ImageView wind_directionImageView;
    private TextView visibilityTextView2;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        setData();
        return view;

    }


    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        if (weather != null) {
            mMyDiagram.setWeather(weather);
            mMyDiagram.initWeather();
            mMyDiagram.initPath();
            mMyDiagram.invalidate();

            pmTextView.setText(weather.getData().getAir_pm25());
            airTextView.setText(weather.getData().getAir());
            tempTextView.setText(weather.getData().getTemp());
            visibilityTextView.setText(weather.getData().getVisibility());
            weatherTextView.setText(weather.getData().getWeather());
            minToMaxTextView.setText(weather.getData().getMin_temp() + "~" + weather.getData().getMax_temp() + "℃");
        }
    }

    private void initView(View view) {
        pressureImageView = view.findViewById(R.id.iv_fragment_pressure);
        humidityImageView = view.findViewById(R.id.iv_fragment_humidity);
        raysImageView = view.findViewById(R.id.iv_fragment_rays);
        visibilityImageView = view.findViewById(R.id.iv_fragment_visibility);
        wind_directionImageView = view.findViewById(R.id.iv_fragment_wind_direction);

        visibilityTextView2 = view.findViewById(R.id.visibility);

        mMyDiagram = view.findViewById(R.id.md_home_show);
        pmTextView = view.findViewById(R.id.tv_home_pm2_5);
        airTextView = view.findViewById(R.id.tv_home_air);
        tempTextView = view.findViewById(R.id.tv_home_temp);
        visibilityTextView = view.findViewById(R.id.tv_home_visibility);
        weatherTextView = view.findViewById(R.id.tv_home_weather);
        minToMaxTextView = view.findViewById(R.id.tv_home_minToMax);
        mLinearLayout = view.findViewById(R.id.ll_home_weatherBackground);
        mScrollView = view.findViewById(R.id.sc_fragment);
        mScrollView.setScrollListener(new MyScrollView.ScrollListener() {
            @Override
            public void scrollOritention(int l, int t, int oldl, int oldt) {
                int alpha = (int) (255.0 - (float) ((255.0 / 700) * mScrollView.getScrollY()));
                if (alpha < 0) {
                    alpha = 0;
                }
                if (alpha > 255) {
                    alpha = 255;
                }
                mLinearLayout.getBackground().setAlpha(alpha);

            }
        });

        mConstraintLayout = view.findViewById(R.id.cl_fragment);

        ObjectAnimator animator = ObjectAnimator.ofFloat(mConstraintLayout, "rotation", 0f, 360f);
        animator.setDuration(8000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        rotate(pressureImageView);
        rotate(humidityImageView);
        rotate(raysImageView);
        rotate(visibilityImageView);
        rotate(wind_directionImageView);
        rotate(visibilityTextView2);
       /* ObjectAnimator animator2 = ObjectAnimator.ofFloat(mTextView,"rotation",360f,0f);
        animator2.setDuration(10000);
        animator2.start();
        animator2.setRepeatCount(ObjectAnimator.INFINITE);
        animator2.setInterpolator(new LinearInterpolator());*/

    }
    private void rotate(View view){
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f);
        animator.setDuration(8000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

}