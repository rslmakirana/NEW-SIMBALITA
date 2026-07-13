package com.example.simbalita.ui.ibu;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class IbuMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibu_main);

        bottomNav = findViewById(R.id.bottom_nav_ibu);

        // Load default fragment (Home)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_ibu, new IbuHomeFragment())
                    .commit();
        }

        // Bottom Navigation listener (2 Tabs: Home, Profil)
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.menu_home) {
                selectedFragment = new IbuHomeFragment();
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

        handleIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent();
    }

    private void handleIntent() {
        if (getIntent() != null) {
            String tab = getIntent().getStringExtra("target_tab");
            if ("home".equals(tab)) {
                bottomNav.setSelectedItemId(R.id.menu_home);
            } else if ("profil".equals(tab)) {
                bottomNav.setSelectedItemId(R.id.menu_profil);
            }
            getIntent().removeExtra("target_tab");
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_ibu, fragment)
                .commit();
    }

    // Support logout
    public void logout() {
        getSharedPreferences("simbalita_prefs", MODE_PRIVATE).edit().clear().apply();
        Intent intent = new Intent(IbuMainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
