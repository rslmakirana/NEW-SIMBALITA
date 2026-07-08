package com.example.simbalita.ui.ibu;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.simbalita.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailPemeriksaanActivity extends AppCompatActivity {

    private TextView tvDate, tvWeight, tvHeight, tvStatus;
    private ImageView ivStatusIcon, ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pemeriksaan);

        tvDate = findViewById(R.id.tv_det_exam_date);
        tvWeight = findViewById(R.id.tv_det_exam_weight);
        tvHeight = findViewById(R.id.tv_det_exam_height);
        tvStatus = findViewById(R.id.tv_det_exam_status);
        ivStatusIcon = findViewById(R.id.iv_det_status_icon);
        ivBack = findViewById(R.id.iv_det_exam_back);

        ivBack.setOnClickListener(v -> finish());

        // Read extras
        String dateStr = getIntent().getStringExtra("exam_date");
        double weight = getIntent().getDoubleExtra("exam_weight", 0.0);
        double height = getIntent().getDoubleExtra("exam_height", 0.0);
        String status = getIntent().getStringExtra("exam_status");

        // Format Date to friendly form
        String dateFormatted = dateStr;
        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputSdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
        try {
            Date date = inputSdf.parse(dateStr);
            if (date != null) dateFormatted = outputSdf.format(date);
        } catch (ParseException e) {
            // fallback
        }

        tvDate.setText(dateFormatted);
        tvWeight.setText(String.format(Locale.US, "%.1f Kg", weight));
        tvHeight.setText(String.format(Locale.US, "%.0f cm", height));
        tvStatus.setText(status);

        // Adjust colors dynamically
        if (status != null) {
            int bgColor, textColor;
            switch (status) {
                case "Normal":
                    bgColor = ContextCompat.getColor(this, R.color.status_normal_light);
                    textColor = ContextCompat.getColor(this, R.color.status_normal);
                    break;
                case "Kurang":
                case "Lebih":
                    bgColor = ContextCompat.getColor(this, R.color.status_warning);
                    textColor = Color.parseColor("#7F5F00");
                    break;
                case "Stunting":
                default:
                    bgColor = ContextCompat.getColor(this, R.color.status_danger_light);
                    textColor = ContextCompat.getColor(this, R.color.status_danger);
                    break;
            }
            tvStatus.setTextColor(textColor);
            ivStatusIcon.setBackgroundTintList(ColorStateList.valueOf(bgColor));
            ivStatusIcon.setImageTintList(ColorStateList.valueOf(textColor));
        }
    }
}
