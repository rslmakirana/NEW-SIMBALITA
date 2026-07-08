package com.example.simbalita.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.model.Examination;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExaminationAdapter extends RecyclerView.Adapter<ExaminationAdapter.ViewHolder> {

    private final List<Examination> examList;
    private final OnExamClickListener listener;

    public interface OnExamClickListener {
        void onExamClick(Examination exam);
    }

    public ExaminationAdapter(List<Examination> examList, OnExamClickListener listener) {
        this.examList = examList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_examination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Examination exam = examList[position];
        
        // Format Date to friendly form e.g. "10 Mei 2026"
        String formattedDate = formatDateStr(exam.getDate());
        holder.tvDate.setText(formattedDate);
        holder.tvWeight.setText(String.format(Locale.US, "%.1f", exam.getWeight()));
        holder.tvHeight.setText(String.format(Locale.US, "%.0f", exam.getHeight()));
        holder.tvStatus.setText(exam.getStatus());

        // Color status based on value
        int bgColor, textColor;
        switch (exam.getStatus()) {
            case "Normal":
                bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_normal_light);
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_normal);
                break;
            case "Kurang":
            case "Lebih":
                bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_warning);
                textColor = Color.parseColor("#7F5F00"); // Dark yellow/brown
                break;
            case "Stunting":
            default:
                bgColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_danger_light);
                textColor = ContextCompat.getColor(holder.itemView.getContext(), R.color.status_danger);
                break;
        }
        
        holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        holder.tvStatus.setTextColor(textColor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExamClick(exam);
            }
        });
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    private String formatDateStr(String dateStr) {
        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputSdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        try {
            Date date = inputSdf.parse(dateStr);
            if (date != null) return outputSdf.format(date);
        } catch (ParseException e) {
            // fallback
        }
        return dateStr;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvWeight, tvHeight, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_exam_date);
            tvWeight = itemView.findViewById(R.id.tv_exam_weight);
            tvHeight = itemView.findViewById(R.id.tv_exam_height);
            tvStatus = itemView.findViewById(R.id.tv_exam_status);
        }
    }
}
