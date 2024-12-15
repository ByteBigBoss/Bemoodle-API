package com.bytebigboss.Bemoodle.dto;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class ResponseDTO implements Serializable {
    
    @Expose
    private boolean success;
    
    @Expose
    private Object content;

    public ResponseDTO() {
    }

    public ResponseDTO(boolean success, Object content) {
        this.success = success;
        this.content = content;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

 
    

}
