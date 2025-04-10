package hk.hku.cs.myapplication.activities.match;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import hk.hku.cs.myapplication.R;
import hk.hku.cs.myapplication.utils.NavigationUtils;

public class MatchResultActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_result);

        // 获取传递的数据
        Intent intent = getIntent();
        String userName = intent.getStringExtra("user_name");
        int commonCount = intent.getIntExtra("common_count", 0);

        // 显示数据
        TextView resultText = findViewById(R.id.resultText);
        resultText.setText(userName + " - 共同课程: " + commonCount);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(NavigationUtils.getNavListener(this));
        bottomNavigationView.setSelectedItemId(R.id.navigation_match);
    }
}