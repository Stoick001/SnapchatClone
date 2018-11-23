package com.example.gorda.snapchatclone.ReciclerViewReciever;

public class RecieverObject {
    private String username;
    private String uid;
    private String profileImageUrl;
    private boolean receive;

    public RecieverObject(String username, String uid, String profileImageUrl, boolean receive) {
        this.username = username;
        this.uid = uid;
        this.receive = receive;
        this.profileImageUrl = profileImageUrl;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean getReceive() {
        return receive;
    }
    public void setReceive(boolean receive) {
        this.receive = receive;
    }
}
