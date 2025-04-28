package hk.hku.cs.myapplication.activities.forum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.ForumAdapter;
import hk.hku.cs.myapplication.models.forum.ForumItem;
import hk.hku.cs.myapplication.models.forum.PostForumRequest;
import hk.hku.cs.myapplication.models.response.ApiResponse;
import hk.hku.cs.myapplication.network.ApiService;
import hk.hku.cs.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputEditText;
    private Button sendButton, backButton;
    private ForumAdapter forumAdapter;
    private List<ForumItem> forumItems = new ArrayList<>();
    private int courseId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        getSupportActionBar().hide();

        courseId = getIntent().getIntExtra("courseId", 0);

        recyclerView = findViewById(R.id.recyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);

        apiService = RetrofitClient.getRetrofit().create(ApiService.class);

        forumAdapter = new ForumAdapter(forumItems, this::onItemClick, this::onDeleteClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(forumAdapter);

        loadForums();

        sendButton.setOnClickListener(v -> {
            String content = inputEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                PostForumRequest request = new PostForumRequest(courseId, "留言", content);
                apiService.postForumMessage(request).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            inputEditText.setText("");
                            loadForums();
                        } else {
                            Toast.makeText(ForumActivity.this, "发帖失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ForumActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void loadForums() {
        apiService.getForumMessages(courseId).enqueue(new Callback<ApiResponse<List<ForumItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ForumItem>>> call, Response<ApiResponse<List<ForumItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    forumItems.clear();
                    forumItems.addAll(response.body().getData());
                    forumAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ForumActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ForumItem>>> call, Throwable t) {
                Toast.makeText(ForumActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onItemClick(ForumItem item) {
        Intent intent = new Intent(this, ForumDetailActivity.class);
        intent.putExtra("forumId", item.getId());
        startActivity(intent);
    }

    private void onDeleteClick(ForumItem item) {
        apiService.deleteForum(item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForumActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    loadForums();
                } else {
                    Toast.makeText(ForumActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ForumActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
