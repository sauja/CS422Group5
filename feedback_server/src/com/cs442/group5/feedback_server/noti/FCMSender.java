package com.cs442.group5.feedback_server.noti;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.gcm.server.Sender;

class FCMSender extends Sender {

    public FCMSender(String key) {
        super(key);
    }

    @Override
    protected HttpURLConnection getConnection(String url) throws IOException {
        String fcmUrl = "https://fcm.googleapis.com/fcm/send";
        return (HttpURLConnection) new URL(fcmUrl).openConnection();
    }
}