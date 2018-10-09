package com.bulletinboard;

import com.bulletinboard.BulletinBoardGrpc.BulletinBoardBlockingStub;

import io.grpc.*;

import java.util.ArrayList;

public class BulletinBoardClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5000).usePlaintext().build();
		BulletinBoardBlockingStub stub = BulletinBoardGrpc.newBlockingStub(channel);

		Post post = new Post();
		post.setTitle("Introductory Title");
		post.setMessage("Hello server! I am writing on you!");

		sendPost(post, stub);

		post.setTitle("Java Title");
		post.setMessage("This is a second message.");

		sendPost(post, stub);

		printAllPostTitles(stub);

        messagePostTitle title1 = messagePostTitle.newBuilder()
                                .setTitle("Introductory Title")
                                .build();

        messagePostTitle title2 = messagePostTitle.newBuilder()
                               .setTitle("This one should fail!")
                               .build();

        messageResponse resp = stub.deletePost(title2);
        for(int i = 0; i<resp.getMessageCount();i++)
            System.out.println(resp.getMessage(i));

        printAllPostTitles(stub);


        messagePostFromServer requestedPost;

        requestedPost = stub.getMessagePost(title2);

        if(requestedPost.getSuccess()){
            Post tempPost = Post.buildPostFromServer(requestedPost);
            System.out.println(tempPost);
        }else{
            System.out.println("Message was not found, sorry.");
        }


	}

	static void sendPost(Post post, BulletinBoardBlockingStub stub) {
	    messagePost tempPost;

	    tempPost = messagePost.newBuilder()
                .setTitle(post.getTitle())
                .setMessage(post.getMessage())
                .build();

	    messageResponse res = stub.postMessage(tempPost);

	    for(int i = 0; i<res.getMessageCount();i++)
			System.out.println(res.getMessage(i));
    }

    static void printAllPostTitles(BulletinBoardBlockingStub stub) {
	    System.out.println("Printing all Titles\n-------------------");
        empty emp;
        emp = empty.newBuilder().build();
        messageResponse titles = stub.getTitles(emp);
        for (int i = 0; i < titles.getMessageCount(); i++)
            System.out.println(titles.getMessage(i));

        System.out.println();
    }

}
