package hk.hku.cs.myapplication.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.models.User;
import hk.hku.cs.myapplication.adapters.UserAdapter;
import hk.hku.cs.myapplication.utils.NavigationUtils;

public class MatchResultActivity extends AppCompatActivity {

    private RecyclerView matchResultRecyclerView;
    private UserAdapter matchResultAdapter;
    private List<User> matchResultList;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);

        // 初始化视图
        matchResultRecyclerView = findViewById(R.id.matchResultRecyclerView);

        // 初始化数据
        matchResultList = new ArrayList<>();
        matchResultList.add(new User("Alice", new ArrayList<>()));
        matchResultList.add(new User("Bob", new ArrayList<>()));

        // 设置适配器
        matchResultAdapter = new UserAdapter(matchResultList);
        matchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        matchResultRecyclerView.setAdapter(matchResultAdapter);

    }
}