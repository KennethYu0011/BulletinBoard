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

	/**
	* Attempt to retrieve a post from the server with the given title.
	* @param title The title of the target post.
	* @exception StatusRuntimeException Occurs when server is down or message lost.
	* @return Post Return a Post if successful, null otherwise.
	*/
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

	/**
	* Attempt to place a post on the server.
	* @param post The Post object that is to be placed on the server.
	* @exception StatusRuntimeException Occurs when server is down or message lost.
	* @return boolean States whether the post was successful placed on the server.
	*/
	boolean sendPost(Post post) throws StatusRuntimeException {
	    messagePost tempPost;

	    tempPost = messagePost.newBuilder()
                .setTitle(post.getTitle())
                .setMessage(post.getMessage())
                .build();

	    messageSuccess res = stub.postMessage(tempPost);

	    return res.getSuccess();
    }

	/**
	* Get the list of titles for all Post objects stored on the server.
	* @exception StatusRuntimeException Occurs when server is down or message lost.
	* @return ArrayList<String> An ArrayList containing all titles for posts on the server.
	*/
    ArrayList<String> getAllPostTitles() throws StatusRuntimeException {
        empty emp;
        emp = empty.newBuilder().build();
        messageResponse titles = stub.getTitles(emp);

        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < titles.getMessageCount(); i++)
            list.add(titles.getMessage(i));

        return list;
    }

	/**
	* Delete a Post of the given title on the server.
	* @param title The title of the Post that will be targeted and deleted.
	* @exception StatusRuntimeException Occurs when server is down or message lost.
	* @return boolean States whether the deletion was succesfully completed on server.
	*/
    boolean deletePost(String title) throws StatusRuntimeException {
        messagePostTitle to_delete = messagePostTitle.newBuilder()
                                   .setTitle(title)
                                   .build();

        messageSuccess resp = stub.deletePost(to_delete);

        return resp.getSuccess();
    }

}
