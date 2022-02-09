package com.example.weather.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather.R;
import com.example.weather.bean.Weather;

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

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.cityTextView.setText(data.get(position).getCity());
        holder.tempTextView.setText(data.get(position).getData().getTemp()+"℃");

    }

    @Override
    public int getItemCount() {

        return data == null ? 0:data.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView cityTextView;
        private TextView tempTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cityTextView = itemView.findViewById(R.id.tv_itemRv_city);
            tempTextView = itemView.findViewById(R.id.tv_itemRv_temp);
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
