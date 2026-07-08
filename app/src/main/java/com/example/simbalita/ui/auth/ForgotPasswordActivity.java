package com.example.simbalita.ui.auth;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etUsername, etNik, etNewPassword;
    private Button btnReset;
    private ImageView ivBack, ivTogglePassword;
    private DatabaseHelper dbHelper;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbHelper = new DatabaseHelper(this);

        // Bind views
        etUsername = findViewById(R.id.et_forgot_username);
        etNik = findViewById(R.id.et_forgot_nik);
        etNewPassword = findViewById(R.id.et_forgot_new_password);
        btnReset = findViewById(R.id.btn_forgot_reset);
        ivBack = findViewById(R.id.iv_forgot_back);
        ivTogglePassword = findViewById(R.id.iv_forgot_toggle_password);

        // Back button
        ivBack.setOnClickListener(v -> finish());

        // Password visibility toggle
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                etNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
            isPasswordVisible = !isPasswordVisible;
            etNewPassword.setSelection(etNewPassword.length());
        });

        // Handle Reset Button click
        btnReset.setOnClickListener(v -> performResetPassword());
    }

    private void performResetPassword() {
        String username = etUsername.getText().toString().trim().toLowerCase();
        String nik = etNik.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Username tidak boleh kosong");
            etUsername.requestFocus();
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

        if (newPassword.isEmpty()) {
            etNewPassword.setError("Password baru tidak boleh kosong");
            etNewPassword.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            etNewPassword.setError("Password baru minimal 6 karakter");
            etNewPassword.requestFocus();
            return;
        }

        boolean success = dbHelper.resetPassword(username, nik, newPassword);
        if (success) {
            Toast.makeText(this, "Password berhasil disetel ulang! Silakan masuk kembali.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Reset Gagal! Username atau NIK tidak cocok dengan database.", Toast.LENGTH_LONG).show();
        }
    }
}
