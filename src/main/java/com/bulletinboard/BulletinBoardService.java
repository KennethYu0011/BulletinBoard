package com.bulletinboard;

import java.util.ArrayList;

import io.grpc.stub.StreamObserver;

public class BulletinBoardService extends BulletinBoardGrpc.BulletinBoardImplBase{

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
		String title = post.getTitle();
		String message = post.getMessage();

		System.out.print("Received post:");
		System.out.println();
		System.out.print("Title: " + title);
		System.out.println();
		System.out.print("Post: " + message);
		System.out.println();

		messageResponse res = messageResponse.newBuilder().addMessage("Received post, thank you.").build();

		responseObserver.onNext(res);
		responseObserver.onCompleted();
	}
	
}
