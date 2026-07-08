package com.example.simbalita.ui.ibu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Examination;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class KmsGraphView extends View {

    private Paint gridPaint;
    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint fillPaintRed;
    private Paint fillPaintYellow;
    private Paint fillPaintGreen;
    private Paint fillPaintOrange;

    private List<Examination> checkupList = new ArrayList<>();
    private String birthDate = "2025-01-01"; // Default birth date

    // KMS coordinates: age (0-24 months) vs weight (0-16 kg)
    private static final int MAX_AGE_MONTHS = 18;
    private static final float MAX_WEIGHT_KG = 16.0f;

    public KmsGraphView(Context context) {
        super(context);
        init();
    }

    public KmsGraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KmsGraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#E0E0E0"));
        gridPaint.setStrokeWidth(2f);
        gridPaint.setStyle(Paint.Style.STROKE);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#4CAF50")); // Green line
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.parseColor("#388E3C")); // Darker green
        pointPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.parseColor("#4E4E4E"));
        textPaint.setTextSize(24f);

        // Paints for shaded regions
        fillPaintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaintRed.setColor(Color.parseColor("#FFCDD2")); // Red/Light Red
        fillPaintRed.setStyle(Paint.Style.FILL);

        fillPaintYellow = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaintYellow.setColor(Color.parseColor("#FFF9C4")); // Yellow
        fillPaintYellow.setStyle(Paint.Style.FILL);

        fillPaintGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaintGreen.setColor(Color.parseColor("#C8E6C9")); // Green
        fillPaintGreen.setStyle(Paint.Style.FILL);

        fillPaintOrange = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaintOrange.setColor(Color.parseColor("#FFE0B2")); // Orange
        fillPaintOrange.setStyle(Paint.Style.FILL);
    }

    public void setData(List<Examination> list, String birthDate) {
        this.checkupList = list;
        this.birthDate = birthDate;
        
        // Sort checkups chronologically by date
        Collections.sort(this.checkupList, new Comparator<Examination>() {
            @Override
            public int compare(Examination o1, Examination o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        invalidate(); // Redraw
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = 80;
        int paddingTop = 40;
        int paddingRight = 40;
        int paddingBottom = 80;

        float width = getWidth() - paddingLeft - paddingRight;
        float height = getHeight() - paddingTop - paddingBottom;

        // Draw colored bands representing KMS normal/warning zones
        drawKmsZones(canvas, paddingLeft, paddingTop, width, height);

        // Draw Grid Lines
        for (int i = 0; i <= MAX_AGE_MONTHS; i++) {
            float x = paddingLeft + (width * i / MAX_AGE_MONTHS);
            canvas.drawLine(x, paddingTop, x, paddingTop + height, gridPaint);
            
            // X-axis label (months)
            if (i % 2 == 0) {
                canvas.drawText(String.valueOf(i), x - 10, paddingTop + height + 35, textPaint);
            }
        }

        for (int i = 0; i <= (int) MAX_WEIGHT_KG; i += 2) {
            float y = paddingTop + height - (height * i / MAX_WEIGHT_KG);
            canvas.drawLine(paddingLeft, y, paddingLeft + width, y, gridPaint);
            
            // Y-axis label (weight in kg)
            canvas.drawText(String.valueOf(i), paddingLeft - 50, y + 8, textPaint);
        }

        // Draw axis titles
        textPaint.setTextSize(26f);
        canvas.drawText("Umur (Bulan)", paddingLeft + (width / 2) - 80, paddingTop + height + 75, textPaint);
        
        // Draw Y axis title (vertical)
        canvas.save();
        canvas.rotate(-90, 30, paddingTop + (height / 2));
        canvas.drawText("Berat Badan (Kg)", 30 - 80, paddingTop + (height / 2), textPaint);
        canvas.restore();

        // Plot child growth line
        if (checkupList != null && !checkupList.isEmpty()) {
            List<PointF> points = new ArrayList<>();
            for (Examination exam : checkupList) {
                int ageMonths = DatabaseHelper.calculateAgeInMonths(birthDate, exam.getDate());
                if (ageMonths < 0) ageMonths = 0;
                if (ageMonths > MAX_AGE_MONTHS) ageMonths = MAX_AGE_MONTHS;

                float weight = (float) exam.getWeight();
                if (weight > MAX_WEIGHT_KG) weight = MAX_WEIGHT_KG;

                float px = paddingLeft + (width * ageMonths / MAX_AGE_MONTHS);
                float py = paddingTop + height - (height * weight / MAX_WEIGHT_KG);

                points.add(new PointF(px, py));
            }

            // Draw line
            Path linePath = new Path();
            for (int i = 0; i < points.size(); i++) {
                PointF p = points.get(i);
                if (i == 0) {
                    linePath.moveTo(p.x, p.y);
                } else {
                    linePath.lineTo(p.x, p.y);
                }
            }
            canvas.drawPath(linePath, linePaint);

            // Draw dots
            for (PointF p : points) {
                canvas.drawCircle(p.x, p.y, 10f, pointPaint);
            }
        }
    }

    private void drawKmsZones(Canvas canvas, float left, float top, float w, float h) {
        // Approximate standard WHO / KMS growth limits for boys/girls merged
        // age (months) -> weight thresholds
        // under_red: below 2.0 -> 7.0 kg
        // under_yellow: below 2.5 -> 8.5 kg
        // normal_green: 3.0 -> 10.5 kg
        // over_orange: 4.0 -> 12.0 kg
        
        Path orangePath = new Path();
        Path greenPath = new Path();
        Path yellowPath = new Path();
        Path redPath = new Path();

        // We will construct polygonal segments for drawing
        // Bottom line: Weight 0
        // Top line: Weight MAX_WEIGHT_KG

        // Red Zone: 0 to limit1
        // Yellow Zone: limit1 to limit2
        // Green Zone: limit2 to limit3
        // Orange Zone: limit3 to limit4

        List<PointF> redPoints = new ArrayList<>();
        List<PointF> yellowPoints = new ArrayList<>();
        List<PointF> greenPoints = new ArrayList<>();
        List<PointF> orangePoints = new ArrayList<>();

        for (int m = 0; m <= MAX_AGE_MONTHS; m++) {
            float x = left + (w * m / MAX_AGE_MONTHS);
            
            // Standard weight limits based on age in months
            float limitRed = 2.0f + (m * 0.35f);     // Red/Yellow border
            float limitYellow = 2.5f + (m * 0.45f);  // Yellow/Green border
            float limitGreen = 3.3f + (m * 0.55f);   // Green/Orange border
            float limitOrange = 4.2f + (m * 0.65f);  // Orange/Top border

            float yRed = top + h - (h * limitRed / MAX_WEIGHT_KG);
            float yYellow = top + h - (h * limitYellow / MAX_WEIGHT_KG);
            float yGreen = top + h - (h * limitGreen / MAX_WEIGHT_KG);
            float yOrange = top + h - (h * limitOrange / MAX_WEIGHT_KG);

            redPoints.add(new PointF(x, yRed));
            yellowPoints.add(new PointF(x, yYellow));
            greenPoints.add(new PointF(x, yGreen));
            orangePoints.add(new PointF(x, yOrange));
        }

        // Draw Red Area (bottom part)
        redPath.moveTo(left, top + h);
        for (PointF p : redPoints) {
            redPath.lineTo(p.x, p.y);
        }
        redPath.lineTo(left + w, top + h);
        redPath.close();
        canvas.drawPath(redPath, fillPaintRed);

        // Draw Yellow Area
        yellowPath.moveTo(left, top + h);
        for (PointF p : yellowPoints) {
            yellowPath.lineTo(p.x, p.y);
        }
        for (int i = redPoints.size() - 1; i >= 0; i--) {
            yellowPath.lineTo(redPoints.get(i).x, redPoints.get(i).y);
        }
        yellowPath.close();
        canvas.drawPath(yellowPath, fillPaintYellow);

        // Draw Green Area
        greenPath.moveTo(left, top + h);
        for (PointF p : greenPoints) {
            greenPath.lineTo(p.x, p.y);
        }
        for (int i = yellowPoints.size() - 1; i >= 0; i--) {
            greenPath.lineTo(yellowPoints.get(i).x, yellowPoints.get(i).y);
        }
        greenPath.close();
        canvas.drawPath(greenPath, fillPaintGreen);

        // Draw Orange Area
        orangePath.moveTo(left, top + h);
        for (PointF p : orangePoints) {
            orangePath.lineTo(p.x, p.y);
        }
        for (int i = greenPoints.size() - 1; i >= 0; i--) {
            orangePath.lineTo(greenPoints.get(i).x, greenPoints.get(i).y);
        }
        orangePath.close();
        canvas.drawPath(orangePath, fillPaintOrange);
    }
}
