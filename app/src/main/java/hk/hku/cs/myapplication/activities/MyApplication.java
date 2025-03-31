package hk.hku.cs.myapplication.activities;

// MyApplication.java
import android.app.Application;

import hk.hku.cs.myapplication.network.RetrofitClient;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 在这里初始化 RetrofitClient
        RetrofitClient.initialize(this); // this 指向 Application Context
    }
}
