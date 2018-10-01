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
	
}
