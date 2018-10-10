package com.bulletinboard;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;

import io.grpc.stub.StreamObserver;

public class BulletinBoardService extends BulletinBoardGrpc.BulletinBoardImplBase{

	private PostList plist;

	public BulletinBoardService() {
	    plist = new PostList();
	    readInData();
    }

	@Override
	public void getTitles(empty emp, StreamObserver<messageResponse> responseObserver)
	{
	    System.out.println("[Get All Posts]");
		ArrayList<String> titles = new ArrayList<String>();
		Enumeration<String> e = plist.getTitles();

		while (e.hasMoreElements())
			titles.add(e.nextElement());

		messageResponse res = messageResponse.newBuilder().addAllMessage(titles).build();
		
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}

	@Override
	public void postMessage(messagePost post, StreamObserver<messageSuccess> responseObserver) {
		Post dataPost = new Post();
		dataPost.setTitle(post.getTitle());
		dataPost.setMessage(post.getMessage());

		plist.addPost(dataPost);
		writeBackup();

		System.out.println("[Post Message]: " + post.getTitle());

		messageSuccess res = messageSuccess.newBuilder().setSuccess(true).build();

		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}

    @Override
    public void deletePost(messagePostTitle title, StreamObserver<messageSuccess> responseObserver) {

        String key = title.getTitle();
        messageSuccess res;
        if(plist.removePost(key)) {
            writeBackup();
            System.out.println("[Post Delete]: " + key);
            res = messageSuccess.newBuilder().setSuccess(true).build();
        }
        else {
            System.out.println("[Post Delete]: Failed - " + key);
            res = messageSuccess.newBuilder().setSuccess(false).build();
        }

        responseObserver.onNext(res);
        responseObserver.onCompleted();

    }

    @Override
    public void getMessagePost(messagePostTitle title, StreamObserver<messagePostFromServer> responseObserver) {
        String key = title.getTitle();
        messagePostFromServer res;
        Post post = plist.getPost(key);

        if(post == null){
            System.out.println("[Post Retrieved]: Failed - " + key);
            res = messagePostFromServer.newBuilder()
                    .setTitle("")
                    .setMessage("")
                    .setSuccess(false).build();
        }else {
            System.out.println("[Post Retrieved]: " + key);
            res = messagePostFromServer.newBuilder()
                    .setTitle(post.getTitle())
                    .setMessage(post.getMessage())
                    .setSuccess(true).build();
        }

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }

    public boolean writeBackup() {
	    try {
            FileOutputStream fout = new FileOutputStream(new File("data.txt"));
            ObjectOutputStream oout = new ObjectOutputStream(fout);

            oout.writeObject(plist);

            oout.close();
        } catch (Exception e) {
	        return false;
        }

        return true;
    }

    public boolean readInData() {
        try {
            FileInputStream fin = new FileInputStream(new File("data.txt"));
            ObjectInputStream oin = new ObjectInputStream(fin);

            this.plist = (PostList) oin.readObject();

            oin.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
