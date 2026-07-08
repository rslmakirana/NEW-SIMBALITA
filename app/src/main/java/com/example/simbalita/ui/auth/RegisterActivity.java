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

    private EditText etName, etPhone, etPassword, etNik, etAddress, etUsername;
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
        etNik = findViewById(R.id.et_register_nik);
        etAddress = findViewById(R.id.et_register_address);
        etUsername = findViewById(R.id.et_register_username);
        
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
        String nik = etNik.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String username = etUsername.getText().toString().trim().toLowerCase(); // Normalize to lowercase

        if (name.isEmpty()) {
            etName.setError("Nama tidak boleh kosong");
            etName.requestFocus();
            return;
        }

        if (nik.isEmpty()) {
            etNik.setError("NIK tidak boleh kosong");
            etNik.requestFocus();
            return;
        }

        if (nik.length() != 16) {
            etNik.setError("NIK harus 16 digit");
            etNik.requestFocus();
            return;
        }

        if (username.isEmpty()) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 4) {
            etUsername.setError("Username minimal 4 karakter");
            etUsername.requestFocus();
            return;
        }

        if (username.contains(" ")) {
            etUsername.setError("Username tidak boleh mengandung spasi");
            etUsername.requestFocus();
            return;
        }

        if (dbHelper.isUsernameExists(username)) {
            etUsername.setError("Username sudah digunakan! Coba yang lain (misal: " + username + "123 atau " + username + "_).");
            etUsername.requestFocus();
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

        if (address.isEmpty()) {
            etAddress.setError("Alamat tidak boleh kosong");
            etAddress.requestFocus();
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
        user.setNik(nik);
        user.setAddress(address);
        user.setUsername(username);
        user.setRole("IBU");

        long result = dbHelper.registerUser(user);
        if (result != -1) {
            Toast.makeText(this, "Registrasi Berhasil! Silakan masuk.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Registrasi Gagal! Silakan coba lagi.", Toast.LENGTH_LONG).show();
        }
    }
}
