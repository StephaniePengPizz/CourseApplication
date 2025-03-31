package hk.hku.cs.myapplication.models;

public class LoginResponse {
    private int code;
    private String msg;
    private Data data;

    // Getters and Setters
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public Data getData() {
        return data;
    }

    public static class Data {
        private String access_token;
        private String accessTokenExpireTime;
        private String sessionUuid;
        private User user;

        // Getters and Setters
        public String getAccessToken() {
            return access_token;
        }

        public String getAccessTokenExpireTime() {
            return accessTokenExpireTime;
        }

        public void setAccessTokenExpireTime(String accessTokenExpireTime) {
            this.accessTokenExpireTime = accessTokenExpireTime;
        }

        public String getSessionUuid() {
            return sessionUuid;
        }

        public void setSessionUuid(String sessionUuid) {
            this.sessionUuid = sessionUuid;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }
    public boolean isSuccess() {
        return code == 200; // Assuming 200 means success in your API
    }
}