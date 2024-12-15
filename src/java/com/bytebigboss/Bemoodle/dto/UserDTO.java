package com.bytebigboss.Bemoodle.dto;

import com.bytebigboss.Bemoodle.entity.UserStatus;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class UserDTO implements Serializable {

    @Expose
    private int id;

    @Expose
    private String display_name;

    @Expose
    private String username;

    @Expose
    private String email;

    @Expose(deserialize = true, serialize = false)
    private String password;

    public UserDTO() {
    }

    public UserDTO(String display_name, String username, String email, String password) {
        this.display_name = display_name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
