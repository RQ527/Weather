package com.example.weather.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.bean.Weather;
import com.example.weather.utils.SelectUtils;
import com.example.weather.utils.TimeUtils;


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

    public Weather getWeather() {
        return weather;
    }

    private Weather weather;
    private ConstraintLayout mConstraintLayout;
    private LinearLayout visibilityLinearLayout;
    private LinearLayout humidityLinearLayout;
    private LinearLayout pressureLinearLayout;
    private LinearLayout windLinearLayout;
    private LinearLayout background;
    private TextView pressureTextView;
    private TextView humidityTextView;
    private TextView windDirectionTextView;
    private TextView windLevelTextView;
    private TextView visibilityTextView2;
    private TextView wearTextView;
    private TextView lipstickTextView;
    private TextView motionTextView;
    private TextView cleanCarTextView;
    private TextView coldTextView;
    private TextView raysTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        setData(weather);
        return view;

    }


    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    @SuppressLint("SetTextI18n")
    public void setData(Weather weather1) {

        if (weather1 != null && weather1.getData() != null) {
            Log.d(TAG, "setData: " + weather1.getCity());
            mMyDiagram.setWeather(weather1);
            mMyDiagram.initWeather();
            mMyDiagram.initPath();
            mMyDiagram.invalidate();


            String time = weather1.getData().getUpdate_time();
            int minuteTime = TimeUtils.TimeToMinutes(time.substring(11, 16));
            int sunrise = TimeUtils.TimeToMinutes(weather1.getData().getSunrise());
            int sunset = TimeUtils.TimeToMinutes(weather1.getData().getSunset());
            int textColor = Color.WHITE;
            String when = "晚上";
            //判断白天还是晚上用于设置不同的视图
            if (minuteTime >= sunrise && minuteTime <= sunset) {
                when = "白天";
                textColor = Color.BLACK;
            }
            int backgroundPicture = SelectUtils.selectWeatherBackground(weather1.getData().getWeather(), when);

            background.setBackgroundResource(backgroundPicture);
            if (weather1.getData().getAir_pm25() != null) {
                pmTextView.setText(weather1.getData().getAir_pm25());
                airTextView.setText(weather1.getData().getAir());
            }
            tempTextView.setText(weather1.getData().getTemp());
            visibilityTextView.setText(weather1.getData().getVisibility());
            weatherTextView.setText(weather1.getData().getWeather());
            minToMaxTextView.setText(weather1.getData().getMin_temp() + "~" + weather1.getData().getMax_temp() + "℃");

            windDirectionTextView.setText(weather1.getData().getWind());
            windLevelTextView.setText(weather1.getData().getWind_speed());
            visibilityTextView2.setText(weather1.getData().getVisibility());
            pressureTextView.setText(weather1.getData().getPressure() + "hPa");
            humidityTextView.setText(weather1.getData().getHumidity());

            if (weather1.getData().getIndex().getGanmao()!=null) {
                wearTextView.setText(weather1.getData().getIndex().getChuangyi().getLevel());
                lipstickTextView.setText(weather1.getData().getIndex().getHuazhuang().getLevel());
                motionTextView.setText(weather1.getData().getIndex().getYundong().getLevel());
                cleanCarTextView.setText(weather1.getData().getIndex().getXiche().getLevel());
                coldTextView.setText(weather1.getData().getIndex().getGanmao().getLevel());
                raysTextView.setText(weather1.getData().getIndex().getZiwaixian().getLevel());
            }

            windLevelTextView.setTextColor(textColor);
            visibilityTextView2.setTextColor(textColor);
            pressureTextView.setTextColor(textColor);
            humidityTextView.setTextColor(textColor);

            wearTextView.setTextColor(textColor);
            lipstickTextView.setTextColor(textColor);
            motionTextView.setTextColor(textColor);
            cleanCarTextView.setTextColor(textColor);
            coldTextView.setTextColor(textColor);
            raysTextView.setTextColor(textColor);
        }
    }

    private void initView(View view) {
        background = view.findViewById(R.id.ll_fragment_weatherBackground);

        visibilityLinearLayout = view.findViewById(R.id.ll_fragment_visibility);
        humidityLinearLayout = view.findViewById(R.id.ll_fragment_humidity);
        pressureLinearLayout = view.findViewById(R.id.ll_fragment_pressure);
        windLinearLayout = view.findViewById(R.id.ll_fragment_wind);

        windDirectionTextView = view.findViewById(R.id.tv_fragment_windDirection);
        windLevelTextView = view.findViewById(R.id.tv_fragment_windLevel);
        visibilityTextView2 = view.findViewById(R.id.tv_fragment_visibility);
        humidityTextView = view.findViewById(R.id.tv_fragment_humidity);
        pressureTextView = view.findViewById(R.id.tv_fragment_pressure);

        wearTextView = view.findViewById(R.id.tv_cardView_wear);
        lipstickTextView = view.findViewById(R.id.tv_cardView_lipstick);
        motionTextView = view.findViewById(R.id.tv_cardView_motion);
        cleanCarTextView = view.findViewById(R.id.tv_cardView_cleanCar);
        coldTextView = view.findViewById(R.id.tv_cardView_cold);
        raysTextView = view.findViewById(R.id.tv_cardView_rays);

        mMyDiagram = view.findViewById(R.id.md_home_show);
        pmTextView = view.findViewById(R.id.tv_home_pm2_5);
        airTextView = view.findViewById(R.id.tv_home_air);
        tempTextView = view.findViewById(R.id.tv_home_temp);
        visibilityTextView = view.findViewById(R.id.tv_home_visibility);
        weatherTextView = view.findViewById(R.id.tv_home_weather);
        minToMaxTextView = view.findViewById(R.id.tv_home_minToMax);

        mLinearLayout = view.findViewById(R.id.ll_fragment_weatherBackground);
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

        rotate(visibilityLinearLayout);
        rotate(humidityLinearLayout);
        rotate(pressureLinearLayout);
        rotate(windLinearLayout);

    }

    private void rotate(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f);
        animator.setDuration(8000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onResume() {
        super.onResume();

        mScrollView.getViewTreeObserver().addOnScrollChangedListener(
                () -> {
                    MainActivity activity = (MainActivity) getActivity();
                    int state = activity.getState();
                    if (!(state== ViewPager2.SCROLL_STATE_DRAGGING)){
                            getActivity().findViewById(R.id.srl_main_refresh)
                                    .setEnabled(mScrollView.getScrollY() >= -1 && mScrollView.getScrollY() <= 3);
                    }
                });

        ConstraintLayout background2 = getActivity().findViewById(R.id.cl_activity_main_bg);

        if (weather != null) {
            TextView city = getActivity().findViewById(R.id.tv_toolbar_city);
            city.setText(weather.getCity());

            String time = weather.getData().getUpdate_time();
            int minuteTime = TimeUtils.TimeToMinutes(time.substring(11, 16));
            int sunrise = TimeUtils.TimeToMinutes(weather.getData().getSunrise());
            int sunset = TimeUtils.TimeToMinutes(weather.getData().getSunset());
            String when = "晚上";
            //判断白天还是晚上用于设置不同的视图
            if (minuteTime >= sunrise && minuteTime <= sunset) {
                when = "白天";
            }

            if (when.equals("晚上")) {
                background2.setBackgroundResource(R.color.navyBlue);
            } else if (when.equals("白天")) {
                background2.setBackgroundResource(R.drawable.background_gradient);
            }
        }
        int alpha = (int) (255.0 - (float) ((255.0 / 700) * mScrollView.getScrollY()));
        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 255) {
            alpha = 255;
        }
        mLinearLayout.getBackground().setAlpha(alpha);
    }

}