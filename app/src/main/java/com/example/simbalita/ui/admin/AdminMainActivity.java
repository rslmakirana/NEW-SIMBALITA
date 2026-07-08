package com.example.simbalita.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Apply Admin Theme
        setTheme(R.style.Theme_Simbalita_Admin);
        setContentView(R.layout.activity_admin_main);

        bottomNav = findViewById(R.id.bottom_nav_admin);

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_admin, new AdminDashboardFragment())
                    .commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.menu_admin_dashboard) {
                selectedFragment = new AdminDashboardFragment();
            } else if (itemId == R.id.menu_admin_peserta) {
                selectedFragment = new AdminDataPesertaFragment();
            } else if (itemId == R.id.menu_admin_pemeriksaan) {
                selectedFragment = new AdminPemeriksaanFragment();
            } else if (itemId == R.id.menu_admin_jadwal) {
                selectedFragment = new AdminJadwalFragment();
            } else if (itemId == R.id.menu_admin_laporan) {
                selectedFragment = new AdminLaporanFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_admin, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    public void logout() {
        getSharedPreferences("simbalita_prefs", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
