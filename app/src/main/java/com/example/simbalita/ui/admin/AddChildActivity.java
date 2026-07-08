package com.example.simbalita.ui.admin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddChildActivity extends AppCompatActivity {

    private EditText etName, etBirthDate, etBirthWeight, etBirthHeight;
    private Spinner spGender, spMother;
    private Button btnSave;
    private ImageView ivBack;
    private TextView tvTitle, tvNoMotherWarning;
    private View vHeader;
    
    private DatabaseHelper dbHelper;
    private boolean isEdit = false;
    private int childId = -1;
    private String selectedSqlDate = ""; // stores yyyy-MM-dd
    private String userRole = "ADMIN";
    private int currentUserId = -1;
    
    private List<User> motherList = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbHelper = new DatabaseHelper(this);

        // Read user role and details
        SharedPreferences pref = getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        userRole = pref.getString("user_role", "ADMIN");
        currentUserId = pref.getInt("user_id", -1);

        // Set role based theme before inflating layout
        if (userRole.equals("ADMIN")) {
            setTheme(R.style.Theme_Simbalita_Admin);
        } else {
            setTheme(R.style.Theme_Simbalita_Ibu);
        }
        
        setContentView(R.layout.activity_add_child);

        // Bind views
        vHeader = findViewById(R.id.v_add_child_header);
        ivBack = findViewById(R.id.iv_add_child_back);
        tvTitle = findViewById(R.id.tv_add_child_title);
        etName = findViewById(R.id.et_child_name);
        etBirthDate = findViewById(R.id.et_child_birth_date);
        spGender = findViewById(R.id.sp_child_gender);
        etBirthWeight = findViewById(R.id.et_child_birth_weight);
        etBirthHeight = findViewById(R.id.et_child_birth_height);
        spMother = findViewById(R.id.sp_child_mother);
        tvNoMotherWarning = findViewById(R.id.tv_no_mother_warning);
        btnSave = findViewById(R.id.btn_child_save);

        // Setup theme colors dynamically
        applyThemeColors();

        ivBack.setOnClickListener(v -> finish());

        // Setup Genders
        String[] genders = {"Laki-laki", "Perempuan"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        // Setup Mothers spinner
        setupMotherSpinner();

        // Date Picker Dialog
        etBirthDate.setOnClickListener(v -> showDatePicker());

        // Check if editing
        isEdit = getIntent().getBooleanExtra("is_edit", false);
        childId = getIntent().getIntExtra("child_id", -1);

        if (isEdit && childId != -1) {
            tvTitle.setText("Edit Data Balita");
            loadChildData();
        }

        btnSave.setOnClickListener(v -> saveChildData());
    }

    private void applyThemeColors() {
        if (!userRole.equals("ADMIN")) {
            // Apply Mother Green Theme
            int greenColor = ContextCompat.getColor(this, R.color.primary_ibu);
            vHeader.setBackgroundColor(greenColor);
            btnSave.setBackgroundTintList(ColorStateList.valueOf(greenColor));
        }
    }

    private void setupMotherSpinner() {
        if (userRole.equals("IBU")) {
            // Mother role: she can only choose herself! Hide the selector and save the mother ID directly.
            findViewById(R.id.sp_child_mother).setVisibility(View.GONE);
            // also hide title label for Pilih Ibu
            spMother.setVisibility(View.GONE);
            return;
        }

        // Admin role: fetch all mothers
        motherList = dbHelper.getMothers();

        if (motherList.isEmpty()) {
            tvNoMotherWarning.setVisibility(View.VISIBLE);
            spMother.setEnabled(false);
            
            List<String> emptyList = new ArrayList<>();
            emptyList.add("Tidak ada Ibu terdaftar");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, emptyList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spMother.setAdapter(adapter);
        } else {
            tvNoMotherWarning.setVisibility(View.GONE);
            spMother.setEnabled(true);

            List<String> motherNames = new ArrayList<>();
            for (User mother : motherList) {
                motherNames.add(mother.getName() + " (" + mother.getPhone() + ")");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, motherNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spMother.setAdapter(adapter);
        }
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Set displayed date e.g. "10 Mei 2024"
            SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            etBirthDate.setText(displaySdf.format(calendar.getTime()));

            // Store database formatted date "yyyy-MM-dd"
            SimpleDateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            selectedSqlDate = sqlSdf.format(calendar.getTime());
        };

        new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void loadChildData() {
        Child child = dbHelper.getChildById(childId);
        if (child != null) {
            etName.setText(child.getName());
            
            // Set birth date
            selectedSqlDate = child.getBirthDate();
            SimpleDateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            try {
                Date date = sqlSdf.parse(child.getBirthDate());
                if (date != null) {
                    etBirthDate.setText(displaySdf.format(date));
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                etBirthDate.setText(child.getBirthDate());
            }

            // Set Gender selection
            if (child.getGender().equals("Laki-laki")) {
                spGender.setSelection(0);
            } else {
                spGender.setSelection(1);
            }

            etBirthWeight.setText(String.valueOf(child.getBirthWeight()));
            etBirthHeight.setText(String.valueOf(child.getBirthHeight()));

            // Set Mother selection
            if (userRole.equals("ADMIN")) {
                for (int i = 0; i < motherList.size(); i++) {
                    if (motherList.get(i).getId() == child.getMotherId()) {
                        spMother.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void saveChildData() {
        String name = etName.getText().toString().trim();
        String displayDate = etBirthDate.getText().toString().trim();
        String weightStr = etBirthWeight.getText().toString().trim();
        String heightStr = etBirthHeight.getText().toString().trim();
        String gender = spGender.getSelectedItem().toString();

        if (name.isEmpty()) {
            etName.setError("Nama anak tidak boleh kosong");
            etName.requestFocus();
            return;
        }

        if (displayDate.isEmpty() || selectedSqlDate.isEmpty()) {
            etBirthDate.setError("Pilih tanggal lahir");
            etBirthDate.requestFocus();
            return;
        }

        if (weightStr.isEmpty()) {
            etBirthWeight.setError("Berat lahir tidak boleh kosong");
            etBirthWeight.requestFocus();
            return;
        }

        if (heightStr.isEmpty()) {
            etBirthHeight.setError("Tinggi lahir tidak boleh kosong");
            etBirthHeight.requestFocus();
            return;
        }

        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);

        int targetMotherId = -1;
        if (userRole.equals("IBU")) {
            targetMotherId = currentUserId;
        } else {
            if (motherList.isEmpty()) {
                Toast.makeText(this, "Silakan registrasikan akun Ibu terlebih dahulu!", Toast.LENGTH_LONG).show();
                return;
            }
            int selectedMotherPos = spMother.getSelectedItemPosition();
            targetMotherId = motherList.get(selectedMotherPos).getId();
        }

        Child child = new Child();
        child.setName(name);
        child.setBirthDate(selectedSqlDate);
        child.setGender(gender);
        child.setBirthWeight(weight);
        child.setBirthHeight(height);
        child.setMotherId(targetMotherId);

        if (isEdit) {
            child.setId(childId);
            boolean success = dbHelper.updateChild(child);
            if (success) {
                Toast.makeText(this, "Data anak berhasil diubah", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal mengubah data", Toast.LENGTH_SHORT).show();
            }
        } else {
            long result = dbHelper.addChild(child);
            if (result != -1) {
                Toast.makeText(this, "Balita berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
