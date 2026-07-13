package com.example.simbalita.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.adapter.ScheduleAdapter;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Schedule;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class AdminJadwalFragment extends Fragment {

    private RecyclerView rvSchedules;
    private TextView tvEmpty;
    private ImageView btnBack;
    private Button btnTambahJadwal;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_jadwal, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        rvSchedules = view.findViewById(R.id.rv_admin_schedules);
        tvEmpty = view.findViewById(R.id.tv_admin_schedules_empty);
        btnBack = view.findViewById(R.id.btn_back_jadwal);
        btnTambahJadwal = view.findViewById(R.id.btn_tambah_jadwal);

        rvSchedules.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Back button click -> Switch tab to Home
        btnBack.setOnClickListener(v -> {
            if (requireActivity() instanceof AdminMainActivity) {
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_nav_admin);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.menu_admin_dashboard);
                }
            }
        });

        // Add schedule button click
        btnTambahJadwal.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddScheduleActivity.class);
            startActivity(intent);
        });

        loadSchedules();

        return view;
    }

    private void loadSchedules() {
        List<Schedule> list = dbHelper.getAllSchedules();
        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvSchedules.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvSchedules.setVisibility(View.VISIBLE);

            ScheduleAdapter adapter = new ScheduleAdapter(list, true, new ScheduleAdapter.OnScheduleActionListener() {
                @Override
                public void onEdit(Schedule schedule) {
                    Intent intent = new Intent(requireActivity(), AddScheduleActivity.class);
                    intent.putExtra("schedule_id", schedule.getId());
                    intent.putExtra("is_edit", true);
                    startActivity(intent);
                }

                @Override
                public void onDelete(Schedule schedule) {
                    showDeleteConfirmDialog(schedule);
                }
            });
            rvSchedules.setAdapter(adapter);
        }
    }

    private void showDeleteConfirmDialog(Schedule schedule) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Jadwal")
                .setMessage("Apakah Anda yakin ingin menghapus jadwal posyandu tanggal " + schedule.getDate() + "?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    dbHelper.deleteSchedule(schedule.getId());
                    Toast.makeText(requireContext(), "Jadwal berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadSchedules();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSchedules();
    }
}
