package com.bulletinboard;

import java.util.ArrayList;
import java.util.Enumeration;

import io.grpc.stub.StreamObserver;

public class BulletinBoardService extends BulletinBoardGrpc.BulletinBoardImplBase{

	private PostList plist;

	public BulletinBoardService() {
	    plist = new PostList();
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
}
