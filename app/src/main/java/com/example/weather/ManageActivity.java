package com.example.weather;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.adapter.MyRecyclerAdapter;
import com.example.weather.base.BaseActivity;
import com.example.weather.bean.Weather;
import com.example.weather.room.IDispose;
import com.example.weather.room.MyDataBase;
import com.example.weather.room.WeatherDao;
import com.example.weather.utils.RoomUtils;

import java.util.ArrayList;
import java.util.List;

public class ManageActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "RQ";
    private RecyclerView mRecyclerView;
    private WeatherDao weatherDao;
    private MyDataBase myDataBase;
    private ArrayList<Weather> data;//recyclerView的数据源
    private Button backButton;
    private Button addButton;
    private MyRecyclerAdapter recyclerAdapter;
    private Intent intent = new Intent();//用于携带数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        initView();
        //加载数据
        RoomUtils.queryAll(new IDispose() {
            @Override
            public void runOnUi(Weather weather) {

            }
            @Override
            public void runOnUi(List<Weather> weathers) {
                if (weathers != null) {
                    //遍历weathers添加数据
                    for (Weather weather : weathers) {
                        data.add(weather);
                    }
                    recyclerAdapter = new MyRecyclerAdapter(data, ManageActivity.this);
                    //设置item监听
                    recyclerAdapter.setRecyclerItemClickListener(new MyRecyclerAdapter.onRecyclerItemClickListener() {
                        @Override
                        public void onRecyclerItemClick(int position, View view) {
                            //点击就跳转，并携带位置
                            intent.putExtra("position",String.valueOf(position));
                            setResult(0,intent);
                            finish();
                        }

                        @Override
                        public void onRecyclerItemLongClick(int position, View view) {
                            //长按弹出窗口
                            openPopupWindow(view,position);
                        }

                    });

                    mRecyclerView.setAdapter(recyclerAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(ManageActivity.this));

                }
            }
        }, weatherDao);

    }

    private void initView() {
        mRecyclerView = findViewById(R.id.rv_manageActivity_city);
        myDataBase = MyDataBase.getInstance(this);
        weatherDao = myDataBase.getWeatherDao();
        data = new ArrayList<>();
        backButton = findViewById(R.id.bt_toolbar_back);
        addButton = findViewById(R.id.bt_toolbar_addCity);
        backButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_toolbar_back:
                finish();
                break;
            case R.id.bt_toolbar_addCity:
                Intent intent2 = new Intent(ManageActivity.this, AddWeatherActivity.class);
                intent2.putExtra("flag", "manage");
                startActivityForResult(intent2,0);
                break;
        }
    }

    PopupWindow popupWindow;

    //用popupWindow作为弹出的dialog
    private void openPopupWindow(View mView,int mPosition) {
        //渲染
        View view = LayoutInflater.from(MainActivity.getContext()).inflate(R.layout.popupwindow_list, null);
        ListView listView = view.findViewById(R.id.lt_popupWindow);
        //数据
        final String[] data2 = {"删除"};
        //让文本显示居中
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.getContext(), android.R.layout.simple_list_item_1, data2) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        };
        listView.setAdapter(adapter);

        //设置点击事件
        listView.setOnItemClickListener((parent, view1, position, id) -> {

            TextView city = mView.findViewById(R.id.tv_itemRv_city);
            for (Weather weather:data){
                if (weather.getCity().equals(city.getText().toString())){
                    data.remove(weather);
                    recyclerAdapter.notifyItemRemoved(mPosition);
                    break;
                }
            }

            RoomUtils.delete(weatherDao, city.getText().toString());
            intent.putExtra("position2",String.valueOf(mPosition));
            setResult(0,intent);
            //如果删完了就跳转至添加城市界面
            if (data.size() == 0) {
                Intent intent = new Intent(ManageActivity.this, AddWeatherActivity.class);
                intent.putExtra("flag", "manage");
                startActivityForResult(intent,0);
            }

            //影藏弹窗
            dismissPopupWindow();
        });

        popupWindow = new PopupWindow(view, 250, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.mypopwindow2_anim_style);
        popupWindow.showAsDropDown(mView, 300, -350);

    }

    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null) {
                //添加后通知recyclerView更新
                Weather weather = (Weather) data.getSerializableExtra("weather");
                this.data.add(weather);
                recyclerAdapter.notifyDataSetChanged();
        }
    }
}