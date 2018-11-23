package com.example.gorda.snapchatclone.recyclerViewStory;

public class StoryObject {
    private String username;
    private String uid;
    private String chatOrStory;
    private String profileImageUrl;

    public StoryObject(String username, String uid, String profileImageUrl, String chatOrStory) {
        this.username = username;
        this.uid = uid;
        this.chatOrStory = chatOrStory;
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

    public String getChatOrStory() {
        return chatOrStory;
    }

    public void setChatOrStory(String chatOrStory) {
        this.chatOrStory = chatOrStory;
    }

    @Override
    public boolean equals(Object obj) {
        boolean same = false;

        if (obj != null && obj instanceof StoryObject) {
            same = this.uid == ((StoryObject) obj).uid;
        }

        return same;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (this.uid == null ? 0 : this.uid.hashCode());
        return result;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
