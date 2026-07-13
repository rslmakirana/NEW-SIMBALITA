package com.example.simbalita.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.adapter.ChildAdminAdapter;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import java.util.ArrayList;
import java.util.List;

public class DataPesertaActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etSearch;
    private RecyclerView rvPeserta;
    private Button btnTambahPeserta;

    private DatabaseHelper dbHelper;
    private List<Child> childList = new ArrayList<>();
    private ChildAdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_peserta);

        dbHelper = new DatabaseHelper(this);

        // Bind Views
        btnBack = findViewById(R.id.btn_back_data_peserta);
        etSearch = findViewById(R.id.et_search_peserta);
        rvPeserta = findViewById(R.id.rv_peserta_list);
        btnTambahPeserta = findViewById(R.id.btn_tambah_peserta);

        // RecyclerView setup
        rvPeserta.setLayoutManager(new LinearLayoutManager(this));

        // Click Back button
        btnBack.setOnClickListener(v -> finish());

        // Setup search logic
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadData(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Click Tambah Peserta -> open AddChildActivity
        btnTambahPeserta.setOnClickListener(v -> {
            Intent intent = new Intent(DataPesertaActivity.this, AddChildActivity.class);
            startActivity(intent);
        });

        // Initial load
        loadData("");
    }

    private void loadData(String query) {
        if (query.isEmpty()) {
            childList = dbHelper.getAllChildren();
        } else {
            childList = dbHelper.searchChildren(query);
        }

        adapter = new ChildAdminAdapter(childList, dbHelper, child -> {
            // Click participant -> open PemeriksaanBalitaActivity
            Intent intent = new Intent(DataPesertaActivity.this, PemeriksaanBalitaActivity.class);
            intent.putExtra("child_id", child.getId());
            startActivity(intent);
        });
        rvPeserta.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload list when returning
        loadData(etSearch.getText().toString().trim());
    }
}
