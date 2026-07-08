package com.example.simbalita.ui.ibu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IbuMainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNav;
    private DatabaseHelper dbHelper;
    private int motherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibu_main);

        dbHelper = new DatabaseHelper(this);

        // Bind Views
        drawerLayout = findViewById(R.id.drawer_layout_ibu);
        navigationView = findViewById(R.id.nav_view_ibu);
        bottomNav = findViewById(R.id.bottom_nav_ibu);

        // Read Mother ID
        SharedPreferences pref = getSharedPreferences("simbalita_prefs", MODE_PRIVATE);
        motherId = pref.getInt("user_id", -1);

        // Load default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_ibu, new IbuHomeFragment())
                    .commit();
        }

        // Bottom Navigation listener (3 Tabs: Home, Notifikasi, Profil)
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                selectedFragment = new IbuHomeFragment();
            } else if (itemId == R.id.menu_notifikasi) {
                selectedFragment = new IbuNotifikasiFragment();
            } else if (itemId == R.id.menu_profil) {
                selectedFragment = new IbuProfilFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_ibu, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);

            if (itemId == R.id.nav_child_profile) {
                loadFragment(new IbuDataAnakFragment());
                uncheckBottomNav();
                return true;
            } else if (itemId == R.id.nav_kms_graph) {
                Intent intent = new Intent(IbuMainActivity.this, KmsActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_pemeriksaan_history) {
                Intent intent = new Intent(IbuMainActivity.this, RiwayatPemeriksaanActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_imunisasi_history) {
                Intent intent = new Intent(IbuMainActivity.this, ImunisasiActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_schedule) {
                loadFragment(new IbuJadwalFragment());
                uncheckBottomNav();
                return true;
            } else if (itemId == R.id.nav_edukasi) {
                loadFragment(new IbuEdukasiFragment());
                uncheckBottomNav();
                return true;
            }
            return false;
        });

        // Initialize and refresh drawer child profile
        refreshDrawerHeader();
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_ibu, fragment)
                .commit();
    }

    public void uncheckBottomNav() {
        int size = bottomNav.getMenu().size();
        for (int i = 0; i < size; i++) {
            bottomNav.getMenu().getItem(i).setCheckable(false);
        }
        // Restore checkability on click
        bottomNav.setOnItemSelectedListener(item -> {
            int size2 = bottomNav.getMenu().size();
            for (int j = 0; j < size2; j++) {
                bottomNav.getMenu().getItem(j).setCheckable(true);
            }
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                selectedFragment = new IbuHomeFragment();
            } else if (itemId == R.id.menu_notifikasi) {
                selectedFragment = new IbuNotifikasiFragment();
            } else if (itemId == R.id.menu_profil) {
                selectedFragment = new IbuProfilFragment();
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    public void openDrawer() {
        if (drawerLayout != null) {
            refreshDrawerHeader(); // refresh child data before drawer opens
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    public void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void refreshDrawerHeader() {
        View headerView = navigationView.getHeaderView(0);
        if (headerView == null) return;

        TextView tvName = headerView.findViewById(R.id.tv_nav_child_name);
        TextView tvAge = headerView.findViewById(R.id.tv_nav_child_age);
        TextView tvLink = headerView.findViewById(R.id.tv_nav_view_profile);
        ImageView ivAvatar = headerView.findViewById(R.id.iv_nav_child_avatar);

        // Get selected child ID
        SharedPreferences pref = getSharedPreferences("simbalita_prefs", MODE_PRIVATE);
        int childId = pref.getInt("selected_child_id", -1);
        Child child = null;

        if (childId != -1) {
            child = dbHelper.getChildById(childId);
        } else {
            List<Child> kids = dbHelper.getChildrenByMother(motherId);
            if (!kids.isEmpty()) {
                child = kids.get(0);
                pref.edit().putInt("selected_child_id", child.getId()).apply();
            }
        }

        if (child != null) {
            tvName.setText(child.getName());
            
            // Age calculation
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String today = sdf.format(new Date());
            int ageMonths = DatabaseHelper.calculateAgeInMonths(child.getBirthDate(), today);
            tvAge.setText(DatabaseHelper.formatAge(ageMonths));
            
            tvLink.setVisibility(View.VISIBLE);
            tvLink.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                loadFragment(new IbuDataAnakFragment());
                uncheckBottomNav();
            });
        } else {
            tvName.setText("Belum Ada Anak");
            tvAge.setText("Hubungi Kader untuk mendaftar");
            tvLink.setVisibility(View.GONE);
        }
    }

    // Support logout manually
    public void logout() {
        getSharedPreferences("simbalita_prefs", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(IbuMainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
