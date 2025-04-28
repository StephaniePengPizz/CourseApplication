package hk.hku.cs.myapplication.models.user;

import androidx.annotation.NonNull;

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

        private String school;
        private String major;
        private String registeryear;

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
        @NonNull
        public String getSchool() {
            return school != null ? school : "";
        }
        @NonNull
        public String getMajor() {
            return major != null ? major : "";
        }
        @NonNull
        public String getRegisterYear() {
            return registeryear != null ? registeryear : "";
        }
    }
}