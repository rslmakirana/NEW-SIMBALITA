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
import com.example.simbalita.model.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChildAdminAdapter extends RecyclerView.Adapter<ChildAdminAdapter.ViewHolder> {

    private final List<Child> childList;
    private final DatabaseHelper dbHelper;
    private final OnChildClickListener listener;
    private final boolean filterToday;

    public interface OnChildClickListener {
        void onChildClick(Child child);
    }

    public ChildAdminAdapter(List<Child> childList, DatabaseHelper dbHelper, boolean filterToday, OnChildClickListener listener) {
        this.childList = childList;
        this.dbHelper = dbHelper;
        this.filterToday = filterToday;
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
        holder.tvName.setText(child.getName().toUpperCase());

        // Calculate age
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String today = sdf.format(new Date());
        int ageMonths = DatabaseHelper.calculateAgeInMonths(child.getBirthDate(), today);
        holder.tvAge.setText("(" + DatabaseHelper.formatAge(ageMonths) + ")");

        if (filterToday) {
            com.example.simbalita.model.Examination exam = dbHelper.getLatestExamination(child.getId());
            if (exam != null) {
                holder.tvInfo.setText("Hasil Pemeriksaan: " + exam.getStatus());
            } else {
                holder.tvInfo.setText("Hasil Pemeriksaan: -");
            }
        } else {
            // Gender & Mother details
            String genderStr = "L".equalsIgnoreCase(child.getGender()) || "Laki-laki".equalsIgnoreCase(child.getGender()) ? "Laki-laki" : "Perempuan";
            String motherName = "-";
            User mother = dbHelper.getUserById(child.getMotherId());
            if (mother != null) {
                motherName = mother.getName();
            }
            holder.tvInfo.setText(genderStr + " | Ibu: " + motherName);
        }

        // Hide arrow and disable click feedback
        holder.ivArrow.setVisibility(android.view.View.GONE);
        holder.itemView.setOnClickListener(null);
        holder.itemView.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAge, tvInfo;
        ImageView ivArrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_child_name);
            tvAge = itemView.findViewById(R.id.tv_child_age);
            tvInfo = itemView.findViewById(R.id.tv_child_gender);
            ivArrow = itemView.findViewById(R.id.iv_child_arrow);
        }
    }
}
