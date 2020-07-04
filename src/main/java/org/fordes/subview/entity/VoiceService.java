package org.fordes.subview.entity;

public class VoiceService {

    private Boolean state;
    private Boolean free;
    private String key;
    private String id;

    public Boolean getState() {
        return state;
    }

    public Boolean getFree() {
        return free;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
