package com.bulletinboard;

import java.io.IOException;

import io.grpc.*;

public class BulletinBoardServer {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		Server server = ServerBuilder.forPort(5000).addService(new BulletinBoardService()).build();

		System.out.println("Server online. Listening on port 5000.\n");
		server.start();
		
		server.awaitTermination();

	}

}
