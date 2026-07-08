package com.example.simbalita.ui.ibu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.adapter.ExaminationAdapter;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Examination;
import java.util.List;

public class RiwayatPemeriksaanActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmpty;
    private ImageView ivBack;
    private DatabaseHelper dbHelper;
    private int childId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_pemeriksaan);

        dbHelper = new DatabaseHelper(this);
        childId = getIntent().getIntExtra("child_id", -1);

        rvHistory = findViewById(R.id.rv_history);
        tvEmpty = findViewById(R.id.tv_history_empty);
        ivBack = findViewById(R.id.iv_history_back);

        ivBack.setOnClickListener(v -> finish());

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        loadExaminations();
    }

    private void loadExaminations() {
        if (childId == -1) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
            return;
        }

        List<Examination> exams = dbHelper.getExaminationsByChild(childId);

        if (exams.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);

            ExaminationAdapter adapter = new ExaminationAdapter(exams, exam -> {
                Intent intent = new Intent(RiwayatPemeriksaanActivity.this, DetailPemeriksaanActivity.class);
                intent.putExtra("exam_date", exam.getDate());
                intent.putExtra("exam_weight", exam.getWeight());
                intent.putExtra("exam_height", exam.getHeight());
                intent.putExtra("exam_status", exam.getStatus());
                startActivity(intent);
            });
            rvHistory.setAdapter(adapter);
        }
    }
}
