package com.example.weather.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weather.MainActivity;
import com.example.weather.R;
import com.example.weather.bean.Weather;
import com.example.weather.utils.SelectUtils;
import com.example.weather.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 * 自定义曲线图
 *
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/19
 */
public class MyDiagram extends ViewGroup {
    private static final String TAG = "RQ";
    private int height;//控件高度
    private int screenWidth;//屏幕宽度
    private int minTemp = 0;//最小温度初始值为0
    private int viewWidth;//view的宽
    private int size = 1;//视图数量初始值为1
    private float scaleX;//X轴缩放比率
    private float scaleY;//Y轴缩放比率
    private float differDistance;//与整数倍的viewWidth的偏移量
    private float drawX = 100;//圆环圆心初始值为100
    private float drawY = 300;//圆环圆心初始值为300
    private GestureDetector detector;//手势辅助器
    private VelocityTracker mVelocityTracker;//滑动速度追踪器
    private FlingRunnable mFling;//惯性滑动线程
    private List<Point> points;//曲线上点的集合
    private Scroller mScroller;//优化滑动效果的工具
    private Boolean isFlinging = false;//是否正在惯性滑动的标志
    private Weather weather;//weather数据
    private Paint curvePaint;//曲线画笔
    private Paint whiteCirclePaint;//圆环内园画笔
    private Paint textPaint;//文字画笔
    private Paint dottedPaint;//虚线画笔
    private Path curvePath;//曲线路径
    private Path dottedPath;//虚线路径
    private View view;//展示天气的视图

    public MyDiagram(Context context, AttributeSet attrs) {
        super(context, attrs);
        //允许ViewGroup的onDraw生效
        setWillNotDraw(false);

        init();
        initPaint();
        //咦？你可能会疑问为什么没有初始化路径，因为我放在了omMeasure里面。具体看onMeasure
    }

    //从外界设置数据
    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    /**
     * 初始化其他东西
     */
    private void init() {

        mVelocityTracker = VelocityTracker.obtain();
        mScroller = new Scroller(MainActivity.getContext());
        screenWidth = getScreenWidth();
        viewWidth = (int) (screenWidth / 5.0);

        //初始化手势识别器
        detector = new GestureDetector(MainActivity.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollTo((int) distanceX + getScrollX(), 0);
                //反拦截，处理于viewpage2的滑动冲突
                if (Math.abs(distanceX) >= Math.abs(distanceY)||Math.abs(distanceY)<8) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return true;
            }
        });

    }

    /**
     * 初始化路径
     */
    public void initPath() {
        curvePath = new Path();
        //这里面减0.1是为了防止误差画圆环找不到圆心，具体看画圆心的原理。
        curvePath.moveTo((float) (points.get(0).x - 1), points.get(0).y);
        //遍历点集合画曲线
        for (int i = 0; i < points.size() - 1; i++) {
            curvePath.cubicTo((points.get(i).x + points.get(i + 1).x) / 2,
                    points.get(i).y,
                    (points.get(i).x + points.get(i + 1).x) / 2,
                    points.get(i + 1).y, (float) (points.get(i + 1).x + 0.1), points.get(i + 1).y);
        }
        dottedPath = new Path();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mFling = new FlingRunnable(MainActivity.getContext());

        curvePaint = new Paint();
        curvePaint.setAntiAlias(true);
        curvePaint.setColor(Color.parseColor("#FF03DAC5"));
        curvePaint.setStrokeWidth(15);
        curvePaint.setStyle(Paint.Style.STROKE);

        whiteCirclePaint = new Paint();
        whiteCirclePaint.setStrokeWidth(40);
        whiteCirclePaint.setColor(Color.WHITE);
        whiteCirclePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);

        dottedPaint = new Paint();
        dottedPaint.setAntiAlias(true);
        dottedPaint.setStrokeWidth(3);
        dottedPaint.setColor(Color.GRAY);
        dottedPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 初始化天气数据
     */
    public void initWeather() {
        //防止weather为空，因此设置了假数据,也是累死了.
        if (weather == null) {
            List<Weather.Data.Hour> hours = new ArrayList<>();
            Weather.Data.Hour hour = new Weather.Data.Hour("0", "null", "null", "null",
                    "null", "null");
            hours.add(hour);
            weather = new Weather(0, " ", 0,
                    new Weather.Data("null", "null",
                            new ArrayList<>(), new Weather.Data.Aqi("null", "null",
                            "null", "null", "null", "null", "null",
                            "null", "null"), "null", "null", "null",
                            hours, "null",
                            new Weather.Data.Index(
                                    new Weather.Data.Index.Chuangyi("null", "null", "null"),
                                    new Weather.Data.Index.Ganmao("null", "null", "null"),
                                    new Weather.Data.Index.Huazhuang("null", "null", "null"),
                                    new Weather.Data.Index.Xiche("null", "null", "null"),
                                    new Weather.Data.Index.Yundong("null", "null", "null"),
                                    new Weather.Data.Index.Ziwaixian("null", "null", "null")),
                            "null", "null", "null", "null", "null",
                            "null", "null", "null", "null", "null",
                            "null", "null", "null", "null", "null",
                            "null", "null", "null"), 0, "null", 0);
        }
        //添加视图之前删除假数据。

        //将weather的温度转化成点。
        points = weatherToPoint(weather);
    }

    //重写scrollTo方法防止滑动过度
    @Override
    public void scrollTo(int x, int y) {
        if (x <= 0) {
            x = 0;
        }
        if (x >= (viewWidth) * (size - 5)) {
            x = (viewWidth) * (size - 5);
        }
        super.scrollTo(x, y);
    }

    /**
     * 获取xml布局里的width和height
     *
     * @param defaultSize 默认大小
     * @param measureSpec 解析的测量码
     * @return
     */
    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);//测量模式
        int size = MeasureSpec.getSize(measureSpec);//测量尺寸
        //根据模式化尺寸
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                mySize = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                mySize = size;
                break;
        }
        return mySize;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 添加天气视图的方法。
     */
    private void insertView() {
        if (weather.getCode()!=0) {
            List<Weather.Data.Hour> hours = weather.getData().getHour();//获取的时间集合
            Weather.Data.Hour hour;//用来存储小时天气
            String wea;//天气状况
            String time;//时间
            TextView timeTextView;//展示时间的控件
            ImageView weatherImageView;//展示天气的控件
            TextView weatherTextview;//展示天气的TextView
            TextView fanTextView;//展示风力的TextView
            String wind;//风力
            int weatherPicture;//天气图片ID
            int minuteTime;//时间（分钟）
            Log.d(TAG, "insertView: " + weather.getData().getSunrise());
            int sunrise = TimeUtils.TimeToMinutes(weather.getData().getSunrise());//日出时间（分钟）
            int sunset = TimeUtils.TimeToMinutes(weather.getData().getSunset());//日落时间（分钟）
            String when = "晚上";//白天还是晚上
            //添加天气视图
            for (int i = 0; i < size; i++) {
                view = LayoutInflater.from(MainActivity.getContext()).inflate(R.layout.item_mydaigram, null);
                hour = hours.get(i);
                wea = hour.getWea();

                time = hour.getTime();
                minuteTime = TimeUtils.TimeToMinutes(time.substring(11, 16));


                //判断白天还是晚上用于设置不同的视图
                if (minuteTime >= sunrise && minuteTime <= sunset) {
                    when = "白天";
                }
                weatherPicture = SelectUtils.selectWeatherPicture(wea, when);
                weatherImageView = view.findViewById(R.id.iv_weather);
                weatherImageView.setImageResource(weatherPicture);

                timeTextView = view.findViewById(R.id.tv_time);
                timeTextView.setText(time.substring(11, 16));

                weatherTextview = view.findViewById(R.id.tv_weather);
                weatherTextview.setText(hour.getWea());

                wind = hour.getWind_level();
                fanTextView = view.findViewById(R.id.tv_fan);
                fanTextView.setText(wind);
                //适应字体颜色
                if (when.equals("晚上")) {
                    weatherTextview.setTextColor(Color.WHITE);
                    fanTextView.setTextColor(Color.WHITE);
                    timeTextView.setTextColor(Color.WHITE);
                }

                addView(view);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取控件高度
        height = getMySize(100, heightMeasureSpec);
        //因为初始化weather需要height,而height在上面才初始化完成所以放在这。
        initWeather();
        //path也是一样
        initPath();
        //到这里数据加载完毕，添加视图。
        insertView();
        //测量view
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            childAt.measure(childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
        }
        if (view!=null) {
            //获取view缩放比率
            scaleX = (viewWidth) / (float) view.getMeasuredWidth();
            scaleY = (height / 2) / (float) view.getMeasuredHeight();
        }
        setMeasuredDimension(size * (viewWidth), height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int left = this.getLeft();
        //调整view位置
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);

            childAt.layout(left, height / 2, viewWidth, height);
            childAt.setPivotX(0);
            childAt.setPivotY(0);

            childAt.setScaleX(scaleX);
            childAt.setScaleY(scaleY);
            childAt.layout((int) (viewWidth) * (i), height / 2,
                    ((int) (viewWidth / scaleX)) * (i + 1), (int) (height / scaleY));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画曲线
        canvas.drawPath(curvePath, curvePaint);
        //画圆环
        drawRing(drawX, drawY, canvas, curvePaint);

    }

    //为调整圆环位置以及优化圆环移动效果而开启的线程,这里更新的方法用的是郭神的思路，感谢郭神!!!
    private final Runnable mMoveDrawRun = new Runnable() {
        /*算法:
        计算每次圆环位置的算法是画一条path于曲线路径相交，然后取交集，
        再用矩形计算交点位置，再得出圆环新的位置，并通知刷新。
         */
        private final Path linePath = new Path();
        private final RectF rect = new RectF();

        @Override
        public void run() {
            linePath.reset();//重置路径
            //圆环的位置有两种情况，在前面一直在屏幕左方固定位置，在最后面可以通过点击来调整圆环的位置，所以分成了两种情况：
            if (getScrollX() >= (viewWidth * (size - 5))) {
                // 绘制垂直线与曲线取交集
                linePath.moveTo(drawX, 0);
                linePath.lineTo(drawX, (height / 2) + 10);

                // 这里就直接取一个底为1，高为控件高度的矩形
                linePath.lineTo(drawX + 1, (height / 2) + 10);
                linePath.lineTo(drawX + 1, 0);
            } else if (getScrollX() < (viewWidth * (size - 5))) {
                // 绘制垂直线与曲线取交集

                linePath.moveTo((float) (getScrollX() + viewWidth / 2.0), 0);
                linePath.lineTo((float) (getScrollX() + viewWidth / 2.0), (height / 2) + 10);

                // 这里就直接取一个底为 1，高为控件高度的矩形
                linePath.lineTo((float) (getScrollX() + viewWidth / 2.0 + 1), (height / 2) + 10);
                linePath.lineTo((float) (getScrollX() + viewWidth / 2.0 + 1), 0);
                drawX = (float) (getScrollX() + viewWidth / 2.0);
            }
            linePath.close();
            //取交集
            linePath.op(curvePath, Path.Op.INTERSECT);

            // 取完交集后使用下面这个得到包裹它的矩形
            linePath.computeBounds(rect, false);

            if (rect.top == 0) {
                //为零就让其在底部
                drawY = height / 2;
            } else {
                drawY = rect.bottom; // 矩形的  bottom 就是圆心 y
            }

            //这里更新的方法用的是郭神的思路，感谢郭神!!!
            invalidate(); // 通知重绘，为什么放这里而不放在 onTouchEvent 中？原因看下面注释
            /*
             * 下面这个是重点！
             * 方法意思为：将这个 Runnable 放在下一个动画时间点再调用
             * 为什么要这样？
             * 1、调用 invalidate() 会回调 onDraw()，但并不是马上就回调
             * 2、onTouchEvent 的回调频率与 onDraw() 的回调频率并不相同
             * 3、为了节省资源，减少不必要的回调，刷新率 60 的手机是每 16 毫秒回调一次 onDraw()
             *    刷新率 90 的手机是每 11 毫秒回调一次（计算方式：1 秒 = 1000 毫秒，1000 / 60 = 16）
             * 4、正是因为这样的设计，并不需要每次产生移动就重新计算圆心坐标后调用 invalidate() 刷新，
             *    所以官方就提供了下面这个方法，你可以在很多官方源码中看见这种用法
             * */
            ViewCompat.postOnAnimation(MyDiagram.this, this);
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        super.onTouchEvent(event);
        differDistance = 0;
        //增加速度追踪器
        mVelocityTracker.addMovement(event);
        detector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                
                //拦截刷新
                getParent().requestDisallowInterceptTouchEvent(true);
                Context context = getContext();
                while (context instanceof ContextWrapper) {
                    if (context instanceof Activity) {
                        break;

                    }

                    context = ((ContextWrapper) context).getBaseContext();

                }
                Activity activity = (Activity) context;
                SwipeRefreshLayout refresh = activity.findViewById(R.id.srl_main_refresh);
                refresh.setEnabled(false);

                //对应圆环位于后面的情况
                if (getScrollX() >= (size - 5) * viewWidth) {
                    if ((getScrollX() + event.getX()) > (size - 0.5) * viewWidth) {
                        drawX = getScrollX() + (float) 4.5 * viewWidth - 1;
                    } else {
                        drawX = getScrollX() + event.getX();
                    }
                }
                //按下开始监听
                mMoveDrawRun.run();
                //按下就停止正在的滑动
                mFling.stop();
                break;
            case MotionEvent.ACTION_MOVE:
                //对应圆环位于后面的情况
                if (getScrollX() >= (size - 5) * viewWidth) {
                    if ((getScrollX() + event.getX()) > (size - 0.5) * viewWidth) {
                        drawX = getScrollX() + (float) 4.5 * viewWidth - 1;
                    } else {
                        drawX = getScrollX() + event.getX();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //对应圆环位于后面的情况
                if (getScrollX() >= (size - 5) * viewWidth) {
                    drawX = (float) (getScrollX() + viewWidth / 2.0);
                }
                //测量1秒的滑动速度
                mVelocityTracker.computeCurrentVelocity(1000);
                //获取水平方向的滑动速度
                float xVelocity = mVelocityTracker.getXVelocity();
                //大于最小速度才算滑动
                if (Math.abs(xVelocity) > ViewConfiguration.get(MainActivity.getContext()).getScaledMinimumFlingVelocity()) {
                    //从当前位置开始滑动
                    int initX = getScrollX();
                    //最大滑动距离为控件宽度
                    int maxX = (viewWidth) * size;

                    if (maxX > 0) {
                        isFlinging = true;
                        mFling.start(initX, (int) xVelocity, initX, maxX);
                    }
                }
                //如果没有惯性滑动就调整Ui
                if (!isFlinging) {
                    adjustUi();
                }

            case MotionEvent.ACTION_CANCEL: // 被父布局拦截
                removeCallbacks(mMoveDrawRun);// 抬手或被父布局拦截时关闭 mMoveDrawRun
                break;
            default:

        }
        return true;
    }

    /**
     * 调整控件位置保持只有五个view出现在屏幕中
     */
    private void adjustUi() {
        differDistance = getScrollX() % (viewWidth);
        if (differDistance >= (viewWidth) / 2) {
            differDistance = (viewWidth) - differDistance;
        } else if (differDistance < (viewWidth) / 2) {
            differDistance = -differDistance;
        }
        //平滑过渡
        mScroller.startScroll(getScrollX(), getScrollY(), (int) differDistance, 0);

        invalidate();
    }

    //滑动工具类的计算滑动方法
    @Override
    public void computeScroll() {
        //滑动同时调整圆环位置
        mMoveDrawRun.run();

        if (mScroller.computeScrollOffset()) {

            float currX = mScroller.getCurrX();
            scrollTo((int) currX, 0);

            invalidate();

        } else {
            //滑动结束移除圆环位置监听
            removeCallbacks(mMoveDrawRun);
        }
    }

    /**
     * 进行惯性滚动线程，因为滚动比较耗时所以另外开启的线程进行滚动
     */
    private class FlingRunnable implements Runnable {


        private Scroller mScroller;
        private int mInitX;//开始滚动位置
        private int mMinX;
        private int mMaxX;

        private int mVelocityX;

        FlingRunnable(Context context) {
            this.mScroller = new Scroller(context, null, false);
        }

        void start(int initX, int velocityX, int minX, int maxX) {
            this.mInitX = initX;
            this.mVelocityX = velocityX;
            this.mMinX = minX;
            this.mMaxX = maxX;

            // 先停止上一次的滚动
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }

            // 开始 fling
            mScroller.fling(initX, 0, velocityX,
                    0, 0, maxX, 0, 0);
            post(this);

        }

        @Override
        public void run() {
            // 如果已经结束，就不再进行
            if (!mScroller.computeScrollOffset()) {
                return;
            }
            // 计算偏移量
            int currX = mScroller.getCurrX();
            int diffX = mInitX - currX;
            // 用于记录是否超出边界，如果已经超出边界，则不再进行回调，即使滚动还没有完成
            boolean isEnd = false;
            if (diffX != 0) {
                // 超出右边界，进行修正
                if (getScrollX() + diffX >= (viewWidth) * size) {
                    diffX = (int) ((viewWidth) * size - getScrollX());
                    isEnd = true;
                }
                // 超出左边界，进行修正
                if (getScrollX() + diffX <= 0) {
                    diffX = 0 - getScrollX();
                    isEnd = true;
                }
                if (!mScroller.isFinished()) {
                    scrollBy(diffX, 0);
                }
                mInitX = currX;
            }
            //在滚动就更新UI
            if (!isEnd) {
                post(this);
            }
            //滚动停止调整UI
            if (mScroller.computeScrollOffset()) {
                adjustUi();
                isFlinging = false;
            }
        }

        /**
         * 进行停止
         */
        void stop() {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
        }
    }

    /**
     * weather转化成point的方法
     *
     * @param weather 数据weather
     * @return
     */
    private List<Point> weatherToPoint(Weather weather) {
        //用于保存获取的温度
        int temp;
        //点的集合
        List<Point> points = new ArrayList<Point>();
        //获取hours的集合
        List<Weather.Data.Hour> hours = weather.getData().getHour();
        size = hours.size();
        //获取最小温度
        minTemp = Integer.parseInt(hours.get(0).getTemp());
        //找出最小温度
        for (int i = 0; i < hours.size(); i++) {
            Weather.Data.Hour hour = hours.get(i);
            temp = Integer.parseInt(hour.getTemp());
            if (minTemp >= temp) {
                minTemp = temp;
            }
        }
        //遍历转化成点
        for (int i = 0; i < hours.size(); i++) {
            Weather.Data.Hour hour = hours.get(i);
            temp = Integer.parseInt(hour.getTemp());
            Point point = new Point();
            point.y = height / 2 - ((height / 2) / 15) * (temp - minTemp);
            point.x = (int) ((2 * (i + 1) - 1) * (viewWidth / 2.0));
            points.add(point);
        }
        return points;
    }

    /**
     * 画圆环
     *
     * @param x      圆环圆心坐标x
     * @param y      圆环圆心坐标y
     * @param canvas 画布
     * @param paint  画笔
     */
    private void drawRing(float x, float y, Canvas canvas, Paint paint) {
        //外圆
        canvas.drawCircle(x, y, 40, paint);
        //内园
        canvas.drawCircle(x, y, 33, whiteCirclePaint);
        //计算温度
        int temp = Math.round((height / 2 - y) * ((float) 15 / (height / 2))) + minTemp;
        //文字
        canvas.drawText(String.valueOf(temp), x, y + 10, textPaint);
        //虚线
        int drawDistance = 50;
        while ((y + drawDistance) <= height / 2) {
            dottedPath.moveTo(x, y + drawDistance);
            dottedPath.lineTo(x, y + drawDistance + 15);
            canvas.drawPath(dottedPath, dottedPaint);
            drawDistance += 30;
        }
        dottedPath.reset();
    }
}

