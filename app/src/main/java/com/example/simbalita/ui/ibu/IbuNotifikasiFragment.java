package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.adapter.NotificationAdapter;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.Notification;
import java.util.ArrayList;
import java.util.List;
import com.example.simbalita.model.Schedule;

public class IbuNotifikasiFragment extends Fragment {

    private RecyclerView rvNotifications;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;
    private int motherId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_notifikasi, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        // Bind views
        rvNotifications = view.findViewById(R.id.rv_notifications);
        tvEmpty = view.findViewById(R.id.tv_notif_empty);

        // Shared preferences
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        motherId = pref.getInt("user_id", -1);
        int childId = pref.getInt("selected_child_id", -1);

        String childName = "Anak Anda";
        if (childId != -1) {
            Child child = dbHelper.getChildById(childId);
            if (child != null) {
                childName = child.getName();
            }
        } else {
            List<Child> kids = dbHelper.getChildrenByMother(motherId);
            if (!kids.isEmpty()) {
                childName = kids.get(0).getName();
            }
        }

        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Ambil jadwal dari database
        List<Notification> list = new ArrayList<>();

        List<Schedule> schedules = dbHelper.getAllSchedules();

        for (Schedule schedule : schedules) {

            String title = "Jadwal Posyandu";

            String body =
                    schedule.getTitle()
                            + "\nTanggal : " + schedule.getDate()
                            + "\nJam : " + schedule.getTime()
                            + "\nLokasi : " + schedule.getLocation();

            list.add(new Notification(
                    schedule.getId(),
                    title,
                    body,
                    schedule.getDate(),
                    "schedule"
            ));
        }
        NotificationAdapter adapter = new NotificationAdapter(requireContext(), list);
        rvNotifications.setAdapter(adapter);

        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
        }

        return view;
    }
}
