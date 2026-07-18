package com.example.simbalita.ui.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private TextView tvTotalBalita, tvPemeriksaanHariIni;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        tvTotalBalita = view.findViewById(R.id.tv_total_balita);
        tvPemeriksaanHariIni = view.findViewById(R.id.tv_pemeriksaan_hari_ini);

        androidx.cardview.widget.CardView cvTotalBalita = view.findViewById(R.id.cv_total_balita);
        if (cvTotalBalita != null) {
            cvTotalBalita.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(requireContext(), DataPesertaActivity.class);
                startActivity(intent);
            });
        }

        androidx.cardview.widget.CardView cvPemeriksaanHariIni = view.findViewById(R.id.cv_pemeriksaan_hari_ini);
        if (cvPemeriksaanHariIni != null) {
            cvPemeriksaanHariIni.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(requireContext(), DataPesertaActivity.class);
                intent.putExtra("filter_today", true);
                startActivity(intent);
            });
        }

        ImageView ivLogout = view.findViewById(R.id.iv_admin_logout);
        if (ivLogout != null) {
            ivLogout.setOnClickListener(v -> {
                if (requireActivity() instanceof AdminMainActivity) {
                    ((AdminMainActivity) requireActivity()).logout();
                }
            });
        }

        updateDashboardStats();

        return view;
    }

    private void updateDashboardStats() {
        int totalKids = dbHelper.getAllChildren().size();
        tvTotalBalita.setText(String.valueOf(totalKids));

        // Get count of exams today
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String todayStr = sdf.format(new Date());
        int examsToday = dbHelper.getCheckupsCountByDate(todayStr);
        tvPemeriksaanHariIni.setText(String.valueOf(examsToday));
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDashboardStats();
    }
}
