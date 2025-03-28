package hk.hku.cs.myapplication.utils;  // 根据项目包名调整

import android.app.Activity;
import android.content.Intent;

import hk.hku.cs.myapplication.activities.course.MainActivity;
import hk.hku.cs.myapplication.activities.match.MatchActivity;
import hk.hku.cs.myapplication.activities.user.ProfileActivity;
import hk.hku.cs.myapplication.R;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationUtils {

    /**
     * 获取底部导航栏的点击监听器
     *
     * @param activity 当前 Activity
     * @return BottomNavigationView.OnNavigationItemSelectedListener
     */
    public static BottomNavigationView.OnNavigationItemSelectedListener getNavListener(final Activity activity) {
        return item -> {
            Intent intent = null;
            int itemId = item.getItemId();

            // 根据选中的菜单项启动对应的 Activity
            if (itemId == R.id.navigation_course) {
                // 如果当前 Activity 不是 MainActivity，则跳转
                if (!(activity instanceof MainActivity)) {
                    intent = new Intent(activity, MainActivity.class);
                }
            } else if (itemId == R.id.navigation_match) {
                // 如果当前 Activity 不是 MatchActivity，则跳转
                if (!(activity instanceof MatchActivity)) {
                    intent = new Intent(activity, MatchActivity.class);
                }
            } else if (itemId == R.id.navigation_profile) {
                // 如果当前 Activity 不是 ProfileActivity，则跳转
                if (!(activity instanceof ProfileActivity)) {
                    intent = new Intent(activity, ProfileActivity.class);
                }
            }

            // 启动 Activity
            if (intent != null) {
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0); // 取消切换动画
            }
            return true;
        };
    }
}