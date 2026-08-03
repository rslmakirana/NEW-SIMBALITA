package com.example.simbalita.ui.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Notification;
import com.example.simbalita.model.Schedule;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

    private TextView tvFormTitle;
    private Spinner spTitle, spLocation, spStatus;
    private EditText etTitleCustom, etDate, etTime, etPesan;
    private Button btnSave;
    private ImageView ivBack;

    private DatabaseHelper dbHelper;
    private boolean isEdit = false;
    private int scheduleId = -1;
    private String selectedSqlDate = "";
    private Calendar calendar = Calendar.getInstance();

    private final String[] jenisJadwalOptions = {
            "Jadwal Posyandu",
            "Jadwal Imunisasi DPT",
            "Pemberian Vitamin A",
            "Pengingat Kehadiran",
            "+ Judul Kustom..."
    };

    private final String[] lokasiOptions = {
            "Posyandu Melati 1",
            "Posyandu Melati 2",
            "Posyandu Mawar",
            "Posyandu Kenanga"
    };

    private final String[] statusOptions = {
            "Belum Terlaksana",
            "Sedang Dilaksanakan",
            "Selesai"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        dbHelper = new DatabaseHelper(this);

        tvFormTitle = findViewById(R.id.tv_form_title);
        spTitle = findViewById(R.id.sp_sch_title);
        etTitleCustom = findViewById(R.id.et_sch_title_custom);
        etDate = findViewById(R.id.et_sch_date);
        etTime = findViewById(R.id.et_sch_time);
        spLocation = findViewById(R.id.sp_sch_location);
        spStatus = findViewById(R.id.sp_sch_status);
        etPesan = findViewById(R.id.et_sch_pesan);
        btnSave = findViewById(R.id.btn_sch_save);
        ivBack = findViewById(R.id.btn_back_add_schedule);

        // Setup Spinners with black text layout
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, jenisJadwalOptions);
        titleAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spTitle.setAdapter(titleAdapter);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, lokasiOptions);
        locationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spLocation.setAdapter(locationAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);

        spTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == jenisJadwalOptions.length - 1) {
                    etTitleCustom.setVisibility(View.VISIBLE);
                } else {
                    etTitleCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ivBack.setOnClickListener(v -> finish());

        // Date Picker & Time Picker
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        // Check if editing existing schedule
        isEdit = getIntent().getBooleanExtra("is_edit", false);
        scheduleId = getIntent().getIntExtra("schedule_id", -1);

        if (isEdit && scheduleId != -1) {
            tvFormTitle.setText("EDIT JADWAL");
            btnSave.setText("SIMPAN PERUBAHAN");
            loadScheduleData();
        } else {
            tvFormTitle.setText("TAMBAH JADWAL");
            btnSave.setText("KIRIM NOTIFIKASI & SIMPAN");
            // Set default date & time
            SimpleDateFormat displaySdf = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            SimpleDateFormat sqlSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            etDate.setText(displaySdf.format(calendar.getTime()));
            selectedSqlDate = sqlSdf.format(calendar.getTime());
            etTime.setText("08.00 WIB");
            etPesan.setText("Jadwal terbaru telah ditambahkan oleh Kader. Harap membawa buku KIA dan hadir tepat waktu.");
        }

        btnSave.setOnClickListener(v -> saveScheduleAndNotify());
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
            String timeStr = String.format(Locale.getDefault(), "%02d.%02d WIB", hourOfDay, minuteOfHour);
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
            
            // Set spinner title selection if matches
            String title = sch.getTitle();
            boolean foundTitle = false;
            for (int i = 0; i < jenisJadwalOptions.length - 1; i++) {
                if (jenisJadwalOptions[i].equalsIgnoreCase(title)) {
                    spTitle.setSelection(i);
                    foundTitle = true;
                    break;
                }
            }
            if (!foundTitle) {
                spTitle.setSelection(jenisJadwalOptions.length - 1);
                etTitleCustom.setVisibility(View.VISIBLE);
                etTitleCustom.setText(title);
            }

            // Set spinner location selection if matches
            String location = sch.getLocation();
            if (location != null) {
                for (int j = 0; j < lokasiOptions.length; j++) {
                    if (lokasiOptions[j].equalsIgnoreCase(location)) {
                        spLocation.setSelection(j);
                        break;
                    }
                }
            }

            // Set spinner status selection if matches
            String status = sch.getStatus();
            if (status != null) {
                for (int k = 0; k < statusOptions.length; k++) {
                    if (statusOptions[k].equalsIgnoreCase(status)) {
                        spStatus.setSelection(k);
                        break;
                    }
                }
            }

            etPesan.setText("Perubahan jadwal posyandu: " + title + " pada " + etDate.getText().toString() + " di " + (location != null ? location : "Posyandu"));
        }
    }

    private void saveScheduleAndNotify() {
        String title;
        if (spTitle.getSelectedItemPosition() == jenisJadwalOptions.length - 1) {
            title = etTitleCustom.getText().toString().trim();
            if (title.isEmpty()) {
                etTitleCustom.setError("Judul kustom wajib diisi");
                return;
            }
        } else {
            title = spTitle.getSelectedItem().toString();
        }

        String displayDate = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String location = spLocation.getSelectedItem().toString();
        String status = spStatus.getSelectedItem().toString();
        String pesan = etPesan.getText().toString().trim();

        if (displayDate.isEmpty() || selectedSqlDate.isEmpty()) {
            etDate.setError("Pilih tanggal");
            return;
        }

        if (time.isEmpty()) {
            etTime.setError("Waktu harus diisi");
            return;
        }

        if (pesan.isEmpty()) {
            etPesan.setError("Pesan notifikasi wajib diisi");
            return;
        }

        // 1. Simpan/Update Schedule
        Schedule sch = new Schedule();
        sch.setDate(selectedSqlDate);
        sch.setTime(time);
        sch.setTitle(title);
        sch.setLocation(location);
        sch.setStatus(status);

        if (isEdit) {
            sch.setId(scheduleId);
            dbHelper.updateSchedule(sch);
        } else {
            dbHelper.addSchedule(sch);
        }

        // 2. Simpan/Sync ke Notifikasi agar langsung tampil di layar Ibu User
        String iconType = "bell";
        if (title.contains("Posyandu")) {
            iconType = "schedule";
        } else if (title.contains("Imunisasi")) {
            iconType = "vaccine";
        }

        String notifBody = pesan + "\n📅 " + displayDate + ", " + time + " • " + location;
        Notification notif = new Notification(0, title, notifBody, "", iconType);
        dbHelper.addNotification(notif);

        Toast.makeText(this, isEdit ? "Jadwal & notifikasi berhasil diperbarui!" : "Berhasil! Notifikasi telah dikirim ke seluruh user.", Toast.LENGTH_LONG).show();
        finish();
    }
}
