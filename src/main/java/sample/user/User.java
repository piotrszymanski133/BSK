package sample.user;

import java.security.KeyPair;

public class User {
    private String username;
    private String password;
    private KeyPair keyPair;

    public User(String username, String password, KeyPair keyPair) {
        this.username = username;
        this.password = password;
        this.keyPair = keyPair;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }
}
