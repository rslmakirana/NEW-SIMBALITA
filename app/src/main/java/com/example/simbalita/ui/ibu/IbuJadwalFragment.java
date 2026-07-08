package com.example.simbalita.ui.ibu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.adapter.ScheduleAdapter;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Schedule;
import java.util.List;

public class IbuJadwalFragment extends Fragment {

    private RecyclerView rvSchedules;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_jadwal, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        rvSchedules = view.findViewById(R.id.rv_schedules);
        tvEmpty = view.findViewById(R.id.tv_schedules_empty);

        rvSchedules.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Drawer Menu Toggle
        android.widget.ImageView ivMenu = view.findViewById(R.id.iv_sch_menu);
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> {
                if (requireActivity() instanceof IbuMainActivity) {
                    ((IbuMainActivity) requireActivity()).openDrawer();
                }
            });
        }

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
            
            ScheduleAdapter adapter = new ScheduleAdapter(list, false, null);
            rvSchedules.setAdapter(adapter);
        }
    }
}
