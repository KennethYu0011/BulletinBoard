package com.bulletinboard;

public class Post {

    private String title;
    private String message;

    public Post() { }

    public Post(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return (this.title + "\n\n" + this.message);
    }

    public static Post buildPostFromServer(messagePostFromServer post) {
        Post temp = new Post();

        temp.setTitle(post.getTitle());
        temp.setMessage(post.getMessage());

        return temp;
    }

}
