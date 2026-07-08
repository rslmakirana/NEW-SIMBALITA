package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import java.util.List;

public class IbuProfilFragment extends Fragment {

    private TextView tvMotherName, tvChildSummary;
    private ImageView ivMenu;
    private LinearLayout llDataIbu, llAnakSaya, llSettings, llBantuan, llAbout;
    private Button btnLogout;
    
    private DatabaseHelper dbHelper;
    private int motherId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_profil, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        // Bind Views
        tvMotherName = view.findViewById(R.id.tv_profile_mother_name);
        tvChildSummary = view.findViewById(R.id.tv_profile_child_summary);
        ivMenu = view.findViewById(R.id.iv_profile_menu);
        
        llDataIbu = view.findViewById(R.id.ll_opt_data_ibu);
        llAnakSaya = view.findViewById(R.id.ll_opt_anak_saya);
        llSettings = view.findViewById(R.id.ll_opt_settings);
        llBantuan = view.findViewById(R.id.ll_opt_bantuan);
        llAbout = view.findViewById(R.id.ll_opt_about);
        btnLogout = view.findViewById(R.id.btn_profile_logout);

        // Session preferences
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        motherId = pref.getInt("user_id", -1);
        String motherName = pref.getString("user_name", "Ibu Simbalita");
        
        tvMotherName.setText(motherName);

        // Determine child name
        int childId = pref.getInt("selected_child_id", -1);
        String childName = "";
        if (childId != -1) {
            Child child = dbHelper.getChildById(childId);
            if (child != null) {
                childName = child.getName();
            }
        } else {
            List<Child> kids = dbHelper.getChildrenByMother(motherId);
            if (!kids.isEmpty()) {
                childName = kids.get(0).getName();
            }
        }

        if (!childName.isEmpty()) {
            tvChildSummary.setText("Ibu dari " + childName);
        } else {
            tvChildSummary.setText("Belum mendaftarkan anak");
        }

        // Toggle Drawer
        if (ivMenu != null) {
            ivMenu.setOnClickListener(v -> {
                if (requireActivity() instanceof IbuMainActivity) {
                    ((IbuMainActivity) requireActivity()).openDrawer();
                }
            });
        }

        // Setup Option Listeners
        llDataIbu.setOnClickListener(v -> {
            String name = pref.getString("user_name", "-");
            String nik = pref.getString("user_nik", "-");
            String username = pref.getString("user_username", "-");
            String phone = pref.getString("user_phone", "-");
            String address = pref.getString("user_address", "-");

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
            builder.setTitle("Detail Data Ibu / Orang Tua");
            builder.setMessage("Nama Lengkap:\n" + name + "\n\n" +
                               "Username:\n" + username + "\n\n" +
                               "NIK (No. KTP):\n" + nik + "\n\n" +
                               "Nomor HP:\n" + phone + "\n\n" +
                               "Alamat Rumah:\n" + address);
            builder.setPositiveButton("Tutup", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

        llAnakSaya.setOnClickListener(v -> {
            if (requireActivity() instanceof IbuMainActivity) {
                IbuMainActivity main = (IbuMainActivity) requireActivity();
                main.loadFragment(new IbuDataAnakFragment());
                main.uncheckBottomNav();
            }
        });

        llSettings.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Pengaturan akun dalam pengembangan", Toast.LENGTH_SHORT).show();
        });

        llBantuan.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Silakan hubungi Kader Posyandu untuk bantuan", Toast.LENGTH_SHORT).show();
        });

        llAbout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Simbalita E-Posyandu v1.0", Toast.LENGTH_LONG).show();
        });

        // Manual Logout
        btnLogout.setOnClickListener(v -> {
            if (requireActivity() instanceof IbuMainActivity) {
                ((IbuMainActivity) requireActivity()).logout();
            }
        });

        return view;
    }
}
