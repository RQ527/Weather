package com.example.weather.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.weather.MainActivity;
import com.example.weather.R;

/**
 * ...
 * 自定义菜单栏(只适用于此项目)
 * @author 1799796122 (Ran Sixiang)
 * @email 1799796122@qq.com
 * @date 2022/1/18
 */
public class MyMenu extends View implements View.OnClickListener{
    private Bitmap background;//菜单图片
    private Paint paint;
    private PopupWindow popupWindow;
    //图片的缩放比例
    private float scaleX;
    private float scaleY;
    private MyMenuListener listen;

    public MyMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        setOnClickListener(this);

    }

    private void initView() {
        paint = new Paint();
        paint.setAntiAlias(true);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.menu);
    }

    /**
     * 获取xml布局里的width和height
     * @param defaultSize 默认大小
     * @param measureSpec 解析的测量码
     * @return
     */
    private int getMySize(int defaultSize,int measureSpec){
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);//测量模式
        int size = MeasureSpec.getSize(measureSpec);//测量尺寸

        switch (mode){
            case MeasureSpec.UNSPECIFIED:
             mySize = defaultSize;
             break;
            case MeasureSpec.AT_MOST:
                mySize = size;
                break;
            case MeasureSpec.EXACTLY:
                mySize = size;
                break;
        }
        return mySize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMySize(100,widthMeasureSpec);
        int height = getMySize(100,heightMeasureSpec);
        //以短的一边进行缩放
        if (width>=height){
            scaleX = height/(float)background.getWidth();
            scaleY = height/(float)background.getHeight();
        }else {
            scaleX = width/(float)background.getWidth();
            scaleY = width/(float)background.getHeight();
        }

        setMeasuredDimension(width,height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //缩放矩阵
        Matrix matrix = new Matrix();
        matrix.preScale(scaleX,scaleY);
        //缩放操作
        canvas.drawBitmap(background,matrix,paint);
    }

    @Override
    public void onClick(View v) {
        openPopupWindow();
    }

    //用popupWindow作为弹出的menu菜单
    private void openPopupWindow(){
        //渲染
        View view = LayoutInflater.from(MainActivity.getContext()).inflate(R.layout.popupwindow_list,null);
        ListView listView = view.findViewById(R.id.lt_popupWindow);
        //数据
        final String[] data = {"设置","关于"};
        //让文本显示居中
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.getContext(), android.R.layout.simple_list_item_1,data){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position,convertView,parent);
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        };
        listView.setAdapter(adapter);
        //设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (data[position].equals("关于")&&listen!=null){
                    listen.itemListen(position);
                }else {
                    //点击反应
                    Toast.makeText(MainActivity.getContext(), data[position], Toast.LENGTH_SHORT).show();
                }
                //影藏弹窗
                dismissPopupWindow();
            }
        });

        popupWindow = new PopupWindow(view, 250, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.showAsDropDown(this, -170, 50);

    }

    private void dismissPopupWindow() {
        if (popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * menu监听接口
     */
    public interface MyMenuListener{
        void itemListen(int position);
    }

    /**
     * 为menu设置监听
     * @param listen
     */
    public void setListen(MyMenuListener listen) {
        this.listen = listen;
    }
}
