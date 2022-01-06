package com.nairalance.rewards.android.helpers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;

import com.nairalance.rewards.android.Prefs;

public class Notify
{
    //public static final int CONTENT_ID = 12;
    //public static final int SOURCE_ID = 13;
    //public static final int INTEREST_ID = 14;
    //public static final int SEARCH_ID = 15;

    public static void cancel(Context context, int id)
    {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(id);
    }

    public static Notification get(Context context, NotificationCompat.Builder builder)
    {
        Notification notification = null;

        try
        {
            String sound = Prefs.getPushSound(context);
            if(!TextUtils.isEmpty(sound))
            {
                //builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                builder.setSound(Uri.parse(sound));
            }

            /*
            int granted = ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE);
            if(Prefs.getPushVibrate(context) && granted == PackageManager.PERMISSION_GRANTED)
            {
                builder.setVibrate(new long[] { 1000, 1000, 1000});
            }

            if(Prefs.getPushLED(context))
            {
                builder.setLights(Color.RED, 3000, 3000);
            }*/

            notification = builder.build();
        }
        catch(SecurityException se)
        {
            se.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return notification;
    }

    public static void show(Context context, int id, Notification notification)
    {
        try
        {
            if(notification != null)
            {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(id, notification);
            }
        }
        catch(SecurityException se)
        {
            se.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void show(Context context, int id, NotificationCompat.Builder builder)
    {
        try
        {
            Notification notification = get(context, builder);
            if(notification != null)
            {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(id, notification);
            }
        }
        catch(SecurityException se)
        {
            se.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
