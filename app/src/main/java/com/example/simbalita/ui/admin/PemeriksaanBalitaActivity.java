package com.example.simbalita.ui.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.Examination;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PemeriksaanBalitaActivity extends AppCompatActivity {

    private EditText etChildName, etDate, etWeight, etHeight, etStatus;
    private Button btnSave;
    private ImageView ivBack;

    private DatabaseHelper dbHelper;
    private int childId = -1;
    private Child currentChild;
    private Calendar calendar = Calendar.getInstance();
    private String selectedSqlDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pemeriksaan_balita);

        dbHelper = new DatabaseHelper(this);
        childId = getIntent().getIntExtra("child_id", -1);

        if (childId == -1) {
            Toast.makeText(this, "Balita tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentChild = dbHelper.getChildById(childId);
        if (currentChild == null) {
            Toast.makeText(this, "Data balita kosong", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind Views
        ivBack = findViewById(R.id.btn_back_pemeriksaan_balita);
        etChildName = findViewById(R.id.et_exam_child_name);
        etDate = findViewById(R.id.et_exam_date);
        etWeight = findViewById(R.id.et_exam_weight);
        etHeight = findViewById(R.id.et_exam_height);
        etStatus = findViewById(R.id.et_exam_status);
        btnSave = findViewById(R.id.btn_exam_save);

        ivBack.setOnClickListener(v -> finish());

        // Pre-fill child name
        etChildName.setText(currentChild.getName());

        // Set default date to today
        setDefaultDate();

        // Date dialog dropdown trigger
        etDate.setOnClickListener(v -> showDatePicker());

        // Setup TextWatchers to auto-calculate status
        TextWatcher calculationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                autoCalculateStatus();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        etWeight.addTextChangedListener(calculationWatcher);
        etHeight.addTextChangedListener(calculationWatcher);

        btnSave.setOnClickListener(v -> saveExamination());
    }

    private void setDefaultDate() {
        Date today = new Date();
        SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        SimpleDateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        
        etDate.setText(displaySdf.format(today));
        selectedSqlDate = sqlSdf.format(today);
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            etDate.setText(displaySdf.format(calendar.getTime()));

            SimpleDateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            selectedSqlDate = sqlSdf.format(calendar.getTime());
            
            autoCalculateStatus();
        };

        new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void autoCalculateStatus() {
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (weightStr.isEmpty() || heightStr.isEmpty() || selectedSqlDate.isEmpty()) {
            etStatus.setText("");
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);

            String status = dbHelper.calculateNutritionalStatus(childId, selectedSqlDate, weight, height);
            etStatus.setText(status);
        } catch (NumberFormatException e) {
            etStatus.setText("");
        }
    }

    private void saveExamination() {
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (weightStr.isEmpty()) {
            etWeight.setError("Berat badan harus diisi");
            etWeight.requestFocus();
            return;
        }

        if (heightStr.isEmpty()) {
            etHeight.setError("Tinggi badan harus diisi");
            etHeight.requestFocus();
            return;
        }

        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);
        
        // Calculate status
        String status = dbHelper.calculateNutritionalStatus(childId, selectedSqlDate, weight, height);

        Examination exam = new Examination();
        exam.setChildId(childId);
        exam.setDate(selectedSqlDate);
        exam.setWeight(weight);
        exam.setHeight(height);
        exam.setStatus(status);

        long result = dbHelper.addExamination(exam);
        if (result != -1) {
            Toast.makeText(this, "Pemeriksaan berhasil disimpan! Status: " + status, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal menyimpan pemeriksaan", Toast.LENGTH_SHORT).show();
        }
    }
}
