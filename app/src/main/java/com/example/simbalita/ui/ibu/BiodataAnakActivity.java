package com.example.simbalita.ui.ibu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BiodataAnakActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvNama, tvGender, tvBirthDate, tvAge, tvWeight, tvHeight, tvIbuName;
    private BottomNavigationView bottomNav;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodata_anak);

        dbHelper = new DatabaseHelper(this);

        // Bind Views
        btnBack = findViewById(R.id.btn_back_anak);
        tvNama = findViewById(R.id.tv_anak_nama);
        tvGender = findViewById(R.id.tv_anak_gender);
        tvBirthDate = findViewById(R.id.tv_anak_birth_date);
        tvAge = findViewById(R.id.tv_anak_age);
        tvWeight = findViewById(R.id.tv_anak_weight);
        tvHeight = findViewById(R.id.tv_anak_height);
        tvIbuName = findViewById(R.id.tv_anak_ibu);
        bottomNav = findViewById(R.id.bottom_nav_biodata_anak);

        // Get details
        SharedPreferences pref = getSharedPreferences("simbalita_prefs", MODE_PRIVATE);
        int childId = pref.getInt("selected_child_id", -1);
        String motherName = pref.getString("user_name", "-");

        tvIbuName.setText(motherName);

        if (childId != -1) {
            Child child = dbHelper.getChildById(childId);
            if (child != null) {
                tvNama.setText(child.getName());
                tvGender.setText("L".equalsIgnoreCase(child.getGender()) || "Laki-laki".equalsIgnoreCase(child.getGender()) ? "Laki-laki" : "Perempuan");
                tvBirthDate.setText(child.getBirthDate());
                tvWeight.setText(String.format(Locale.US, "%.1f Kg", child.getBirthWeight()));
                tvHeight.setText(String.format(Locale.US, "%.1f Cm", child.getBirthHeight()));

                // Umur calculation
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String today = sdf.format(new Date());
                int ageMonths = DatabaseHelper.calculateAgeInMonths(child.getBirthDate(), today);
                tvAge.setText(DatabaseHelper.formatAge(ageMonths));
            } else {
                Toast.makeText(this, "Data anak tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Tidak ada anak terpilih", Toast.LENGTH_SHORT).show();
        }

        // Back button click
        btnBack.setOnClickListener(v -> finish());

        // Highlight "Profil" tab by default
        bottomNav.setSelectedItemId(R.id.menu_profil);

        // Bottom Nav logic
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = new Intent(BiodataAnakActivity.this, IbuMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            if (itemId == R.id.menu_home) {
                intent.putExtra("target_tab", "home");
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.menu_profil) {
                intent.putExtra("target_tab", "profil");
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });
    }
}
