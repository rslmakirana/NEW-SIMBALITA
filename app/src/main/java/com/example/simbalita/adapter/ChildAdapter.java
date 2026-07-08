package com.example.simbalita.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ViewHolder> {

    private final List<Child> childList;
    private final OnChildClickListener listener;

    public interface OnChildClickListener {
        void onChildClick(Child child);
    }

    public ChildAdapter(List<Child> childList, OnChildClickListener listener) {
        this.childList = childList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Child child = childList.get(position);
        holder.tvName.setText(child.getName());

        // Calculate age
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String today = sdf.format(new Date());
        int ageMonths = DatabaseHelper.calculateAgeInMonths(child.getBirthDate(), today);
        holder.tvAge.setText("(" + DatabaseHelper.formatAge(ageMonths) + ")");

        // Gender icon/text description
        holder.tvGender.setText(child.getGender());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChildClick(child);
            }
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAge, tvGender;
        ImageView ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_child_name);
            tvAge = itemView.findViewById(R.id.tv_child_age);
            tvGender = itemView.findViewById(R.id.tv_child_gender);
            ivAvatar = itemView.findViewById(R.id.iv_child_avatar);
        }
    }
}
