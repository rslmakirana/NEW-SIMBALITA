package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.Examination;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImunisasiActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvImunisasi;
    private Button btnNext;

    private DatabaseHelper dbHelper;
    private Child currentChild;
    private int childAgeMonths = 0;
    private int examCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imunisasi);

        dbHelper = new DatabaseHelper(this);

        ivBack = findViewById(R.id.iv_imunisasi_back);
        rvImunisasi = findViewById(R.id.rv_imunisasi);
        btnNext = findViewById(R.id.btn_next_imunisasi);

        ivBack.setOnClickListener(v -> finish());

        // Get selected child ID
        SharedPreferences pref = getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        int childId = pref.getInt("selected_child_id", -1);
        int motherId = pref.getInt("user_id", -1);

        if (childId == -1) {
            List<Child> kids = dbHelper.getChildrenByMother(motherId);
            if (!kids.isEmpty()) {
                currentChild = kids.get(0);
                pref.edit().putInt("selected_child_id", currentChild.getId()).apply();
            }
        } else {
            currentChild = dbHelper.getChildById(childId);
        }

        rvImunisasi.setLayoutManager(new LinearLayoutManager(this));

        if (currentChild != null) {
            // Calculate child age
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String today = sdf.format(new Date());
            childAgeMonths = DatabaseHelper.calculateAgeInMonths(currentChild.getBirthDate(), today);
            
            // Get examination count
            List<Examination> exams = dbHelper.getExaminationsByChild(currentChild.getId());
            examCount = exams.size();
        }

        setupVaccineList();

        btnNext.setOnClickListener(v -> {
            Toast.makeText(this, "Silakan ikuti jadwal Posyandu berikutnya untuk melengkapi imunisasi.", Toast.LENGTH_LONG).show();
        });
    }

    private void setupVaccineList() {
        List<VaccineItem> list = new ArrayList<>();
        list.add(new VaccineItem("BCG", "0 - 1 Bulan", 0, 1));
        list.add(new VaccineItem("Polio 1", "1 Bulan", 1, 1));
        list.add(new VaccineItem("DPT-HB-Hib 1", "2 Bulan", 2, 2));
        list.add(new VaccineItem("Polio 2", "2 Bulan", 2, 2));
        list.add(new VaccineItem("DPT-HB-Hib 2", "3 Bulan", 3, 3));
        list.add(new VaccineItem("Polio 3", "3 Bulan", 3, 3));
        list.add(new VaccineItem("Campak-Rubella", "9 Bulan", 9, 4));

        rvImunisasi.setAdapter(new VaccineAdapter(list));
    }

    // Helper model for vaccine item
    private static class VaccineItem {
        String name;
        String ageLabel;
        int targetAge;
        int requiredCheckups;

        public VaccineItem(String name, String ageLabel, int targetAge, int requiredCheckups) {
            this.name = name;
            this.ageLabel = ageLabel;
            this.targetAge = targetAge;
            this.requiredCheckups = requiredCheckups;
        }
    }

    private class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.ViewHolder> {
        private final List<VaccineItem> items;

        public VaccineAdapter(List<VaccineItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_imunisasi, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VaccineItem item = items.get(position);
            holder.tvName.setText(item.name);
            holder.tvAge.setText(item.ageLabel);

            // Determine vaccine completion status based on age and examinations count
            boolean isCompleted = (childAgeMonths >= item.targetAge && examCount >= item.requiredCheckups);

            if (isCompleted) {
                holder.tvStatus.setText("Selesai");
                holder.tvStatus.setTextColor(ContextCompat.getColor(ImunisasiActivity.this, R.color.primary_dark_ibu));
                holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ImunisasiActivity.this, R.color.status_normal_light)));
            } else {
                holder.tvStatus.setText("Belum");
                holder.tvStatus.setTextColor(Color.parseColor("#E53935")); // Red
                holder.tvStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFCDD2"))); // Light Red
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvAge, tvStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_imu_item_name);
                tvAge = itemView.findViewById(R.id.tv_imu_item_age);
                tvStatus = itemView.findViewById(R.id.tv_imu_item_status);
            }
        }
    }
}
