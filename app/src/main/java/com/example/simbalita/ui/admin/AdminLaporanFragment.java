package com.example.simbalita.ui.admin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Child;
import com.example.simbalita.model.Examination;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminLaporanFragment extends Fragment {

    private Spinner spPeriod;
    private Button btnView;
    private CardView cvSummary, cvPdf;
    private TextView tvPeriodTitle, tvTotal, tvNormal, tvWarning, tvStunting;
    
    private DatabaseHelper dbHelper;
    private List<String> displayPeriods = new ArrayList<>();
    private List<String> sqlPeriods = new ArrayList<>();
    private List<Examination> currentPeriodExams = new ArrayList<>();
    private String selectedDisplayPeriod = "";
    private String selectedSqlPeriod = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_laporan, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        spPeriod = view.findViewById(R.id.sp_laporan_period);
        btnView = view.findViewById(R.id.btn_laporan_view);
        cvSummary = view.findViewById(R.id.cv_laporan_summary);
        cvPdf = view.findViewById(R.id.cv_laporan_pdf);
        
        tvPeriodTitle = view.findViewById(R.id.tv_laporan_title_period);
        tvTotal = view.findViewById(R.id.tv_lap_total);
        tvNormal = view.findViewById(R.id.tv_lap_normal);
        tvWarning = view.findViewById(R.id.tv_lap_warning);
        tvStunting = view.findViewById(R.id.tv_lap_stunting);

        setupPeriods();

        btnView.setOnClickListener(v -> generateReportStats());

        view.findViewById(R.id.btn_laporan_pdf).setOnClickListener(v -> exportToPdf());

        return view;
    }

    private void setupPeriods() {
        // Mock dates that fit the mockup: Mei 2026, Juni 2026, Juli 2026, Agustus 2026, September 2026
        displayPeriods.add("Mei 2026"); sqlPeriods.add("2026-05");
        displayPeriods.add("Juni 2026"); sqlPeriods.add("2026-06");
        displayPeriods.add("Juli 2026"); sqlPeriods.add("2026-07");
        displayPeriods.add("Agustus 2026"); sqlPeriods.add("2026-08");
        displayPeriods.add("September 2026"); sqlPeriods.add("2026-09");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, displayPeriods);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPeriod.setAdapter(adapter);
    }

    private void generateReportStats() {
        int pos = spPeriod.getSelectedItemPosition();
        if (pos < 0) return;

        selectedDisplayPeriod = displayPeriods.get(pos);
        selectedSqlPeriod = sqlPeriods.get(pos);

        currentPeriodExams = dbHelper.getExaminationsByPeriod(selectedSqlPeriod);

        int total = currentPeriodExams.size();
        int normal = 0;
        int warning = 0;
        int stunting = 0;

        for (Examination exam : currentPeriodExams) {
            switch (exam.getStatus()) {
                case "Normal":
                    normal++;
                    break;
                case "Kurang":
                case "Lebih":
                    warning++;
                    break;
                case "Stunting":
                default:
                    stunting++;
                    break;
            }
        }

        tvPeriodTitle.setText("Hasil Laporan " + selectedDisplayPeriod);
        tvTotal.setText(String.valueOf(total));
        tvNormal.setText(String.valueOf(normal));
        tvWarning.setText(String.valueOf(warning));
        tvStunting.setText(String.valueOf(stunting));

        cvSummary.setVisibility(View.VISIBLE);
        cvPdf.setVisibility(View.VISIBLE);
    }

    private void exportToPdf() {
        if (currentPeriodExams.isEmpty()) {
            Toast.makeText(requireContext(), "Tidak ada data pemeriksaan untuk periode ini.", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        // A4 page size in points: 595 x 842
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(16f);
        paint.setFakeBoldText(true);

        // Header Title
        canvas.drawText("LAPORAN BULANAN POSYANDU SIMBALITA", 50, 50, paint);
        
        paint.setTextSize(12f);
        paint.setFakeBoldText(false);
        canvas.drawText("Periode: " + selectedDisplayPeriod, 50, 75, paint);
        canvas.drawText("Petugas: Kader Posyandu", 50, 92, paint);

        // Separator line
        paint.setColor(Color.DKGRAY);
        canvas.drawLine(50, 110, 545, 110, paint);

        // Statistics Summary
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        canvas.drawText("Ringkasan Data Gizi:", 50, 140, paint);
        
        paint.setFakeBoldText(false);
        canvas.drawText("Total Balita Diperiksa: " + tvTotal.getText().toString(), 50, 165, paint);
        canvas.drawText("Gizi Normal: " + tvNormal.getText().toString(), 50, 185, paint);
        canvas.drawText("Gizi Kurang / Lebih: " + tvWarning.getText().toString(), 50, 205, paint);
        canvas.drawText("Terindikasi Stunting: " + tvStunting.getText().toString(), 50, 225, paint);

        // Grid/Table header for details
        canvas.drawLine(50, 250, 545, 250, paint);
        paint.setFakeBoldText(true);
        canvas.drawText("Nama Balita", 55, 268, paint);
        canvas.drawText("Tgl Periksa", 220, 268, paint);
        canvas.drawText("BB (Kg)", 330, 268, paint);
        canvas.drawText("TB (cm)", 400, 268, paint);
        canvas.drawText("Status", 470, 268, paint);
        canvas.drawLine(50, 278, 545, 278, paint);

        // Draw Table Row list
        paint.setFakeBoldText(false);
        int y = 295;
        for (int i = 0; i < currentPeriodExams.size(); i++) {
            // Check if page overflow
            if (y > 800) break; 

            Examination exam = currentPeriodExams.get(i);
            Child child = dbHelper.getChildById(exam.getChildId());
            String name = (child != null) ? child.getName() : "Anak ID " + exam.getChildId();
            if (name.length() > 20) {
                name = name.substring(0, 18) + "..";
            }

            canvas.drawText(name, 55, y, paint);
            canvas.drawText(exam.getDate(), 220, y, paint);
            canvas.drawText(String.valueOf(exam.getWeight()), 330, y, paint);
            canvas.drawText(String.valueOf(exam.getHeight()), 400, y, paint);
            canvas.drawText(exam.getStatus(), 470, y, paint);

            y += 20;
        }

        // Draw Footer
        paint.setColor(Color.GRAY);
        paint.setTextSize(10f);
        canvas.drawText("Simbalita E-Posyandu - Laporan Digital", 50, 820, paint);

        pdfDocument.finishPage(page);

        // Save PDF to downloads folder
        File downloadsDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(downloadsDir, "Laporan_Posyandu_" + selectedSqlPeriod + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(requireContext(), "PDF disimpan di: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Gagal menyimpan PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }
}
