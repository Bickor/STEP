package com.google.sps.data;

/**
 * Class that holds information about a comment.
 */
public class Comment {

    private final String comment;
    private final String user;
    private final long timestamp;
    //private long id;

    public Comment(String comment, String user, long timestamp) {
        this.comment = comment;
        this.user = user;
        this.timestamp = timestamp;
        //this.id = id;
    }

}