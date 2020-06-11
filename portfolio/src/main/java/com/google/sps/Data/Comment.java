package com.google.sps.data;

/**
 * Class that holds information about a comment.
 */
public class Comment {

    private final String comment;
    private final String user;
    private final long timestamp;
    private final String nickname;

    public Comment(String comment, String user, String nickname, long timestamp) {
        this.comment = comment;
        this.user = user;
        this.nickname = nickname;
        this.timestamp = timestamp;
    }
}