package hk.hku.cs.myapplication;

import android.app.Application;
import android.content.SharedPreferences;

import hk.hku.cs.myapplication.network.RetrofitClient;

public class MyApplication extends Application {

    private static String username;

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.initialize(this);

        // ✅ 启动时自动从SharedPreferences恢复用户名
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = prefs.getString("username", null); // 默认null
    }

    public static void setUsername(String name) {
        username = name;
    }

    public static String getUsername() {
        return username;
    }
}
