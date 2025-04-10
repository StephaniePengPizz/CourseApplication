package hk.hku.cs.myapplication.activities.match;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.MatchAdapter;
import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.models.User;
import hk.hku.cs.myapplication.utils.NavigationUtils;

public class MatchActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // 初始化RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 创建模拟数据
        List<User> users = new ArrayList<>();
        users.add(new User("Stupid", Arrays.asList(
                new Course("COMP3234","COMP3234","COMP3234","COMP3234",1),
                new Course("COMP3278","COMP3278","COMP3278","COMP3278",1))));
        users.add(new User("Alice", Arrays.asList(
                new Course("COMP3234","COMP3234","COMP3234","COMP3234",1),
                new Course("COMP3278","COMP3278","COMP3278","COMP3278",1))));
        users.add(new User("Bob", Arrays.asList(
                new Course("COMP3234","COMP3234","COMP3234","COMP3234",1),
                new Course("COMP3233","COMP3233","COMP3233","COMP3233",1))));
        users.add(new User("Happy", Arrays.asList(
                new Course("COMP3233","COMP3233","COMP3233","COMP3233",1),
                new Course("COMP3275","COMP3275","COMP3275","COMP3275",1))));


        // 设置适配器
        MatchAdapter adapter = new MatchAdapter(users);
        recyclerView.setAdapter(adapter);

        // 匹配按钮
        Button matchButton = findViewById(R.id.matchButton);
        matchButton.setOnClickListener(v -> {
            // 这里可以添加匹配逻辑
        });
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));
        bottomNavigationView.setSelectedItemId(R.id.navigation_match);
    }
}