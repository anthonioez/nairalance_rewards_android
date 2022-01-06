package com.nairalance.rewards.android.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import com.miciniti.library.Links;
import com.miciniti.library.Utils;
import com.miciniti.library.io.ServerData;
import com.miciniti.library.io.ServerSocket;

import java.util.HashMap;
import java.util.Locale;

import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;

import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.activities.ActivityMaintain;
import com.nairalance.rewards.android.activities.ActivityUpdate;

/**
 * Created by Miciniti onEvent 19/04/16.
 */
public class Device
{
    private static final String TAG = Device.class.getSimpleName();
    public static final String EVENT_TOKEN      = "devices_token";
    public static final String EVENT_REGISTER   = "devices_register";
    public static final String EVENT_CONNECT    = "devices_connect";

    public static HashMap<String, String> getData(Context context)
    {
        HashMap<String, String> data = new HashMap<>();

        data.put(ServerData.tag,         Utils.getID(context));

        data.put(ServerData.make,        Build.MANUFACTURER.toUpperCase(Locale.US));
        data.put(ServerData.name,        Build.MODEL);
        data.put(ServerData.model,       Build.PRODUCT);

        data.put(ServerData.os,          Strings.android);
        data.put(ServerData.osver,       String.valueOf(Build.VERSION.RELEASE));

        data.put(ServerData.width,       String.valueOf(Rewards.screenSize.width()));
        data.put(ServerData.height,      String.valueOf(Rewards.screenSize.height()));

        data.put(ServerData.lon,         String.valueOf(Prefs.getLastLon(context)));
        data.put(ServerData.lat,         String.valueOf(Prefs.getLastLat(context)));

        data.put(ServerData.network,     Utils.getNetwork(context));
        data.put(ServerData.country,     Utils.getCountry(context));

        data.put(ServerData.appver,      Rewards.appVer);
        data.put(ServerData.stamp,       String.valueOf(System.currentTimeMillis()));

        return data;
    }

    public static boolean shouldHome(Context context)
    {
        long last = Prefs.getLastDev(context);

        //if last device is > 1 day
        return (TextUtils.isEmpty(Prefs.getApiHash(context)) || last == 0 || (System.currentTimeMillis() - last) > 24*3600*1000);
    }

    public static void maintain(Activity activity, String msg)
    {
        Intent intent = new Intent(activity, ActivityMaintain.class);
        intent.putExtra(Strings.message, msg);
        activity.startActivity(intent);

        ServerSocket.disconnect();
        activity.finish();

    }

    public static void update(Activity activity, String msg)
    {
        Intent intent = new Intent(activity, ActivityUpdate.class);
        intent.putExtra(Strings.message, msg);
        activity.startActivity(intent);

        ServerSocket.disconnect();
        activity.finish();

    }

    public static void update(final Activity activity, int update, String message, final Runnable runnable)
    {
        if(update == -2)
        {
            maintain(activity, message);
        }
        else if(update == 1)
        {
            if(TextUtils.isEmpty(message))
                message = activity.getString(R.string.update_info);

            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle(Rewards.appName);
            dialog.setMessage(message);
            dialog.setPositiveButton(activity.getString(R.string.continew), new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();

                    if(runnable != null) runnable.run();
                }
            });
            dialog.setNeutralButton(activity.getString(R.string.update_now), new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int id)
                {
                    dialog.cancel();

                    if(runnable != null) runnable.run();

                    Links.openStoreUrl(activity);
                }
            });
            dialog.show();
        }
        else if(update == 2)
        {
            update(activity, message);
        }
        else
        {
            if(runnable != null) runnable.run();
        }
    }
}
