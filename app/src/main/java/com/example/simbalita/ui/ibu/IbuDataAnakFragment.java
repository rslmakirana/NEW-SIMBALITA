package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.ui.admin.AddChildActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IbuDataAnakFragment extends Fragment {

    private TextView tvBirthDate, tvAge, tvGender, tvBirthWeight, tvBirthHeight, tvChildName;
    private Button btnViewHistory;
    private ImageView ivEdit;
    private CardView cvDetails;
    private LinearLayout llNoChildData;
    
    private DatabaseHelper dbHelper;
    private Child currentChild;
    private int motherId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_data_anak, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        // Get Mother ID
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        motherId = pref.getInt("user_id", -1);

        // Bind views
        tvChildName = view.findViewById(R.id.tv_data_child_name);
        tvBirthDate = view.findViewById(R.id.tv_detail_birth_date);
        tvAge = view.findViewById(R.id.tv_detail_age);
        tvGender = view.findViewById(R.id.tv_detail_gender);
        tvBirthWeight = view.findViewById(R.id.tv_detail_birth_weight);
        tvBirthHeight = view.findViewById(R.id.tv_detail_birth_height);
        btnViewHistory = view.findViewById(R.id.btn_view_history);
        ivEdit = view.findViewById(R.id.iv_data_edit);
        cvDetails = view.findViewById(R.id.cv_child_details);
        llNoChildData = view.findViewById(R.id.ll_no_child_data);

        // Drawer Menu Toggle
        ImageView ivMenu = view.findViewById(R.id.iv_data_menu);
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> {
                if (requireActivity() instanceof IbuMainActivity) {
                    ((IbuMainActivity) requireActivity()).openDrawer();
                }
            });
        }

        // Read selected child from preferences
        int childId = pref.getInt("selected_child_id", -1);

        if (childId == -1) {
            // Find first child of mother
            List<Child> kids = dbHelper.getChildrenByMother(motherId);
            if (!kids.isEmpty()) {
                currentChild = kids.get(0);
                pref.edit().putInt("selected_child_id", currentChild.getId()).apply();
            }
        } else {
            currentChild = dbHelper.getChildById(childId);
        }

        displayChildData();

        // Edit button click
        ivEdit.setOnClickListener(v -> {
            if (currentChild != null) {
                Intent intent = new Intent(requireActivity(), AddChildActivity.class);
                intent.putExtra("child_id", currentChild.getId());
                intent.putExtra("is_edit", true);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Tidak ada data anak untuk diedit.", Toast.LENGTH_SHORT).show();
            }
        });

        // View History click
        btnViewHistory.setOnClickListener(v -> {
            if (currentChild != null) {
                Intent intent = new Intent(requireActivity(), RiwayatPemeriksaanActivity.class);
                intent.putExtra("child_id", currentChild.getId());
                startActivity(intent);
            }
        });

        return view;
    }

    private void displayChildData() {
        if (currentChild == null) {
            cvDetails.setVisibility(View.GONE);
            btnViewHistory.setVisibility(View.GONE);
            ivEdit.setVisibility(View.GONE);
            tvChildName.setVisibility(View.GONE);
            llNoChildData.setVisibility(View.VISIBLE);
            return;
        }

        cvDetails.setVisibility(View.VISIBLE);
        btnViewHistory.setVisibility(View.VISIBLE);
        ivEdit.setVisibility(View.VISIBLE);
        tvChildName.setVisibility(View.VISIBLE);
        llNoChildData.setVisibility(View.GONE);

        tvChildName.setText(currentChild.getName());

        // Friendly birth date
        String dob = currentChild.getBirthDate();
        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputSdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        try {
            Date date = inputSdf.parse(dob);
            if (date != null) dob = outputSdf.format(date);
        } catch (ParseException e) {
            // fallback
        }
        tvBirthDate.setText(dob);

        // Age calculation
        String today = inputSdf.format(new Date());
        int ageMonths = DatabaseHelper.calculateAgeInMonths(currentChild.getBirthDate(), today);
        tvAge.setText(DatabaseHelper.formatAge(ageMonths));

        tvGender.setText(currentChild.getGender());
        tvBirthWeight.setText(String.format(Locale.US, "%.1f Kg", currentChild.getBirthWeight()));
        tvBirthHeight.setText(String.format(Locale.US, "%.1f cm", currentChild.getBirthHeight()));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data in case child was edited
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        int childId = pref.getInt("selected_child_id", -1);
        if (childId != -1) {
            currentChild = dbHelper.getChildById(childId);
        } else {
            List<Child> kids = dbHelper.getChildrenByMother(motherId);
            if (!kids.isEmpty()) {
                currentChild = kids.get(0);
                pref.edit().putInt("selected_child_id", currentChild.getId()).apply();
            }
        }
        displayChildData();
    }
}
