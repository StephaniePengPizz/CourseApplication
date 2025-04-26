package hk.hku.cs.myapplication.models.user;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters (必须提供，否则Retrofit无法序列化)
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
}