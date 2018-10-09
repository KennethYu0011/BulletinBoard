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
		ArrayList<String> titles = new ArrayList<String>();
		Enumeration<String> e = plist.getTitles();

		while (e.hasMoreElements())
			titles.add(e.nextElement());

		messageResponse res = messageResponse.newBuilder().addAllMessage(titles).build();
		
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}

	@Override
	public void postMessage(messagePost post, StreamObserver<messageResponse> responseObserver) {
		Post dataPost = new Post();
		dataPost.setTitle(post.getTitle());
		dataPost.setMessage(post.getMessage());

		plist.addPost(dataPost);

		System.out.println("Received post: " + post.getTitle());

		messageResponse res = messageResponse.newBuilder().addMessage("Received post, thank you.").build();

		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}

    @Override
    public void deletePost(messagePostTitle title, StreamObserver<messageResponse> responseObserver) {

        String key = title.getTitle();
        messageResponse res;
        if(plist.removePost(key)) {
            System.out.println("Post deleted: " + key);
            res = messageResponse.newBuilder().addMessage("Deleted post, thank you.").build();
        }else {
            System.out.println("Post not found");
            res = messageResponse.newBuilder().addMessage("Post not found, sorry.").build();
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
            res = messagePostFromServer.newBuilder()
                    .setTitle("")
                    .setMessage("")
                    .setSuccess(false).build();
        }else {
            res = messagePostFromServer.newBuilder()
                    .setTitle(post.getTitle())
                    .setMessage(post.getMessage())
                    .setSuccess(true).build();
        }

        responseObserver.onNext(res);
        responseObserver.onCompleted();
    }
}
