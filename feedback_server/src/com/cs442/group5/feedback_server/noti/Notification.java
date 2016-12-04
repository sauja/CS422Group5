package com.cs442.group5.feedback_server.noti;

import java.io.IOException;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;


public class Notification {
	static String serverKey = "AIzaSyBb8BPEY8clcDuZ6u_0M0oQuaKTYgXP3AA";
	public static void sendReviewNoti(String tokenid,int storeid,String storeName,String username) throws IOException, InterruptedException
	{
		Sender sender = new FCMSender(serverKey);

		Message message2 = new Message.Builder()
				.addData("title", "You have a new notification")
				.addData("body", username+" commented on your store "+storeName)
				.addData("storeid", String.valueOf(storeid))
				.addData("storename", storeName)
				.collapseKey("message")
				.timeToLive(3)
				.delayWhileIdle(true)
				.build();


		Result  result = sender.send(message2,tokenid,2);
		int i=0;
		while(true)
		{
			i++;
			if(i==10)
				break;
			if(result==null)
			{
				Thread.sleep(5000);

			}

			System.out.println("Notification result "+result);
			break;
		}
	}

}
