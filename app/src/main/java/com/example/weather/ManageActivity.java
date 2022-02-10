package com.example.weather;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
    private ArrayList<Weather> data;
    private Button backButton;
    private Button addButton;
    private MyRecyclerAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        initView();
        RoomUtils.queryAll(new IDispose() {

            @Override
            public void runOnUi(Weather weather) {

            }
            @Override
            public void runOnUi(List<Weather> weathers) {
                if (weathers != null) {
                    for (Weather weather : weathers) {
                        data.add(weather);
                    }
                    recyclerAdapter = new MyRecyclerAdapter(data, ManageActivity.this);
                    recyclerAdapter.setRecyclerItemClickListener(new MyRecyclerAdapter.onRecyclerItemClickListener() {
                        @Override
                        public void onRecyclerItemClick(int position, View view) {
                            Intent intent = new Intent(ManageActivity.this,MainActivity.class);
                            intent.putExtra("position",String.valueOf(position));
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onRecyclerItemLongClick(int position, View view) {
                            Toast.makeText(ManageActivity.this, "长按", Toast.LENGTH_SHORT).show();
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
        backButton.setOnClickListener(this);
        addButton = findViewById(R.id.bt_toolbar_addCity);
        addButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_toolbar_back:
                Intent intent = new Intent(ManageActivity.this, MainActivity.class);
                startActivity(intent);
                MainActivity.flag = 1;
                finish();
                break;
            case R.id.bt_toolbar_addCity:
                Intent intent2 = new Intent(ManageActivity.this, AddWeatherActivity.class);
                intent2.putExtra("flag", "manage");
                startActivity(intent2);
                finish();
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
        Log.d(TAG, "onItemClick: "+mPosition);
        TextView city = mView.findViewById(R.id.tv_itemRv_city);
        for (Weather weather:data){
            if (weather.getCity().equals(city.getText().toString())){
                //data.remove(weather);
//                Log.d(TAG, "openPopupWindow: "+data.remove(weather));
                //recyclerAdapter.notifyItemRemoved(mPosition);
            }
        }
        //设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView city = mView.findViewById(R.id.tv_itemRv_city);
                for (Weather weather:data){
                    if (weather.getCity().equals(city.getText().toString())){
                        data.remove(weather);

                        recyclerAdapter.notifyItemRemoved(mPosition);
                        break;
                    }
                }
                RoomUtils.delete(weatherDao, city.getText().toString());
                if (data.size() == 0) {
                    Intent intent = new Intent(ManageActivity.this, AddWeatherActivity.class);
                    intent.putExtra("flag", "manage");
                    startActivity(intent);
                    finish();
                }

                //影藏弹窗
                dismissPopupWindow();
            }

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
}