package hk.hku.cs.myapplication.activities;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/v1/auth/register")
    Call<Void> register(@Body RegisterRequest request);
}