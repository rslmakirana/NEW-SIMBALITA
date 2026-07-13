package com.example.simbalita.ui.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Schedule;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

    private EditText etDate, etTime, etTitle, etLocation;
    private Button btnSave;
    private ImageView ivBack;
    
    private DatabaseHelper dbHelper;
    private boolean isEdit = false;
    private int scheduleId = -1;
    private String selectedSqlDate = "";
    private Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        dbHelper = new DatabaseHelper(this);

        etDate = findViewById(R.id.et_sch_date);
        etTime = findViewById(R.id.et_sch_time);
        etTitle = findViewById(R.id.et_sch_title);
        etLocation = findViewById(R.id.et_sch_location);
        btnSave = findViewById(R.id.btn_sch_save);
        ivBack = findViewById(R.id.btn_back_add_schedule);

        ivBack.setOnClickListener(v -> finish());

        // Date Picker dropdown dialog trigger
        etDate.setOnClickListener(v -> showDatePicker());

        // Time Picker dropdown dialog trigger
        etTime.setOnClickListener(v -> showTimePicker());

        // Check if editing
        isEdit = getIntent().getBooleanExtra("is_edit", false);
        scheduleId = getIntent().getIntExtra("schedule_id", -1);

        if (isEdit && scheduleId != -1) {
            loadScheduleData();
        } else {
            // Set default hints
            etTime.setText("08:00 WIB");
            etTitle.setText("Posyandu Melati 1");
            etLocation.setText("Jl. Melati No. 10");
        }

        btnSave.setOnClickListener(v -> saveSchedule());
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
        };

        new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String timeStr = String.format(Locale.getDefault(), "%02d:%02d WIB", hourOfDay, minuteOfHour);
            etTime.setText(timeStr);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void loadScheduleData() {
        Schedule sch = null;
        for (Schedule s : dbHelper.getAllSchedules()) {
            if (s.getId() == scheduleId) {
                sch = s;
                break;
            }
        }

        if (sch != null) {
            selectedSqlDate = sch.getDate();
            SimpleDateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            try {
                Date date = sqlSdf.parse(sch.getDate());
                if (date != null) {
                    etDate.setText(displaySdf.format(date));
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                etDate.setText(sch.getDate());
            }

            etTime.setText(sch.getTime());
            etTitle.setText(sch.getTitle());
            etLocation.setText(sch.getLocation());
        }
    }

    private void saveSchedule() {
        String displayDate = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (displayDate.isEmpty() || selectedSqlDate.isEmpty()) {
            etDate.setError("Pilih tanggal");
            etDate.requestFocus();
            return;
        }

        if (time.isEmpty()) {
            etTime.setError("Waktu harus diisi");
            etTime.requestFocus();
            return;
        }

        if (title.isEmpty()) {
            etTitle.setError("Nama Posyandu harus diisi");
            etTitle.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            etLocation.setError("Alamat harus diisi");
            etLocation.requestFocus();
            return;
        }

        Schedule sch = new Schedule();
        sch.setDate(selectedSqlDate);
        sch.setTime(time);
        sch.setTitle(title);
        sch.setLocation(location);

        if (isEdit) {
            sch.setId(scheduleId);
            boolean success = dbHelper.updateSchedule(sch);
            if (success) {
                Toast.makeText(this, "Jadwal berhasil diubah", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal mengubah jadwal", Toast.LENGTH_SHORT).show();
            }
        } else {
            long result = dbHelper.addSchedule(sch);
            if (result != -1) {
                Toast.makeText(this, "Jadwal berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal menyimpan jadwal", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
