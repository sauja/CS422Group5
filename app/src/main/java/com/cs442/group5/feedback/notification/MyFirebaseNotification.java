package com.cs442.group5.feedback.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.cs442.group5.feedback.DashBoardActivity;
import com.cs442.group5.feedback.MyStorePageActivity;
import com.cs442.group5.feedback.R;
import com.cs442.group5.feedback.StoreActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by sauja7 on 11/15/16.
 */

public class MyFirebaseNotification extends FirebaseMessagingService {

	private static final String TAG = "MyFirebaseMsgService";
Context context;
	public static final int REVIEW_NOTIFICATION_ID=1;
	/**
	 * Called when message is received.
	 *
	 * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
	 */
	// [START receive_message]
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		// [START_EXCLUDE]
		// There are two types of messages data messages and notification messages. Data messages are handled
		// here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
		// traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
		// is in the foreground. When the app is in the background an automatically generated notification is displayed.
		// When the user taps on the notification they are returned to the app. Messages containing both notification
		// and data payloads are treated as notification messages. The Firebase console always sends notification
		// messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
		// [END_EXCLUDE]

		// TODO(developer): Handle FCM messages here.
		// Not getting messages here? See why this may be: https://goo.gl/39bRNJ
		Log.d(TAG, "From: " + remoteMessage.getFrom());
context=this;
		// Check if message contains a data payload.
		if (remoteMessage.getData().size() > 0) {
			Log.d(TAG, "Message data payload: " + remoteMessage.getData());
			Map<String,String> map=remoteMessage.getData();
			sendNotification(map.get("title"),map.get("body"),map.get("storeid"),map.get("storename"));
		}

		// Check if message contains a notification payload.
		if (remoteMessage.getNotification() != null) {
			Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
		}

		// Also if you intend on generating your own notifications as a result of a received FCM
		// message, here is where that should be initiated. See sendNotification method below.
	}
	// [END receive_message]

	/**
	 * Create and show a simple notification containing the received FCM message.
	 *
	 * @param body FCM message body received.
	 */
	private void sendNotification(String title,String body,String storeid, String storename) {
		Log.e(TAG, "sendNotification: "+storeid+ " "+storename );
		/*Intent storeIntent = new Intent(this, Test.class);
		storeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent storePendingIntent = PendingIntent.getActivity(this, 0 , storeIntent,
				PendingIntent.FLAG_ONE_SHOT);
		storeIntent.putExtra("storeid",storeid);
		Intent myStoreIntent = new Intent(this, Test.class);
		storeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent MyStorePendingIntent = PendingIntent.getActivity(this, 0, myStoreIntent,
				PendingIntent.FLAG_ONE_SHOT);
		myStoreIntent.putExtra("storeid",storeid);

		Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(title)
				.setStyle(new NotificationCompat.BigTextStyle()
						.bigText(body))
				.setContentText(body)
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.addAction(R.drawable.button_background,"Store",storePendingIntent)
				//.addAction(R.drawable.button_background,"My Store",MyStorePendingIntent)
		.setContentIntent(storePendingIntent);

		NotificationManager notificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(REVIEW_NOTIFICATION_ID, notificationBuilder.build());
*/
		generateNotification(title,body,storeid,storename);
	}
	private void generateNotification(String title,String body,String storeid,String storename) {

Intent dashboardIntent=new Intent(MyFirebaseNotification.this,DashBoardActivity.class);
		PendingIntent dashboardPendingIntent=PendingIntent.getActivity(context, 0,
				dashboardIntent, 0);
		long when = System.currentTimeMillis();
		String appname = context.getResources().getString(R.string.app_name);
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Notification notification;
		Bundle b=new Bundle();
		b.putString("storeid",storeid);
		b.putString("storename",storename);
		Intent intent = new Intent(context, StoreActivity.class);
		Intent intent2 = new Intent(context, MyStorePageActivity.class);
		intent.putExtras(b);
		intent2.putExtras(b);
		//intent.putExtra("storeid",storeid);
		//intent.putExtra("storename",storename);
		//intent2.putExtra("storeid",storeid);
		//intent2.putExtra("storename",storename);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		PendingIntent contentIntent2 = PendingIntent.getActivity(context, 0,
				intent2, 0);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		notification = builder
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(title)
				.setAutoCancel(true)
				.setStyle(new NotificationCompat.BigTextStyle()
						.bigText(body))

				.addAction(R.drawable.ic_notification,"Store",contentIntent)
				.addAction(R.drawable.ic_notification,"My Store",contentIntent2)
				.setSound(defaultSoundUri)
				.setVibrate(new long[] { 1000, 1000, 1000 })
				.setContentIntent(dashboardPendingIntent)
				.build();

		notificationManager.notify(REVIEW_NOTIFICATION_ID, notification);


	}

}
