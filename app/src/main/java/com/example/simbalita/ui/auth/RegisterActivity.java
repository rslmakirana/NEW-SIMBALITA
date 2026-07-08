package com.example.simbalita.ui.auth;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etPhone, etPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private ImageView ivBack, ivTogglePassword;
    private DatabaseHelper dbHelper;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        // Bind views
        etName = findViewById(R.id.et_register_name);
        etPhone = findViewById(R.id.et_register_phone);
        etPassword = findViewById(R.id.et_register_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_register_login_now);
        ivBack = findViewById(R.id.iv_register_back);
        ivTogglePassword = findViewById(R.id.iv_register_toggle_password);

        // Back button
        ivBack.setOnClickListener(v -> finish());

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

        // Redirect to login
        tvLogin.setOnClickListener(v -> finish());

        // Handle registration button
        btnRegister.setOnClickListener(v -> performRegistration());
    }

    private void performRegistration() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError("Nama tidak boleh kosong");
            etName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("No HP tidak boleh kosong");
            etPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            etPhone.setError("No HP minimal 10 digit");
            etPhone.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setPassword(password);
        user.setRole("IBU");

        long result = dbHelper.registerUser(user);
        if (result != -1) {
            Toast.makeText(this, "Registrasi Berhasil! Silakan masuk.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Registrasi Gagal! No HP mungkin sudah digunakan.", Toast.LENGTH_LONG).show();
        }
    }
}
