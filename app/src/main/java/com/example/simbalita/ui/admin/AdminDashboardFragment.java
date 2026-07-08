package com.example.simbalita.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminDashboardFragment extends Fragment {

    private TextView tvTotalChildren, tvExamsToday;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        tvTotalChildren = view.findViewById(R.id.tv_dash_balita_value);
        tvExamsToday = view.findViewById(R.id.tv_dash_exams_value);

        updateDashboardStats();

        return view;
    }

    private void updateDashboardStats() {
        int totalKids = dbHelper.getAllChildren().size();
        tvTotalChildren.setText(String.valueOf(totalKids));

        // Get count of exams today
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayStr = sdf.format(new Date());
        int examsToday = dbHelper.getCheckupsCountByDate(todayStr);
        tvExamsToday.setText(String.valueOf(examsToday));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDashboardStats();
    }
}
