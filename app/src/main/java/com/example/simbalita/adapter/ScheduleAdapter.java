package com.example.simbalita.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.model.Schedule;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final List<Schedule> scheduleList;
    private final boolean isAdmin;
    private final OnScheduleActionListener listener;

    public interface OnScheduleActionListener {
        void onEdit(Schedule schedule);
        void onDelete(Schedule schedule);
    }

    public ScheduleAdapter(List<Schedule> scheduleList, boolean isAdmin, OnScheduleActionListener listener) {
        this.scheduleList = scheduleList;
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        
        holder.tvTitle.setText(schedule.getTitle());
        holder.tvLocation.setText(schedule.getLocation());
        
        // Format Date to friendly form e.g. "15 Juli 2026"
        String dateFormatted = formatDateStr(schedule.getDate());
        holder.tvDateTime.setText(dateFormatted + " - " + schedule.getTime());

        if (schedule.getStatus() != null) {
            holder.tvStatus.setText("Status: " + schedule.getStatus());
        } else {
            holder.tvStatus.setText("Status: Belum Terlaksana");
        }

        if (isAdmin) {
            holder.ivEdit.setVisibility(View.VISIBLE);
            holder.ivDelete.setVisibility(View.VISIBLE);
            
            holder.ivEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(schedule);
            });
            holder.ivDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(schedule);
            });
        } else {
            holder.ivEdit.setVisibility(View.GONE);
            holder.ivDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    private String formatDateStr(String dateStr) {
        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputSdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        try {
            Date date = inputSdf.parse(dateStr);
            if (date != null) return outputSdf.format(date);
        } catch (ParseException e) {
            // fallback
        }
        return dateStr;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateTime, tvTitle, tvLocation, tvStatus;
        ImageView ivEdit, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tv_sch_datetime);
            tvTitle = itemView.findViewById(R.id.tv_sch_title);
            tvLocation = itemView.findViewById(R.id.tv_sch_location);
            tvStatus = itemView.findViewById(R.id.tv_sch_status);
            ivEdit = itemView.findViewById(R.id.iv_sch_edit);
            ivDelete = itemView.findViewById(R.id.iv_sch_delete);
        }
    }
}
