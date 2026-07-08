package com.example.simbalita.ui.ibu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simbalita.R;
import com.example.simbalita.adapter.ArticleAdapter;
import com.example.simbalita.database.DatabaseHelper;
import com.example.simbalita.model.Article;
import java.util.List;

public class IbuEdukasiFragment extends Fragment {

    private RecyclerView rvArticles;
    private TextView tvEmpty;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ibu_edukasi, container, false);

        dbHelper = new DatabaseHelper(requireContext());
        rvArticles = view.findViewById(R.id.rv_articles);
        tvEmpty = view.findViewById(R.id.tv_articles_empty);

        rvArticles.setLayoutManager(new LinearLayoutManager(requireContext()));

        loadArticles();

        return view;
    }

    private void loadArticles() {
        List<Article> list = dbHelper.getAllArticles();
        if (list.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvArticles.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvArticles.setVisibility(View.VISIBLE);

            ArticleAdapter adapter = new ArticleAdapter(list, article -> {
                Intent intent = new Intent(requireActivity(), EdukasiDetailActivity.class);
                intent.putExtra("article_id", article.getId());
                startActivity(intent);
            });
            rvArticles.setAdapter(adapter);
        }
    }
}
