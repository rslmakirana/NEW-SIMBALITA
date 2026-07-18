package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.Examination;
import com.example.simbalita.model.Schedule;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IbuHomeFragment extends Fragment {

    private TextView tvHomeWelcome, tvChildName;
    private CardView cvNamaAnak;
    private ImageView ivHomeBell;

    private LinearLayout llPosyanduList;
    private TextView tvPosyanduEmpty;

    private KmsGraphView kmsGraphView;
    private TextView tvGraphEmpty;

    private DatabaseHelper dbHelper;
    private int motherId;
    private String motherName = "Ibu";
    private List<Child> childList = new ArrayList<>();
    private Child selectedChild = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_home, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        // Session preferences
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        motherId = pref.getInt("user_id", -1);
        motherName = pref.getString("user_name", "Ibu");

        // Bind Views
        tvHomeWelcome = view.findViewById(R.id.tv_home_welcome);
        tvChildName = view.findViewById(R.id.tv_child_name);
        cvNamaAnak = view.findViewById(R.id.cv_nama_anak);
        ivHomeBell = view.findViewById(R.id.iv_home_bell);

        llPosyanduList = view.findViewById(R.id.ll_home_posyandu_list);
        tvPosyanduEmpty = view.findViewById(R.id.tv_home_posyandu_empty);

        kmsGraphView = view.findViewById(R.id.home_kms_graph_view);
        tvGraphEmpty = view.findViewById(R.id.tv_home_graph_empty);

        // Load children data & KMS Graph
        loadChildData();

        // Load posyandu schedules (1 monthly schedule)
        loadPosyanduSchedules();

        // Clicking NAMA ANAK card to view child biodata
        cvNamaAnak.setOnClickListener(v -> {
            if (childList.isEmpty()) {
                Toast.makeText(requireContext(), "Belum ada anak terdaftar. Hubungi Kader Posyandu.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(requireContext(), BiodataAnakActivity.class);
                startActivity(intent);
            }
        });

        // Clicking notification bell icon
        ivHomeBell.setOnClickListener(v -> {
            if (requireActivity() instanceof IbuMainActivity) {
                ((IbuMainActivity) requireActivity()).loadFragment(new IbuNotifikasiFragment());
            }
        });

        return view;
    }

    private void loadChildData() {
        childList = dbHelper.getChildrenByMother(motherId);
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        int savedChildId = pref.getInt("selected_child_id", -1);

        if (!childList.isEmpty()) {
            selectedChild = childList.get(0);
            if (savedChildId != -1) {
                for (Child c : childList) {
                    if (c.getId() == savedChildId) {
                        selectedChild = c;
                        break;
                    }
                }
            }
            pref.edit().putInt("selected_child_id", selectedChild.getId()).apply();
            
            // Greeting above box: "HALO, IBU [MotherName] & [ChildName]"
            String greetingText = "HALO, IBU " + motherName.toUpperCase() + " & " + selectedChild.getName().toUpperCase();
            tvHomeWelcome.setText(greetingText);
            
            // Box contains child's name only
            tvChildName.setText(selectedChild.getName().toUpperCase());
            
            // Load direct KMS Graph
            loadDirectKmsGraph();
        } else {
            selectedChild = null;
            tvHomeWelcome.setText("HALO, IBU " + motherName.toUpperCase());
            tvChildName.setText("(Belum Ada Anak Terdaftar)");
            kmsGraphView.setVisibility(View.GONE);
            tvGraphEmpty.setVisibility(View.VISIBLE);
            tvGraphEmpty.setText("Daftarkan anak terlebih dahulu untuk melihat grafik perkembangan.");
        }
    }

    private void loadDirectKmsGraph() {
        if (selectedChild == null) return;
        List<Examination> exams = dbHelper.getExaminationsByChild(selectedChild.getId());
        
        if (exams.isEmpty()) {
            kmsGraphView.setVisibility(View.GONE);
            tvGraphEmpty.setVisibility(View.VISIBLE);
            tvGraphEmpty.setText("Belum ada data pemeriksaan untuk membuat grafik.");
        } else {
            kmsGraphView.setVisibility(View.VISIBLE);
            tvGraphEmpty.setVisibility(View.GONE);
            kmsGraphView.setData(exams, selectedChild.getBirthDate());
        }
    }

    private void loadPosyanduSchedules() {
        llPosyanduList.removeAllViews();
        List<Schedule> list = dbHelper.getAllSchedules();

        if (list.isEmpty()) {
            tvPosyanduEmpty.setVisibility(View.VISIBLE);
        } else {
            tvPosyanduEmpty.setVisibility(View.GONE);
            // Limit to next 1 schedule (monthly posyandu schedule)
            int count = Math.min(list.size(), 1);
            for (int i = 0; i < count; i++) {
                Schedule sch = list.get(i);
                View row = LayoutInflater.from(requireContext()).inflate(R.layout.item_schedule, llPosyanduList, false);
                
                TextView tvDateTime = row.findViewById(R.id.tv_sch_datetime);
                TextView tvTitle = row.findViewById(R.id.tv_sch_title);
                TextView tvLocation = row.findViewById(R.id.tv_sch_location);
                TextView tvStatus = row.findViewById(R.id.tv_sch_status);

                tvDateTime.setText(sch.getDate() + " - " + sch.getTime());
                tvTitle.setText(sch.getTitle());
                tvLocation.setText(sch.getLocation());
                if (tvStatus != null) {
                    if (sch.getStatus() != null) {
                        tvStatus.setText("Status: " + sch.getStatus());
                    } else {
                        tvStatus.setText("Status: Belum Terlaksana");
                    }
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 12);
                row.setLayoutParams(params);

                llPosyanduList.addView(row);
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        loadChildData();
        loadPosyanduSchedules();
    }
}
