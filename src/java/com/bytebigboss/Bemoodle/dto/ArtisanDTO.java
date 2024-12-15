package com.bytebigboss.Bemoodle.dto;

import com.bytebigboss.Bemoodle.entity.UserStatus;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class ArtisanDTO implements Serializable {

    @Expose
    private String username;

    @Expose
    private String bio;

    public ArtisanDTO() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

}
