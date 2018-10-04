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
	public void processMessage(messageRequest request, StreamObserver<messageResponse> responseObserver) 
	{
		String s = request.getMessage();
		
		ArrayList<String> string = new ArrayList<String>();
		
		string.add(s);
		string.add(s);
		string.add(s);
		string.add(s);

		messageResponse res = messageResponse.newBuilder().addAllMessage(string).build();
		
		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}

	@Override
	public void printRequest(messagePost post, StreamObserver<messageResponse> responseObserver) {
		Post dataPost = new Post();
		dataPost.setTitle(post.getTitle());
		dataPost.setMessage(post.getMessage());

		plist.addPost(dataPost);

		System.out.println("Received post, list of all posts is:");
        Enumeration<String> e = plist.getTitles();

        while (e.hasMoreElements())
            System.out.println(e.nextElement());


		messageResponse res = messageResponse.newBuilder().addMessage("Received post, thank you.").build();

		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
	
}
