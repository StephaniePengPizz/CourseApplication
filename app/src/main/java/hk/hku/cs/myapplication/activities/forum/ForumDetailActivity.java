package hk.hku.cs.myapplication.activities.forum;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.ReplyAdapter;
import hk.hku.cs.myapplication.models.forum.PostReplyRequest;
import hk.hku.cs.myapplication.models.forum.ReplyItem;
import hk.hku.cs.myapplication.models.response.ApiResponse;
import hk.hku.cs.myapplication.network.ApiService;
import hk.hku.cs.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputEditText;
    private Button sendButton, backButton;
    private List<ReplyItem> replies = new ArrayList<>();
    private ReplyAdapter replyAdapter;
    private int forumId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_detail);
        getSupportActionBar().hide();

        forumId = getIntent().getIntExtra("forumId", 0);

        recyclerView = findViewById(R.id.recyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);

        apiService = RetrofitClient.getRetrofit().create(ApiService.class);

        replyAdapter = new ReplyAdapter(replies, this::onDeleteReply);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(replyAdapter);

        loadReplies();

        sendButton.setOnClickListener(v -> {
            String content = inputEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                PostReplyRequest request = new PostReplyRequest(forumId, content);
                apiService.postReply(request).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            inputEditText.setText("");
                            loadReplies();
                        } else {
                            Toast.makeText(ForumDetailActivity.this, "回复失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(ForumDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void loadReplies() {
        apiService.getReplies(forumId).enqueue(new Callback<ApiResponse<List<ReplyItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReplyItem>>> call, Response<ApiResponse<List<ReplyItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    replies.clear();
                    replies.addAll(response.body().getData());
                    replyAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ForumDetailActivity.this, "加载回复失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReplyItem>>> call, Throwable t) {
                Toast.makeText(ForumDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onDeleteReply(ReplyItem item) {
        apiService.deleteReply(item.getId()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ForumDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    loadReplies();
                } else {
                    Toast.makeText(ForumDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ForumDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

