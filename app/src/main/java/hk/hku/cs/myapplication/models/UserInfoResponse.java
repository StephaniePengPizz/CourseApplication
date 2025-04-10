package hk.hku.cs.myapplication.models;

import androidx.annotation.NonNull;

import java.util.List;

import retrofit2.internal.EverythingIsNonNull;

public class UserInfoResponse {
    private int code;
    private String msg;
    private Data data;

    public int getCode() {
        return code;
    }

    public Data getData() {
        return data;
    }

    public String getMsg() { return msg;
    }

    public static class Data {
        private int id;
        private String username;
        private String email;

        public int getId(){
            return id;
        }

        @NonNull
        public String getUsername(){
            return username != null ? username : "";
        }
        @NonNull
        public String getEmail() {
            return email != null ? email : "";
        }
    }
}