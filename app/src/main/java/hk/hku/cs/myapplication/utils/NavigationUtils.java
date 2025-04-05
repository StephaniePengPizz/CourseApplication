package hk.hku.cs.myapplication.utils;  // 根据项目包名调整

import android.app.Activity;
import android.content.Intent;

import hk.hku.cs.myapplication.activities.course.PoolCourseActivity;
import hk.hku.cs.myapplication.activities.course.MyCourseActivity;
import hk.hku.cs.myapplication.activities.calendar.CalendarActivity;
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
            if (itemId == R.id.navigation_pool_course) {
                if (!(activity instanceof PoolCourseActivity)) {
                    intent = new Intent(activity, PoolCourseActivity.class);
                }
            } else if (itemId == R.id.navigation_my_course) {
                if (!(activity instanceof MyCourseActivity)) {
                    intent = new Intent(activity, MyCourseActivity.class);
                }
            } else if (itemId == R.id.navigation_calendar) {
                if (!(activity instanceof CalendarActivity)) {
                    intent = new Intent(activity, CalendarActivity.class);
                }
            } else if (itemId == R.id.navigation_match) {
                if (!(activity instanceof MatchActivity)) {
                    intent = new Intent(activity, MatchActivity.class);
                }
            } else if (itemId == R.id.navigation_profile) {
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