package com.example.simbalita.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.adapter.ChildAdapter;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.ui.ibu.RiwayatPemeriksaanActivity;
import java.util.List;

public class AdminDataPesertaFragment extends Fragment {

    private RecyclerView rvPeserta;
    private TextView tvEmpty;
    private EditText etSearch;
    private ImageView ivAdd;
    private DatabaseHelper dbHelper;
    private ChildAdapter adapter;
    private List<Child> childList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_peserta, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        rvPeserta = view.findViewById(R.id.rv_peserta);
        tvEmpty = view.findViewById(R.id.tv_peserta_empty);
        etSearch = view.findViewById(R.id.et_peserta_search);
        ivAdd = view.findViewById(R.id.iv_peserta_add);

        rvPeserta.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Add Button
        ivAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddChildActivity.class);
            startActivity(intent);
        });

        // Search Input
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadChildren();

        return view;
    }

    private void loadChildren() {
        childList = dbHelper.getAllChildren();
        updateRecyclerView(childList);
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            loadChildren();
        } else {
            childList = dbHelper.searchChildren(query);
            updateRecyclerView(childList);
        }
    }

    private void updateRecyclerView(List<Child> list) {
        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvPeserta.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvPeserta.setVisibility(View.VISIBLE);

            adapter = new ChildAdapter(list, child -> showActionDialog(child));
            rvPeserta.setAdapter(adapter);
        }
    }

    private void showActionDialog(Child child) {
        String[] options = {"Lihat Riwayat Pemeriksaan", "Edit Data Balita", "Hapus Data Balita"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(child.getName());
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // View history
                Intent intent = new Intent(requireActivity(), RiwayatPemeriksaanActivity.class);
                intent.putExtra("child_id", child.getId());
                startActivity(intent);
            } else if (which == 1) {
                // Edit child
                Intent intent = new Intent(requireActivity(), AddChildActivity.class);
                intent.putExtra("child_id", child.getId());
                intent.putExtra("is_edit", true);
                startActivity(intent);
            } else if (which == 2) {
                // Confirm Delete
                showDeleteConfirmDialog(child);
            }
        });
        builder.show();
    }

    private void showDeleteConfirmDialog(Child child) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Data")
                .setMessage("Apakah Anda yakin ingin menghapus data " + child.getName() + "? Semua riwayat pemeriksaan anak ini juga akan dihapus.")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    dbHelper.deleteChild(child.getId());
                    Toast.makeText(requireContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                    loadChildren(); // reload list
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Clear search and reload
        etSearch.setText("");
        loadChildren();
    }
}
