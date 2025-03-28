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

import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.models.Message;
import hk.hku.cs.myapplication.models.Forum;
import hk.hku.cs.myapplication.models.ForumManager;
import hk.hku.cs.myapplication.adapters.MessageAdapter;

public class ForumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputEditText;
    private Button sendButton;
    private Button backButton;

    private MessageAdapter messageAdapter;
    private Forum forum;
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        getSupportActionBar().hide();
        courseName = getIntent().getStringExtra("courseName");
        setTitle("Forum: " + courseName);
        Log.d("ForumActivity", "已进入论坛页面，课程名 = " + courseName);
        Toast.makeText(this, "已进入论坛: " + courseName, Toast.LENGTH_SHORT).show();

        forum = ForumManager.getOrCreateForum(courseName);

        recyclerView = findViewById(R.id.recyclerView);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        backButton = findViewById(R.id.backButton);


        if (forum.getMessages().isEmpty()) {
            forum.postMessage(new Message("系统", "欢迎来到 " + courseName + " 的课程论坛！", System.currentTimeMillis()));
        }

        List<Message> messages = forum.getMessages();
        messageAdapter = new MessageAdapter(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputEditText.getText().toString().trim();
                if (!content.isEmpty()) {
                    forum.postMessage(new Message("You", content, System.currentTimeMillis()));
                    inputEditText.setText("");
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });

        backButton.setOnClickListener(v -> {
            Log.d("ForumActivity", "返回按钮被点击");
            finish();
        });
    }
}
