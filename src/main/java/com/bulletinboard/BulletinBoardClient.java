package com.bulletinboard;

import com.bulletinboard.BulletinBoardGrpc.BulletinBoardBlockingStub;

import io.grpc.*;

public class BulletinBoardClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5000).usePlaintext().build();
		BulletinBoardBlockingStub stub = BulletinBoardGrpc.newBlockingStub(channel);
		messageRequest req = messageRequest.newBuilder().setMessage("Hello world!").build();
		
		messageResponse res = stub.processMessage(req);
		
		for(int i = 0; i<res.getMessageCount();i++)
			System.out.println(res.getMessage(i));
	}

}
