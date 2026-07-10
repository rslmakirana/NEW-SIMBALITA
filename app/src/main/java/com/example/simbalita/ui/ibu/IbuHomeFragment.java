package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.Examination;
import com.example.simbalita.model.Schedule;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IbuHomeFragment extends Fragment {

    private TextView tvWelcome, tvWeightValue, tvStatusValue;
    private Spinner spChild;
    private CardView cvScheduleCard;
    private TextView tvSchDate, tvSchTime, tvSchTitle, tvSchLocation;
    private TextView tvNoChildAlert;
    private LinearLayout llMeasurements;
    
    private DatabaseHelper dbHelper;
    private int motherId;
    private List<Child> childList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_home, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        // Read preferences
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        motherId = pref.getInt("user_id", -1);
        String motherName = pref.getString("user_name", "Ibu");

        // Bind views
        tvWelcome = view.findViewById(R.id.tv_home_welcome);
        tvWeightValue = view.findViewById(R.id.tv_home_weight_value);
        tvStatusValue = view.findViewById(R.id.tv_home_status_value);
        spChild = view.findViewById(R.id.sp_home_child);
        llMeasurements = view.findViewById(R.id.ll_measurements);
        tvNoChildAlert = view.findViewById(R.id.tv_no_child_alert);
        
        cvScheduleCard = view.findViewById(R.id.cv_home_schedule);
        tvSchDate = view.findViewById(R.id.tv_home_sch_date);
        tvSchTime = view.findViewById(R.id.tv_home_sch_time);
        tvSchTitle = view.findViewById(R.id.tv_home_sch_title);
        tvSchLocation = view.findViewById(R.id.tv_home_sch_location);

        tvWelcome.setText("Halo, " + motherName + " 👋");

        // Drawer Menu Toggle
        ImageView ivMenu = view.findViewById(R.id.iv_home_menu);
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> {
                if (requireActivity() instanceof IbuMainActivity) {
                    ((IbuMainActivity) requireActivity()).openDrawer();
                }
            });
        }

        // Notification icon click listener
        ImageView ivNotification = view.findViewById(R.id.iv_home_notification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> {
                if (requireActivity() instanceof IbuMainActivity) {
                    IbuMainActivity main = (IbuMainActivity) requireActivity();
                    com.google.android.material.bottomnavigation.BottomNavigationView nav = main.findViewById(R.id.bottom_nav_ibu);
                    if (nav != null) {
                        nav.setSelectedItemId(R.id.menu_notifikasi);
                    }
                }
            });
        }

        // Card clicks redirection
        if (llMeasurements != null) {
            llMeasurements.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), RiwayatPemeriksaanActivity.class);
                startActivity(intent);
            });
        }

        if (cvScheduleCard != null) {
            cvScheduleCard.setOnClickListener(v -> {
                if (requireActivity() instanceof IbuMainActivity) {
                    IbuMainActivity main = (IbuMainActivity) requireActivity();
                    main.loadFragment(new IbuJadwalFragment());
                    main.uncheckBottomNav();
                }
            });
        }

        loadChildrenData();
        loadUpcomingSchedule();

        return view;
    }

    private void loadChildrenData() {
        childList = dbHelper.getChildrenByMother(motherId);

        if (childList.isEmpty()) {
            tvNoChildAlert.setVisibility(View.VISIBLE);
            llMeasurements.setVisibility(View.GONE);
            spChild.setEnabled(false);
            
            List<String> emptyList = new ArrayList<>();
            emptyList.add("Tidak ada balita terdaftar");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, emptyList);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spChild.setAdapter(adapter);
            return;
        }

        tvNoChildAlert.setVisibility(View.GONE);
        llMeasurements.setVisibility(View.VISIBLE);
        spChild.setEnabled(true);

        List<String> spinnerItems = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String today = sdf.format(new Date());

        for (Child child : childList) {
            int ageMonths = DatabaseHelper.calculateAgeInMonths(child.getBirthDate(), today);
            spinnerItems.add(child.getName() + " (" + DatabaseHelper.formatAge(ageMonths) + ")");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, spinnerItems);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spChild.setAdapter(adapter);

        // Retrieve last selected child from prefs if any
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        int savedChildId = pref.getInt("selected_child_id", -1);
        int defaultSelection = 0;
        if (savedChildId != -1) {
            for (int i = 0; i < childList.size(); i++) {
                if (childList.get(i).getId() == savedChildId) {
                    defaultSelection = i;
                    break;
                }
            }
        }
        spChild.setSelection(defaultSelection);

        spChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Child selectedChild = childList.get(position);
                
                // Save selection
                pref.edit().putInt("selected_child_id", selectedChild.getId()).apply();

                loadLatestCheckup(selectedChild.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadLatestCheckup(int childId) {
        Examination exam = dbHelper.getLatestExamination(childId);
        if (exam != null) {
            tvWeightValue.setText(String.format(Locale.US, "%.1f Kg", exam.getWeight()));
            tvStatusValue.setText(exam.getStatus());

            // Status color matching
            int textColor;
            switch (exam.getStatus()) {
                case "Normal":
                    textColor = ContextCompat.getColor(requireContext(), R.color.status_normal);
                    break;
                case "Kurang":
                case "Lebih":
                    textColor = Color.parseColor("#7F5F00");
                    break;
                case "Stunting":
                default:
                    textColor = ContextCompat.getColor(requireContext(), R.color.status_danger);
                    break;
            }
            tvStatusValue.setTextColor(textColor);
        } else {
            tvWeightValue.setText("-- Kg");
            tvStatusValue.setText("Belum Diperiksa");
            tvStatusValue.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        }
    }

    private void loadUpcomingSchedule() {
        Schedule schedule = dbHelper.getUpcomingSchedule();
        if (schedule != null) {
            cvScheduleCard.setVisibility(View.VISIBLE);
            
            // Format Date
            String dateFormatted = schedule.getDate();
            SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat outputSdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
            try {
                Date date = inputSdf.parse(schedule.getDate());
                if (date != null) dateFormatted = outputSdf.format(date);
            } catch (ParseException e) {
                // fallback
            }

            tvSchDate.setText(dateFormatted);
            tvSchTime.setText(schedule.getTime());
            tvSchTitle.setText(schedule.getTitle());
            tvSchLocation.setText(schedule.getLocation());
        } else {
            cvScheduleCard.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh child selection data when returning to home screen (in case child was updated or checkups added by Admin)
        loadChildrenData();
    }
}
