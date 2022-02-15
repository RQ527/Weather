package com.example.weather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * ...
 * 重写ScrollView暴露监听滑动的方法
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/2/5
 */
public class MyScrollView extends ScrollView {
    private ScrollListener mListener;

    public interface ScrollListener {//声明接口，用于传递数据
        void scrollOritention(int l, int t, int oldl, int oldt);
    }

    public MyScrollView(Context context, AttributeSet attrs,
                        int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context) {
        super(context);
    }
    private float startX;//按下位置
    private float startY;
    private float endX;//滑动位置
    private float endY;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                endX = ev.getX();
                endY = ev.getY();
                //拦截viewpager的滑动
                    if (Math.abs(endX-startX)>Math.abs(endY-startY)&&Math.abs(endX-startX)>8){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                break;
        }
        return true;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mListener != null) {
            mListener.scrollOritention(l, t, oldl, oldt);
        }
    }

    /**
     * 设置监听
     * @param l
     */
    public void setScrollListener(ScrollListener l) {
        this.mListener = l;
    }


}
