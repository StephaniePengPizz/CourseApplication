package hk.hku.cs.myapplication;

// MyApplication.java
import android.app.Application;

import hk.hku.cs.myapplication.network.RetrofitClient;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.initialize(this); // this 指向 Application Context
    }
}
