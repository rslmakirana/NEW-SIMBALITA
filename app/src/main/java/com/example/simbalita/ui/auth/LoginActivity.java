package com.example.simbalita.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.User;
import com.example.simbalita.ui.admin.AdminMainActivity;
import com.example.simbalita.ui.ibu.IbuMainActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgot;
    private ImageView ivTogglePassword;
    private DatabaseHelper dbHelper;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        // Bind views
        etUsername = findViewById(R.id.et_login_username);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_login_register_now);
        tvForgot = findViewById(R.id.tv_forgot_password);
        ivTogglePassword = findViewById(R.id.iv_login_toggle_password);

        // Password visibility toggle
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            isPasswordVisible = !isPasswordVisible;
            etPassword.setSelection(etPassword.length());
        });

        // Redirect to Register
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Forgot password redirection
        tvForgot.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Handle Login button
        btnLogin.setOnClickListener(v -> performLogin());
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim().toLowerCase(); // Normalize to lowercase
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        User user = dbHelper.authenticateUser(username, password);
        if (user != null) {
            // Save user details to SharedPreferences
            SharedPreferences pref = getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("user_id", user.getId());
            editor.putString("user_name", user.getName());
            editor.putString("user_phone", user.getPhone());
            editor.putString("user_role", user.getRole());
            editor.putString("user_nik", user.getNik());
            editor.putString("user_address", user.getAddress());
            editor.putString("user_username", user.getUsername());
            editor.apply();

            Toast.makeText(this, "Login Berhasil sebagai " + (user.getRole().equals("ADMIN") ? "Kader" : "Ibu"), Toast.LENGTH_SHORT).show();

            // Redirect based on role
            Intent intent;
            if (user.getRole().equals("ADMIN")) {
                intent = new Intent(LoginActivity.this, AdminMainActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, IbuMainActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Username atau Password salah!", Toast.LENGTH_SHORT).show();
        }
    }
}
