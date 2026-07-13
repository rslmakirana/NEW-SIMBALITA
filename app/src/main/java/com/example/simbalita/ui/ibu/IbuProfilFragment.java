package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;

public class IbuProfilFragment extends Fragment {

    private TextView tvMotherName;
    private Button btnDataIbu, btnDataAnak;
    private CardView cvLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_profil, container, false);

        // Bind Views
        tvMotherName = view.findViewById(R.id.tv_profile_mother_name);
        btnDataIbu = view.findViewById(R.id.btn_data_ibu);
        btnDataAnak = view.findViewById(R.id.btn_data_anak);
        cvLogout = view.findViewById(R.id.cv_profile_logout);

        // Session preferences
        SharedPreferences pref = requireActivity().getSharedPreferences("simbalita_prefs", Context.MODE_PRIVATE);
        String motherName = pref.getString("user_name", "Nama Ibu");

        tvMotherName.setText(motherName.toUpperCase());

        // Button Listeners
        btnDataIbu.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BiodataIbuActivity.class);
            startActivity(intent);
        });

        btnDataAnak.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BiodataAnakActivity.class);
            startActivity(intent);
        });

        cvLogout.setOnClickListener(v -> {
            if (requireActivity() instanceof IbuMainActivity) {
                ((IbuMainActivity) requireActivity()).logout();
            }
        });

        return view;
    }
}
