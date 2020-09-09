package com.zone.slide;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zone.slide.fresco.FrescoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yz on 2020/5/27 5:51 PM
 * Describe:
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PicViewHolder> {
    List<String> mList;
    Context mContext;

    public MyAdapter(RecyclerView recyclerView, List<String> list) {
        if (list != null) {
            this.mList = list;
        } else {
            this.mList = new ArrayList<>();
        }

        /**
         * 一定要关闭viewholder item动画，否则在执行notifyItem时 会造成异常
         */
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
    }

    public void addData(List<String> list) {
        if (list != null) {
            this.mList.addAll(list);
            if (list.size() == mList.size()) {
                notifyDataSetChanged();
            } else {
                notifyItemRangeInserted(mList.size() - list.size(), mList.size());
            }
        }
    }

    public void remove(int position) {
        if (position < 0 || position >= mList.size()) {
            return;
        }
        mList.remove(position);
        notifyItemRemoved(position);
        if (mList.size() == 0) {
            notifyDataSetChanged();
        } else {
            //避免图片不停刷新
            notifyItemRangeChanged(position, mList.size() - position, "updatePosition");
        }

    }


    @NonNull
    @Override
    public PicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new PicViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_pic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PicViewHolder holder, final int position) {
        FrescoUtils.loadNetImage(holder.sdv_pic, mList.get(position));
        holder.tv_position.setText("第 " + position + " 张");
        holder.tv_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"position == " + position, Toast.LENGTH_SHORT).show();
                Log.d("MyAdapter", " position== " + position);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull PicViewHolder holder, final int position, @NonNull List<Object> payloads) {
        if (payloads != null && payloads.size() > 0 && TextUtils.equals((String) (payloads.get(0)), "updatePosition")) {
            /**在此处重置位置position信息, 但不需要重新加载图片**/
            holder.tv_position.setText("第 " + position + " 张");
            holder.tv_position.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"position == " + position, Toast.LENGTH_SHORT).show();
                    Log.d("MyAdapter", " position== " + position);
                }
            });
        } else {
            super.onBindViewHolder(holder, position, payloads);
        }

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public List<String> getData() {
        return mList;
    }

    class PicViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView sdv_pic;
        TextView tv_position;

        public PicViewHolder(@NonNull View itemView) {
            super(itemView);
            sdv_pic = itemView.findViewById(R.id.sdv_pic);
            tv_position = itemView.findViewById(R.id.tv_position);

        }
    }
}
