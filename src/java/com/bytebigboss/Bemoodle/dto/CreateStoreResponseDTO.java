package com.bytebigboss.Bemoodle.dto;

/**
 *
 * @author ByteBigBoss
 * @org ImaginecoreX
 */
public class CreateStoreResponseDTO {

    private boolean success;
    private StoreDTO store;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public StoreDTO getStore() {
        return store;
    }

    public void setStore(StoreDTO store) {
        this.store = store;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
