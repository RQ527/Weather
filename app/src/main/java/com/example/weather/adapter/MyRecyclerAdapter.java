package com.example.weather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.bean.Weather;
import com.example.weather.utils.SelectUtils;
import com.example.weather.utils.TimeUtils;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder>{
    private List<Weather> data;
    private Context context;

    public MyRecyclerAdapter(List<Weather> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_rv,null);
            return new MyViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "Range"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Weather weather = data.get(position);
        String time = weather.getData().getUpdate_time();
        int minuteTime = TimeUtils.TimeToMinutes(time.substring(11, 16));
        int sunrise = TimeUtils.TimeToMinutes(weather.getData().getSunrise());
        int sunset = TimeUtils.TimeToMinutes(weather.getData().getSunset());
        String when = "晚上";
        //判断白天还是晚上用于设置不同的视图
        if (minuteTime >= sunrise && minuteTime <= sunset) {
            when = "白天";
            holder.backgroundCardView.setCardBackgroundColor(Color.parseColor("#6699FF"));
        }

        holder.cityTextView.setText(data.get(position).getCity());
        holder.tempTextView.setText(data.get(position).getData().getTemp()+"℃");
        holder.backgroundImageView.setImageResource(SelectUtils.selectWeatherBackground(weather.getData().getWeather(),when));
        holder.backgroundImageView.setAlpha(200);
    }

    @Override
    public int getItemCount() {

        return data == null ? 0:data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView cityTextView;
        private TextView tempTextView;
        private CardView backgroundCardView;
        private ImageView backgroundImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cityTextView = itemView.findViewById(R.id.tv_itemRv_city);
            tempTextView = itemView.findViewById(R.id.tv_itemRv_temp);
            backgroundCardView = itemView.findViewById(R.id.cd_recyclerView_background);
            backgroundImageView = itemView.findViewById(R.id.iv_recyclerView_background);
            itemView.setOnClickListener(v -> {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onRecyclerItemClick(getAdapterPosition(),itemView);
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (mOnItemClickListener!=null){
                    mOnItemClickListener.onRecyclerItemLongClick(getAdapterPosition(),itemView);
                }
                return true;
            });
        }
    }

    private onRecyclerItemClickListener mOnItemClickListener;

    public void setRecyclerItemClickListener(onRecyclerItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public interface onRecyclerItemClickListener{
        void onRecyclerItemClick(int position,View view);

        void onRecyclerItemLongClick(int position,View view);
    }

}
