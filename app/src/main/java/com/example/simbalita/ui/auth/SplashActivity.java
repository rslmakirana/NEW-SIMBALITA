package com.example.simbalita.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Check persistent user session
        android.content.SharedPreferences pref = getSharedPreferences("simbalita_prefs", android.content.Context.MODE_PRIVATE);
        int userId = pref.getInt("user_id", -1);
        String role = pref.getString("user_role", null);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (userId != -1 && role != null) {
                if (role.equals("ADMIN")) {
                    intent = new Intent(SplashActivity.this, com.example.simbalita.ui.admin.AdminMainActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, com.example.simbalita.ui.ibu.IbuMainActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2500);
    }
}
