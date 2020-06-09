package com.google.sps.data;

/**
 * Class that holds information about a comment.
 */
public class Comment {

    private String comment;
    private String user;
    private long timestamp;
    private long id;

    public Comment(String comment, String user, long timestamp, long id) {
        this.comment = comment;
        this.user = user;
        this.timestamp = timestamp;
        this.id = id;
    }

}