package hk.hku.cs.myapplication.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://course.shamming.cn/";
    private static Retrofit retrofit;
    private static Context appContext; // 添加静态Context引用
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    // 添加自定义DNS解析（解决UnknownHostException的报错）
    private static final okhttp3.Dns customDns = hostname -> {
        if (hostname.equals("course.shamming.cn")) {
            // 如果域名解析失败，尝试直接使用IP
            return java.util.Arrays.asList(
                    java.net.InetAddress.getByName("210.6.94.233")
            );
        }
        return okhttp3.Dns.SYSTEM.lookup(hostname);
    };

    public static ApiService getInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时
                    .readTimeout(30, TimeUnit.SECONDS)     // 读取超时
                    .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时

                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(chain -> {
                        // 自动添加token到请求头
                        Request original = chain.request();
                        SharedPreferences prefs = appContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(appContext);
                        String token = prefs.getString("authToken", "");

                        if (!token.isEmpty()) {
                            Request request = original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .build();
                            return chain.proceed(request);
                        }
                        return chain.proceed(original);
                    })
                    .dns(customDns)  // 使用自定义DNS
                    .retryOnConnectionFailure(true)  // 自动重试
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}