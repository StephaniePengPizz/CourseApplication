package hk.hku.cs.myapplication.activities.forum;

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
import hk.hku.cs.myapplication.models.ApiResponse;
import hk.hku.cs.myapplication.models.ForumItem;
import hk.hku.cs.myapplication.models.Message;
import hk.hku.cs.myapplication.models.Forum;
import hk.hku.cs.myapplication.models.ForumManager;
import hk.hku.cs.myapplication.adapters.MessageAdapter;
import hk.hku.cs.myapplication.models.PostForumRequest;
import hk.hku.cs.myapplication.network.ApiService;
import hk.hku.cs.myapplication.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputEditText;
    private Button sendButton;
    private Button backButton;

    private MessageAdapter messageAdapter;
    private List<Message> messages = new ArrayList<>();
    private int courseId;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        getSupportActionBar().hide();

        courseId = getIntent().getIntExtra("courseId", 0);
        setTitle("Forum - Course ID: " + courseId);
        Log.d("ForumActivity", "进入论坛页面, courseId = " + courseId);

        recyclerView = findViewById(R.id.recyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);

        messageAdapter = new MessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        apiService = RetrofitClient.getRetrofit().create(ApiService.class);


        loadMessages();

        sendButton.setOnClickListener(v -> {
            String content = inputEditText.getText().toString().trim();
            if (!content.isEmpty()) {
                PostForumRequest request = new PostForumRequest(courseId, "留言", content);
                apiService.postForumMessage(request).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            messages.add(new Message("你", content, System.currentTimeMillis()));
                            messageAdapter.notifyDataSetChanged();
                            inputEditText.setText("");
                        } else {
                            Toast.makeText(ForumActivity.this, "发送失败: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("ForumActivity", "发送失败", t);
                        Toast.makeText(ForumActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void loadMessages() {
        apiService.getForumMessages(courseId).enqueue(new Callback<ApiResponse<List<ForumItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ForumItem>>> call, Response<ApiResponse<List<ForumItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    List<ForumItem> forumItems = response.body().getData();
                    messages.clear();
                    for (ForumItem item : forumItems) {
                        String sender = item.getCreator() != null ? item.getCreator().getUsername() : "Unknown";
                        messages.add(new Message(sender, item.getContent(), System.currentTimeMillis()));
                    }
                    messageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ForumActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ForumItem>>> call, Throwable t) {
                Log.e("ForumActivity", "加载失败", t);
                Toast.makeText(ForumActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
            }
        });

    }
}