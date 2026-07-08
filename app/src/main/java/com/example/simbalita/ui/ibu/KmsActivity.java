package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.Examination;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KmsActivity extends AppCompatActivity {

    private ImageView ivBack;
    private KmsGraphView kmsGraphView;
    private TextView tvViewAll, tvEmpty;
    private RecyclerView rvHistory;
    
    private DatabaseHelper dbHelper;
    private List<Examination> examList = new ArrayList<>();
    private Child currentChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kms);

        dbHelper = new DatabaseHelper(this);

        // Bind Views
        ivBack = findViewById(R.id.iv_kms_back);
        kmsGraphView = findViewById(R.id.kms_graph_view);
        tvViewAll = findViewById(R.id.tv_kms_view_all);
        tvEmpty = findViewById(R.id.tv_kms_history_empty);
        rvHistory = findViewById(R.id.rv_kms_history);

        // Setup Back Button
        ivBack.setOnClickListener(v -> finish());

        // Get Current Child ID
        SharedPreferences pref = getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        int childId = pref.getInt("selected_child_id", -1);
        int motherId = pref.getInt("user_id", -1);

        if (childId == -1) {
            List<Child> kids = dbHelper.getChildrenByMother(motherId);
            if (!kids.isEmpty()) {
                currentChild = kids.get(0);
                pref.edit().putInt("selected_child_id", currentChild.getId()).apply();
            }
        } else {
            currentChild = dbHelper.getChildById(childId);
        }

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        if (currentChild != null) {
            loadKmsData();
        } else {
            tvEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        }

        // View All click redirect to Riwayat Pemeriksaan
        tvViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(KmsActivity.this, RiwayatPemeriksaanActivity.class);
            startActivity(intent);
        });
    }

    private void loadKmsData() {
        examList = dbHelper.getExaminationsByChild(currentChild.getId());

        // Draw graph (takes unsorted list, internally sorts it)
        kmsGraphView.setData(examList, currentChild.getBirthDate());

        // Display list (newest first)
        List<Examination> sortedListForRecycler = new ArrayList<>(examList);
        Collections.sort(sortedListForRecycler, (o1, o2) -> o2.getDate().compareTo(o1.getDate()));

        if (sortedListForRecycler.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvHistory.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvHistory.setVisibility(View.VISIBLE);
            rvHistory.setAdapter(new WeightHistoryAdapter(sortedListForRecycler));
        }
    }

    // Short adapter for weights listing in KMS screen
    private class WeightHistoryAdapter extends RecyclerView.Adapter<WeightHistoryAdapter.ViewHolder> {
        private final List<Examination> list;

        public WeightHistoryAdapter(List<Examination> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kms_weight, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Examination exam = list.get(position);
            holder.tvWeight.setText(String.format(Locale.US, "%.1f Kg", exam.getWeight()));
            
            // Format Date
            String formattedDate = exam.getDate();
            try {
                SimpleDateFormat sdfSource = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                SimpleDateFormat sdfDest = new SimpleDateFormat("d MMMM yyyy", new Locale("id", "ID"));
                Date date = sdfSource.parse(exam.getDate());
                if (date != null) {
                    formattedDate = sdfDest.format(date);
                }
            } catch (ParseException e) {
                // Keep original
            }
            holder.tvDate.setText(formattedDate);
        }

        @Override
        public int getItemCount() {
            return Math.min(list.size(), 5); // Show top 5 records
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvWeight;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_kms_item_date);
                tvWeight = itemView.findViewById(R.id.tv_kms_item_weight);
            }
        }
    }
}
