package com.bulletinboard;

import com.bulletinboard.BulletinBoardGrpc.BulletinBoardBlockingStub;

import io.grpc.*;

public class BulletinBoardClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5000).usePlaintext().build();
		BulletinBoardBlockingStub stub = BulletinBoardGrpc.newBlockingStub(channel);

		Post post = new Post();
		post.setTitle("Introductory Title");
		post.setMessage("Hello server! I am writing on you!");

		sendPost(post, stub);
	}

	static void sendPost(Post post, BulletinBoardBlockingStub stub) {
	    messagePost tempPost;

	    tempPost = messagePost.newBuilder()
                .setTitle(post.getTitle())
                .setMessage(post.getMessage())
                .build();

	    messageResponse res = stub.printRequest(tempPost);

	    for(int i = 0; i<res.getMessageCount();i++)
			System.out.println(res.getMessage(i));
    }

}
