package com.example.weather.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weather.R;
import com.example.weather.bean.Weather;


public class HomeFragment extends Fragment {

    private MyDiagram mMyDiagram;
    private TextView pmTextView;
    private TextView airTextView;
    private TextView tempTextView;
    private TextView visibilityTextView;
    private TextView weatherTextView;
    private TextView minToMaxTextView;
    private Weather weather;

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

    private void setData() {
        if (weather!=null){
            mMyDiagram.setWeather(weather);
            pmTextView.setText(weather.getData().getAir_pm25());
            airTextView.setText(weather.getData().getAir());
            tempTextView.setText(weather.getData().getTemp());
            visibilityTextView.setText(weather.getData().getVisibility());
            weatherTextView.setText(weather.getData().getWeather());
            minToMaxTextView.setText(weather.getData().getMin_temp()+"~"+weather.getData().getMax_temp());
        }
    }

    private void initView(View view) {
        mMyDiagram = view.findViewById(R.id.md_home_show);
        pmTextView = view.findViewById(R.id.tv_home_pm2_5);
        airTextView = view.findViewById(R.id.tv_home_air);
        tempTextView = view.findViewById(R.id.tv_home_temp);
        visibilityTextView = view.findViewById(R.id.tv_home_visibility);
        weatherTextView = view.findViewById(R.id.tv_home_weather);
        minToMaxTextView = view.findViewById(R.id.tv_home_minToMax);
    }
}