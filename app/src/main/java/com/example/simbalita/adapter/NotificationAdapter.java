package com.example.simbalita.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.model.Notification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> list;
    private final Context context;

    public NotificationAdapter(Context context, List<Notification> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification item = list.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getBody());
        holder.tvTime.setText(item.getTimeLabel());

        // Dynamic Icon mapping
        if ("schedule".equals(item.getIconType())) {
            holder.ivIcon.setImageResource(R.drawable.ic_calendar);
            holder.ivIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F57C00"))); // Orange
            holder.ivIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFE0B2"))); // Light Orange
        } else if ("vaccine".equals(item.getIconType())) {
            holder.ivIcon.setImageResource(R.drawable.ic_heart);
            holder.ivIcon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary_ibu))); // Green
            holder.ivIcon.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.status_normal_light))); // Light Green
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_bell);
            holder.ivIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor("#2196F3"))); // Blue
            holder.ivIcon.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E3F2FD"))); // Light Blue
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvBody, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_notif_item_icon);
            tvTitle = itemView.findViewById(R.id.tv_notif_item_title);
            tvBody = itemView.findViewById(R.id.tv_notif_item_body);
            tvTime = itemView.findViewById(R.id.tv_notif_item_time);
        }
    }
}
