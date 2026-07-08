package com.example.simbalita.ui.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.Examination;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminPemeriksaanFragment extends Fragment {

    private Spinner spChild;
    private EditText etDate, etWeight, etHeight, etStatus;
    private Button btnSave;
    
    private DatabaseHelper dbHelper;
    private List<Child> childList = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private String selectedSqlDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_pemeriksaan, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        spChild = view.findViewById(R.id.sp_exam_child);
        etDate = view.findViewById(R.id.et_exam_date);
        etWeight = view.findViewById(R.id.et_exam_weight);
        etHeight = view.findViewById(R.id.et_exam_height);
        etStatus = view.findViewById(R.id.et_exam_status);
        btnSave = view.findViewById(R.id.btn_exam_save);

        // Setup date picker
        etDate.setOnClickListener(v -> showDatePicker());

        // Set default date to today
        setDefaultDate();

        // Load children list
        loadChildren();

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

        spChild.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                autoCalculateStatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Save Click
        btnSave.setOnClickListener(v -> saveExamination());

        return view;
    }

    private void setDefaultDate() {
        Date today = new Date();
        SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
        SimpleDateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        
        etDate.setText(displaySdf.format(today));
        selectedSqlDate = sqlSdf.format(today);
    }

    private void loadChildren() {
        childList = dbHelper.getAllChildren();

        if (childList.isEmpty()) {
            btnSave.setEnabled(false);
            List<String> emptyList = new ArrayList<>();
            emptyList.add("Tidak ada balita terdaftar");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, emptyList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spChild.setAdapter(adapter);
        } else {
            btnSave.setEnabled(true);
            List<String> childNames = new ArrayList<>();
            for (Child child : childList) {
                childNames.add(child.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, childNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spChild.setAdapter(adapter);
        }
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
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void autoCalculateStatus() {
        if (childList.isEmpty() || spChild.getSelectedItemPosition() < 0) return;
        
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (weightStr.isEmpty() || heightStr.isEmpty() || selectedSqlDate.isEmpty()) {
            etStatus.setText("");
            return;
        }

        try {
            double weight = Double.parseDouble(weightStr);
            double height = Double.parseDouble(heightStr);
            int selectedChildPos = spChild.getSelectedItemPosition();
            int childId = childList[selectedChildPos].getId();

            String status = dbHelper.calculateNutritionalStatus(childId, selectedSqlDate, weight, height);
            etStatus.setText(status);
        } catch (NumberFormatException e) {
            etStatus.setText("");
        }
    }

    private void saveExamination() {
        if (childList.isEmpty()) {
            Toast.makeText(requireContext(), "Daftarkan balita terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

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
        
        int selectedChildPos = spChild.getSelectedItemPosition();
        int childId = childList[selectedChildPos].getId();

        // Calculate final status
        String status = dbHelper.calculateNutritionalStatus(childId, selectedSqlDate, weight, height);

        Examination exam = new Examination();
        exam.setChildId(childId);
        exam.setDate(selectedSqlDate);
        exam.setWeight(weight);
        exam.setHeight(height);
        exam.setStatus(status);

        long result = dbHelper.addExamination(exam);
        if (result != -1) {
            Toast.makeText(requireContext(), "Pemeriksaan berhasil disimpan! Status: " + status, Toast.LENGTH_LONG).show();
            // Reset form
            etWeight.setText("");
            etHeight.setText("");
            etStatus.setText("");
            setDefaultDate();
        } else {
            Toast.makeText(requireContext(), "Gagal menyimpan pemeriksaan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadChildren();
        autoCalculateStatus();
    }
}
