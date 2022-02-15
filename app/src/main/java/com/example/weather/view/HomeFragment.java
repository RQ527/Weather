package com.example.weather.view;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.weather.DetailActivity;
import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.bean.Weather;
import com.example.weather.utils.SelectUtils;
import com.example.weather.utils.TimeUtils;


public class HomeFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "RQ";
    /**
     * 都是fragment里面的控件，看名字应该就知道啦，我就不一一注释了。
     */
    private Weather weather;
    private MyDiagram mMyDiagram;
    private MyScrollView mScrollView;
    private CardView adviceCardView;
    private ConstraintLayout mConstraintLayout;
    private ConstraintLayout topConstraintLayout;
    private ImageView backgroundImageView;
    private LinearLayout visibilityLinearLayout;
    private LinearLayout humidityLinearLayout;
    private LinearLayout pressureLinearLayout;
    private LinearLayout windLinearLayout;
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
    private TextView pmTextView;
    private TextView airTextView;
    private TextView tempTextView;
    private TextView visibilityTextView;
    private TextView weatherTextView;
    private TextView minToMaxTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        setData(weather);
        return view;

    }

    /**
     * 设置weather
     * @param weather
     */
    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    /**
     * 供外界获取当前weather
     * @return
     */
    public Weather getWeather() {
        return weather;
    }

    /**
     * 设置ui的数据
     * @param weather1
     */
    @SuppressLint("SetTextI18n")
    public void setData(Weather weather1) {

        if (weather1 != null && weather1.getData() != null) {
            //设置自定义view的数据
            mMyDiagram.setWeather(weather1);
            mMyDiagram.initWeather();
            mMyDiagram.initPath();
            mMyDiagram.invalidate();

            //获取时间
            String time = weather1.getData().getUpdate_time();
            int minuteTime = TimeUtils.TimeToMinutes(time.substring(11, 16));
            int sunrise = TimeUtils.TimeToMinutes(weather1.getData().getSunrise());
            int sunset = TimeUtils.TimeToMinutes(weather1.getData().getSunset());
            int textColor = Color.WHITE;//文本颜色
            String when = "晚上";
            //判断白天还是晚上用于设置不同的视图
            if (minuteTime >= sunrise && minuteTime <= sunset) {
                when = "白天";
                textColor = Color.BLACK;
            }
            //挑选背景图片
            int backgroundPicture = SelectUtils.selectWeatherBackground(weather1.getData().getWeather(), when);
            backgroundImageView.setImageResource(backgroundPicture);
            //防止某些小地区没有数据
            if (weather1.getData().getAir_pm25() != null) {
                pmTextView.setText(weather1.getData().getAir_pm25());
                airTextView.setText(weather1.getData().getAir());
            }
            //给UI设置数据的操作
            tempTextView.setText(weather1.getData().getTemp());
            visibilityTextView.setText(weather1.getData().getVisibility());
            weatherTextView.setText(weather1.getData().getWeather());
            minToMaxTextView.setText(weather1.getData().getMin_temp() + "~" + weather1.getData().getMax_temp() + "℃");

            windDirectionTextView.setText(weather1.getData().getWind());
            windLevelTextView.setText(weather1.getData().getWind_speed());
            visibilityTextView2.setText(weather1.getData().getVisibility());
            pressureTextView.setText(weather1.getData().getPressure() + "hPa");
            humidityTextView.setText(weather1.getData().getHumidity());
            //防止某些小地区没有数据
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
        //初始化很多很多控件
        mConstraintLayout = view.findViewById(R.id.cl_fragment_windmill);
        topConstraintLayout = view.findViewById(R.id.cl_fragment_top);
        adviceCardView = view.findViewById(R.id.cd_fragment_advice);
        mConstraintLayout.setOnClickListener(this);
        topConstraintLayout.setOnClickListener(this);
        adviceCardView.setOnClickListener(this);

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

        backgroundImageView = view.findViewById(R.id.ll_fragment_weatherBackground);
        mScrollView = view.findViewById(R.id.sc_fragment);
        //scrollView设置监听
        mScrollView.setScrollListener((l, t, oldl, oldt) -> {
            int alpha = (int) (255.0 - (float) ((255.0 / 700) * mScrollView.getScrollY()));
            if (alpha < 0) {
                alpha = 0;
            }
            if (alpha > 255) {
                alpha = 255;
            }
            backgroundImageView.getDrawable().setAlpha(alpha);
        });

        //风车背景旋转
        ObjectAnimator animator = ObjectAnimator.ofFloat(mConstraintLayout, "rotation", 0f, 360f);
        animator.setDuration(8000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
        //字体逆时针旋转
        rotate(visibilityLinearLayout);
        rotate(humidityLinearLayout);
        rotate(pressureLinearLayout);
        rotate(windLinearLayout);

    }

    /**
     * 字体旋转
     * @param view
     */
    private void rotate(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f);
        animator.setDuration(8000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    /**
     * 切换fragment时
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onResume() {
        super.onResume();
        //scrollView增加监听
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(
                () -> {
                    //获取activity的viewpager的状态
                    MainActivity activity = (MainActivity) getActivity();
                    int state = activity.getState();
                    //如果不是拖拉就加入scrollview监听拦截SwipeRefreshLayout的滑动时间，如果是就不加入，以免影响viewpager2和
                    // SwipeRefreshLayout的滑动冲突处理
                    if (!(state== ViewPager2.SCROLL_STATE_DRAGGING)){
                        SwipeRefreshLayout refresh = getActivity().findViewById(R.id.srl_main_refresh);
                        if (mScrollView.getScrollY()>=0&&mScrollView.getScrollY()<=2){
                            refresh.setEnabled(true);
                        }else {
                            refresh.setEnabled(false);
                        }
                    }
                });
        //获取activity的背景，以跟随当前fragment更换背景。
        ConstraintLayout background2 = getActivity().findViewById(R.id.cl_activity_main_bg);

        if (weather != null) {
            //设置当前城市名称
            TextView city = getActivity().findViewById(R.id.tv_toolbar_city);
            city.setText(weather.getCity());
            //根据早晚设置背景颜色
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
        //防止天气背景透明度重置
        int alpha = (int) (255.0 - (float) ((255.0 / 700) * mScrollView.getScrollY()));
        if (alpha < 0) {
            alpha = 0;
        }
        if (alpha > 255) {
            alpha = 255;
        }
        backgroundImageView.getDrawable().setAlpha(alpha);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cl_fragment_top:
            case R.id.cl_fragment_windmill:
            case R.id.cd_fragment_advice:
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                startActivity(intent);
        }
    }
}