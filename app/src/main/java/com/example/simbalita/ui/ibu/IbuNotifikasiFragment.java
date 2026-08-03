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
import com.example.simbalita.adapter.NotificationAdapter;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Notification;
import com.example.simbalita.model.Schedule;
import java.util.List;

public class IbuNotifikasiFragment extends Fragment {

    private RecyclerView rvNotifications;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_notifikasi, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        // Bind views
        rvNotifications = view.findViewById(R.id.rv_notifications);
        tvEmpty = view.findViewById(R.id.tv_notif_empty);

        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        List<Notification> list = dbHelper.getAllNotifications();
        List<Schedule> schedules = dbHelper.getAllSchedules();

        // Gabungkan seluruh jadwal yang ada di database ke dalam daftar notifikasi
        for (Schedule sch : schedules) {
            boolean exists = false;
            for (Notification n : list) {
                if (n.getTitle().contains(sch.getTitle()) || (sch.getDate() != null && n.getBody().contains(sch.getDate()))) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                String iconType = "schedule";
                if (sch.getTitle() != null && sch.getTitle().contains("Imunisasi")) iconType = "vaccine";
                if (sch.getTitle() != null && (sch.getTitle().contains("Vitamin") || sch.getTitle().contains("Pengingat"))) iconType = "bell";

                String notifBody = "Jadwal kegiatan " + sch.getTitle() + " pada " + sch.getDate() + " jam " + sch.getTime() + " di " + sch.getLocation();
                list.add(new Notification(sch.getId(), sch.getTitle(), notifBody, sch.getDate(), iconType));
            }
        }

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            NotificationAdapter adapter = new NotificationAdapter(requireContext(), list);
            rvNotifications.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotifications();
    }
}
