package hk.hku.cs.myapplication.network;

import hk.hku.cs.myapplication.models.LoginResponse;
import hk.hku.cs.myapplication.models.RegisterRequest;
import hk.hku.cs.myapplication.models.LoginRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/v1/auth/register")
    Call<Void> register(@Body RegisterRequest request);
    @POST("api/v1/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}