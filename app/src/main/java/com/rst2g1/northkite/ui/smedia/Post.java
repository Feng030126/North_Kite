package com.rst2g1.northkite.ui.smedia;

public class Post {
    private String username;
    private String profilePicUrl;
    private String postImageUrl;
    private int likes;
    private String description;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String username, String profilePicUrl, String postImageUrl, int likes, String description) {
        this.username = username;
        this.profilePicUrl = profilePicUrl;
        this.postImageUrl = postImageUrl;
        this.likes = likes;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public int getLikes() {
        return likes;
    }

    public String getDescription() {
        return description;
    }
}
