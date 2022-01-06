package com.nairalance.rewards.android.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Prefs;

public class ServiceMain extends Service
{
    private static final String TAG = ServiceMain.class.getSimpleName();
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public ServiceMain()
    {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.i(TAG, "onCreate()");

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(this, ServiceMain.class);
        intent.setAction(Rewards.ACTION_ALARM);
        pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.i(TAG, "onDestroy()");

        setupAlarm();
        //notifyManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(TAG, "onStartCommand()");

        if (intent != null)
        {
            String action = intent.getAction();

            Log.e(TAG, "action: " + action);
            if (action.equals(Rewards.ACTION_BOOT))
            {
                boot();
            }
            else if (action.equals(Rewards.ACTION_ALARM))
            {
                alarm();
            }
            else if (action.equals(Rewards.ACTION_CHECK))
            {
                check();
            }
            else if (action.equals(Rewards.ACTION_TOKEN))
            {
                Rewards.registerForPush(this);
            }
        }
        return START_STICKY;
    }

    public void boot()
    {
        setupAlarm();
    }

    private void setupAlarm()
    {
        alarmManager.cancel(pendingIntent);

        long interval = (24 * 60 * 60 * 1000);
        long stamp = System.currentTimeMillis() + interval;

        Log.e(TAG, "next run: " + (stamp));

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, stamp, interval, pendingIntent);
    }

    private void alarm()
    {
        setupAlarm();

        long last = Prefs.getLastRun(this);
        long now = System.currentTimeMillis();
        if(last != 0 && (now - last) > (7 * 60 * 60))
        {
            alert();
        }
    }

    private void check()
    {
        setupAlarm();

        Prefs.setLastRun(this, System.currentTimeMillis());
    }

    private void alert()
    {
        /*
        List<String> messages = new ArrayList<>();
        messages.add("We miss you!");
        messages.add("We have top stories for you!");
        messages.add("Remember to catchup on all top stories!");

        String message = "";

        //ServicePush.sendNotification(this, Rewards.appName, message);
        */
    }
}
