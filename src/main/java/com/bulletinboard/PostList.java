package com.bulletinboard;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class PostList {

    private Hashtable<String, Post> plist;

    public PostList(){
        plist = new Hashtable<String, Post>();
    }

    public void addPost(Post post){
        plist.put(post.getTitle(),post);
    }

    public Post getPost(String title){
        return plist.get(title);
    }

    public boolean removePost(String title){
        if(plist.remove(title) == null)
            return false;
        else
            return true;
    }

    public void printTitles() {
        Enumeration<String> titles = getTitles();

        while (titles.hasMoreElements()) {
            System.out.println(titles.nextElement());
        }
    }

    public Enumeration<String> getTitles(){
        return plist.keys();
    }
}
