package com.example.simbalita.ui.ibu;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.simbalita.R;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Article;

public class EdukasiDetailActivity extends AppCompatActivity {

    private TextView tvCategory, tvTitle, tvContent;
    private ImageView ivBack;
    private DatabaseHelper dbHelper;
    private int articleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edukasi_detail);

        dbHelper = new DatabaseHelper(this);
        articleId = getIntent().getIntExtra("article_id", -1);

        tvCategory = findViewById(R.id.tv_edu_det_category);
        tvTitle = findViewById(R.id.tv_edu_det_title);
        tvContent = findViewById(R.id.tv_edu_det_content);
        ivBack = findViewById(R.id.iv_edu_det_back);

        ivBack.setOnClickListener(v -> finish());

        loadArticleDetails();
    }

    private void loadArticleDetails() {
        if (articleId == -1) {
            Toast.makeText(this, "Artikel tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Article article = dbHelper.getArticleById(articleId);
        if (article != null) {
            tvCategory.setText(article.getCategory());
            tvTitle.setText(article.getTitle());
            tvContent.setText(article.getContent());
        } else {
            Toast.makeText(this, "Gagal memuat artikel", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
