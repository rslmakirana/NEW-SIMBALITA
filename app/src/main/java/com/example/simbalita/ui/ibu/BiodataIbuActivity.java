package com.example.simbalita.ui.ibu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BiodataIbuActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvUsername, tvNama, tvNik, tvPhone, tvAlamat;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodata_ibu);

        // Bind Views
        btnBack = findViewById(R.id.btn_back_ibu);
        tvUsername = findViewById(R.id.tv_ibu_username);
        tvNama = findViewById(R.id.tv_ibu_nama);
        tvNik = findViewById(R.id.tv_ibu_nik);
        tvPhone = findViewById(R.id.tv_ibu_phone);
        tvAlamat = findViewById(R.id.tv_ibu_alamat);
        bottomNav = findViewById(R.id.bottom_nav_biodata_ibu);

        // Retrieve Ibu details from SharedPreferences
        SharedPreferences pref = getSharedPreferences("simbalita_prefs", MODE_PRIVATE);
        String name = pref.getString("user_name", "-");
        String nik = pref.getString("user_nik", "-");
        String username = pref.getString("user_username", "-");
        String phone = pref.getString("user_phone", "-");
        String address = pref.getString("user_address", "-");

        // Display Data
        tvUsername.setText(username);
        tvNama.setText(name);
        tvNik.setText(nik);
        tvPhone.setText(phone);
        tvAlamat.setText(address);

        // Back button click
        btnBack.setOnClickListener(v -> finish());

        // Highlight "Profil" tab
        bottomNav.setSelectedItemId(R.id.menu_profil);

        // Bottom Nav logic
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = new Intent(BiodataIbuActivity.this, IbuMainActivity.class);
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
