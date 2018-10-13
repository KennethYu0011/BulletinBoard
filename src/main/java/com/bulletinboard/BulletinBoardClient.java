package com.bulletinboard;

import com.bulletinboard.BulletinBoardGrpc.BulletinBoardBlockingStub;

import io.grpc.*;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BulletinBoardClient {

    ManagedChannel channel;
    BulletinBoardBlockingStub stub;
    private int port = 5000;

    BulletinBoardClient() {
        channel = ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
        stub = BulletinBoardGrpc.newBlockingStub(channel);

        System.out.println("Client connected to server, port: " + port + "\n");
    }

	public static void main(String[] args) {

        BulletinBoardClient bc = new BulletinBoardClient();
        bc.runInputLoop();

	}

	public void runInputLoop(){
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter any of the following commands to interact with the bulletin board:");
		System.out.println("\tpost   \"title\" \"body\"");
		System.out.println("\tget    \"title\"");
		System.out.println("\tdelete \"title\"");
		System.out.println("\tlist");
		System.out.println("\texit");
		System.out.println("------------------------------------------------------------------------");

    	while (true) {
    	    System.out.print("\n> ");
			String line = sc.nextLine();
			if (line.length() == 0) {
				break;
			}
			ArrayList<String> args = getArgs(line);

			if( args.get(0).compareTo("post") == 0 ) {
				parsePost(args);
			}
			else if(args.get(0).compareTo("get") == 0 ) {
				parseGet(args);
			}
			else if(args.get(0).compareTo("delete") == 0 ) {
				parseDelete(args);
			}
			else if(args.get(0).compareTo("list") == 0 ) {
				parseList();
			}
			else if (args.get(0).compareTo("exit") == 0) {
				break;
			}
			else{
				System.out.println("Command not known. Please use post, get, delete, list, or exit.");
			}
		}

		sc.close();
	}

	private ArrayList<String> getArgs(String line){
		ArrayList<String> res = new ArrayList<String>();

		// Get the REST call.
		int firstSpaceIndex = line.indexOf(" ");
		if(firstSpaceIndex == -1) {
			res.add(line);
			return res;
		}

		res.add(line.substring(0, firstSpaceIndex));
		line = line.substring(firstSpaceIndex);

		// Get the two arguments for any REST call.
		String quoteMatch ="[\\s]* |\"([^\"]*)\"";
		Pattern p = Pattern.compile(quoteMatch);

		Matcher m = p.matcher(line);

		String temp;
		while (m.find()) {
			temp = m.group(1);
			if (temp != null) res.add(temp);
		}
    	return res;
	}

	private void parsePost(ArrayList<String> args){
    	if (args.size()!= 3) {
			System.out.println("Illegal arguments. Please try again.");
			return;
		}
    	Post newPost = new Post(args.get(1),args.get(2));

    	try {
			if (!sendPost(newPost))
				System.out.println("Title already exists, must be unique.");
		} catch (StatusRuntimeException e) {
    		System.out.println("Connection failure. Server online and internet connected?");
		}


	}

	private void parseGet(ArrayList<String> args){
    	if (args.size() != 2 ) {
    		System.out.println("Illegal arguments. Please try again!");
    		return;
		}
    	Post post = getPost(args.get(1));
		if(post == null) {
			System.out.println("Title not found, please try again.");
			return;
		}

    	try {
			System.out.println(post.getMessage());
		} catch (StatusRuntimeException e) {
    		System.out.println("Connection failure. Server online and internet connected?");
		}
	}

	private void parseDelete(ArrayList<String> args){
		if (args.size() != 2 ) {
			System.out.println("Illegal arguments. Please try again!");
			return;
		}

    	try {
			if (!deletePost(args.get(1)))
				System.out.println("Title not found, please try again.");
		} catch (StatusRuntimeException e) {
    		System.out.println("Connection failure. Server online and internet connected?");
		}
	}

	private void parseList(){
    	try {
			ArrayList<String> alist = getAllPostTitles();
			System.out.println("Titles\n------");
			for (String str : alist)
				System.out.println(str);
		} catch (StatusRuntimeException e) {
    		System.out.println("Connection failure. Server online and internet connected?");
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
