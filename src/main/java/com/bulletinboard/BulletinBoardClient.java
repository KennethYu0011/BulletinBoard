package com.bulletinboard;

import com.bulletinboard.BulletinBoardGrpc.BulletinBoardBlockingStub;

import io.grpc.*;

import java.util.ArrayList;

public class BulletinBoardClient {

    ManagedChannel channel;
    BulletinBoardBlockingStub stub;

    BulletinBoardClient() {
        channel = ManagedChannelBuilder.forAddress("localhost", 5000).usePlaintext().build();
        stub = BulletinBoardGrpc.newBlockingStub(channel);
    }

	public static void main(String[] args) {
        BulletinBoardClient bc = new BulletinBoardClient();

        try {
		    String title = "Introductory Title";
    		String message = "Hello server! I am writing on you!";
    		Post post = new Post(title, message);

    		bc.sendPost(post);

    		title = "Java Title";
    		message = "This is a second message.";
    		post = new Post(title, message);

    		bc.sendPost(post);

    		ArrayList<String> list = bc.getAllPostTitles();
    		System.out.println(list);

    		if (bc.deletePost("Introductory Title"))
    		    System.out.println("Successful deletion.");
    		else
    		    System.out.println("Unsuccessful deletion.");

    		if (bc.deletePost("This one should fail!"))
    		    System.out.println("Successful deletion.");
    		else
    		    System.out.println("Unsuccessful deletion.");

    		list = bc.getAllPostTitles();
    		System.out.println(list);

            post = bc.getPost("Java Title");

       		if (post != null)
		        System.out.println("Returned from server:\n" + post);
		    else
		        System.out.println("Introductory Title Does Not Exist");

        } catch (StatusRuntimeException e) {
		    System.out.println("Communication Failed: Server Down?");
        }
	}

	// Will return a Post if the message attached to title exists, otherwise it will return null.
	Post getPost(String title) throws StatusRuntimeException {
	    messagePostTitle mpTitle;

	    mpTitle = messagePostTitle.newBuilder()
                  .setTitle(title)
                  .build();

	    messagePostFromServer mpPost;

        mpPost = stub.getMessagePost(mpTitle);

	    if (!mpPost.getSuccess())
	        return null;
	    else {
	        return new Post(mpPost.getTitle(), mpPost.getMessage());
        }
    }

	boolean sendPost(Post post) throws StatusRuntimeException {
	    messagePost tempPost;

	    tempPost = messagePost.newBuilder()
                .setTitle(post.getTitle())
                .setMessage(post.getMessage())
                .build();

	    messageSuccess res = stub.postMessage(tempPost);

	    return res.getSuccess();
    }

    ArrayList<String> getAllPostTitles() throws StatusRuntimeException {
        empty emp;
        emp = empty.newBuilder().build();
        messageResponse titles = stub.getTitles(emp);

        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < titles.getMessageCount(); i++)
            list.add(titles.getMessage(i));

        return list;
    }

    boolean deletePost(String title) throws StatusRuntimeException {
        messagePostTitle to_delete = messagePostTitle.newBuilder()
                                   .setTitle(title)
                                   .build();

        messageSuccess resp = stub.deletePost(to_delete);

        return resp.getSuccess();
    }

}
