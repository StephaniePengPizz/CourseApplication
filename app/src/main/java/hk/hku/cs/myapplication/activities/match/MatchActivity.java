package hk.hku.cs.myapplication.activities.match;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cs.myapplication.models.Course;
import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.adapters.CourseAdapter;
import hk.hku.cs.myapplication.utils.NavigationUtils;

public class MatchActivity extends AppCompatActivity {

    private RecyclerView CourseRecyclerView;
    private CourseAdapter CourseAdapter;
    private List<Course> CourseList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // 初始化视图
        CourseRecyclerView = findViewById(R.id.CourseRecyclerView);
        Button matchButton = findViewById(R.id.matchButton);

        // 初始化数据
        CourseList = new ArrayList<>();
        CourseList.add(new Course("Math", "09:00 AM", "Room 101", "Monday"));
        CourseList.add(new Course("Science", "10:00 AM", "Room 102", "Tuesday"));

        // 设置适配器
        CourseAdapter = new CourseAdapter(CourseList);
        CourseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        CourseRecyclerView.setAdapter(CourseAdapter);

        // 匹配按钮点击事件
        matchButton.setOnClickListener(v -> {
            // 跳转到匹配结果界面
            Intent intent = new Intent(MatchActivity.this, MatchResultActivity.class);
            startActivity(intent);
        });

        // 初始化底部导航栏
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));

        // 根据当前 Activity 设置选中项
        bottomNavigationView.setSelectedItemId(R.id.navigation_match);
    }
}