package com.nairalance.rewards.android.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.RewardsAnalytics;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.activities.ActivityHome;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.helpers.Notify;
import com.nairalance.rewards.android.io.ServerData;

public class ServicePush extends FirebaseMessagingService
{

    private static final String TAG = ServicePush.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend onEvent generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        Ad.makePossible(this);

        //String from = remoteMessage.getFrom();
        String pid = null;
        String title = null;
        String body = null;
        String path = null;
        String data = null;

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if(notification != null)
        {
            title = notification.getTitle();
            body = notification.getBody();
        }

        Map<String, String> payload = remoteMessage.getData();
        if(payload != null && payload.size() > 0)
        {
            //Logger.d(TAG, "Message data payload: " + payload);

            if (title == null) title = payload.get(ServerData.title);
            if (body == null) body = payload.get(ServerData.body);

            pid = payload.get(ServerData.pid);
            path = payload.get(ServerData.path);
        }

        if (path != null && payload != null)
        {
            pushPayload(this, title, body, pid, path, payload);
        }
        else
        {
            pushMessage(this, title, body);
        }

        RewardsAnalytics.logEvent(this, "push", pid);
    }

    private void pushPayload(Context context, String title, String body, String pid, String path, Map<String, String> payload)
    {
        if (body == null) return;

        String link = payload.get(ServerData.link);
        String image = payload.get(ServerData.image);

        String type = payload.get(ServerData.type);
        String data = payload.get(ServerData.data);

        Intent intent = new Intent(this, ActivityHome.class);
        intent.putExtra(ServerData.sender,  Strings.push);
        intent.putExtra(ServerData.pid,     pid);
        intent.putExtra(ServerData.title,   title);
        intent.putExtra(ServerData.path,    path);
        intent.putExtra(ServerData.image,   image);
        intent.putExtra(ServerData.link,    link);

        intent.putExtra(ServerData.type,    type);
        intent.putExtra(ServerData.data,    data);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Long.toString(System.currentTimeMillis()));

        String content = body.replaceAll("\n", "<br/>");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Rewards.notificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(Html.fromHtml(content))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);

        if(!TextUtils.isEmpty(title))
            builder.setContentTitle(title);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(Html.fromHtml(content));

        if(!TextUtils.isEmpty(title))
            bigTextStyle.setBigContentTitle(title);

        builder.setStyle(bigTextStyle);

        try
        {
            if(!TextUtils.isEmpty(image) && Prefs.getImagePush(ServicePush.this))
            {
                builder.setLargeIcon(Rewards.picasso.load(image).get());
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            int count = Prefs.getPushCount(ServicePush.this);
            Prefs.setPushCount(ServicePush.this, count+1);

            Notify.show(ServicePush.this, count, builder);
        }
    }

    public static void pushMessage(Context context, String title, String message)
    {
        Intent intent = new Intent(context, ActivityHome.class);
        intent.putExtra(ServerData.sender, Strings.push);

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Long.toString(System.currentTimeMillis()));

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_status)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if(!TextUtils.isEmpty(title))
            builder.setContentTitle(title);

        if(!TextUtils.isEmpty(message))
            builder.setContentText(message);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        if(!TextUtils.isEmpty(message))
            bigTextStyle.bigText(Html.fromHtml(message));

        if(!TextUtils.isEmpty(title))
            bigTextStyle.setBigContentTitle(title);

        builder.setStyle(bigTextStyle);

        int count = Prefs.getPushCount(context);
        Prefs.setPushCount(context, count+1);

        Notify.show(context, count, builder);

    }
}