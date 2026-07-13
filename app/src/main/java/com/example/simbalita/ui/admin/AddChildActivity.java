package com.example.simbalita.ui.admin;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
    
    private DatabaseHelper dbHelper;
    private boolean isEdit = false;
    private int childId = -1;
    private String selectedSqlDate = ""; // yyyy-MM-dd
    private String userRole = "ADMIN";
    private int currentUserId = -1;
    
    private List<User> motherList = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dbHelper = new DatabaseHelper(this);

        // Read user role
        SharedPreferences pref = getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        userRole = pref.getString("user_role", "ADMIN");
        currentUserId = pref.getInt("user_id", -1);

        setContentView(R.layout.activity_add_child);

        // Bind Views
        ivBack = findViewById(R.id.btn_back_add_child);
        etName = findViewById(R.id.et_add_child_name);
        etBirthDate = findViewById(R.id.et_add_child_birth_date);
        spGender = findViewById(R.id.sp_add_child_gender);
        etBirthWeight = findViewById(R.id.et_add_child_weight);
        etBirthHeight = findViewById(R.id.et_add_child_height);
        spMother = findViewById(R.id.sp_add_child_mother);
        btnSave = findViewById(R.id.btn_add_child_save);

        ivBack.setOnClickListener(v -> finish());

        // Setup Genders Dropdown
        String[] genders = {"Laki-laki", "Perempuan"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, genders);
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spGender.setAdapter(genderAdapter);

        // Check if editing
        isEdit = getIntent().getBooleanExtra("is_edit", false);
        childId = getIntent().getIntExtra("child_id", -1);

        // Setup Mothers Dropdown
        setupMotherSpinner();

        // Date Picker dropdown-like action
        etBirthDate.setOnClickListener(v -> showDatePicker());

        if (isEdit && childId != -1) {
            loadChildData();
        }

        btnSave.setOnClickListener(v -> saveChildData());
    }

    private void setupMotherSpinner() {
        List<User> allMothers = dbHelper.getMothers();
        motherList = new ArrayList<>();

        for (User mother : allMothers) {
            List<Child> kids = dbHelper.getChildrenByMother(mother.getId());
            if (kids.isEmpty()) {
                motherList.add(mother);
            } else if (isEdit && childId != -1) {
                Child currentChild = dbHelper.getChildById(childId);
                if (currentChild != null && currentChild.getMotherId() == mother.getId()) {
                    motherList.add(mother);
                }
            }
        }

        if (motherList.isEmpty()) {
            spMother.setEnabled(false);
            List<String> emptyList = new ArrayList<>();
            emptyList.add("Tidak ada Ibu yang tersedia");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, emptyList);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spMother.setAdapter(adapter);
        } else {
            spMother.setEnabled(true);
            List<String> motherNames = new ArrayList<>();
            for (User mother : motherList) {
                motherNames.add(mother.getName() + " (" + mother.getPhone() + ")");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, motherNames);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spMother.setAdapter(adapter);
        }
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            etBirthDate.setText(displaySdf.format(calendar.getTime()));

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

            if (child.getGender().equals("Laki-laki")) {
                spGender.setSelection(0);
            } else {
                spGender.setSelection(1);
            }

            etBirthWeight.setText(String.valueOf(child.getBirthWeight()));
            etBirthHeight.setText(String.valueOf(child.getBirthHeight()));

            for (int i = 0; i < motherList.size(); i++) {
                if (motherList.get(i).getId() == child.getMotherId()) {
                    spMother.setSelection(i);
                    break;
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

        if (motherList.isEmpty()) {
            Toast.makeText(this, "Silakan registrasikan akun Ibu terlebih dahulu!", Toast.LENGTH_LONG).show();
            return;
        }
        int selectedMotherPos = spMother.getSelectedItemPosition();
        int targetMotherId = motherList.get(selectedMotherPos).getId();

        if (!isEdit) {
            List<Child> existingChildren = dbHelper.getChildrenByMother(targetMotherId);
            if (!existingChildren.isEmpty()) {
                Toast.makeText(this, "Ibu ini sudah memiliki anak terdaftar!", Toast.LENGTH_LONG).show();
                return;
            }
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
