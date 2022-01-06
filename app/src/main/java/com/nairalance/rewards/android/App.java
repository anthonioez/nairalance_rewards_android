package com.nairalance.rewards.android;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.miciniti.library.io.Server;
import com.miciniti.library.io.ServerSocket;
import com.miciniti.library.objects.AppEvent;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.helpers.Device;
import com.nairalance.rewards.android.io.ServerData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class App extends Application implements ServerSocket.SocketInterface
{
    public static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.d(TAG, "Starting...");

        Rewards.load(this, this);

        ServerSocket.getInstance();

        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();

        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);

        Rewards.close(this);
    }

    @Override
    public Object[] payloadDone(Context context, ServerSocket.SocketPayload payload, JSONObject json)
    {
        JSONObject jsonData = null;
        try
        {
            jsonData = json.getJSONObject(ServerData.data);

            String route  = jsonData.optString(ServerData.route, "");
            if(!TextUtils.isEmpty(route))
            {
                ServerSocket.setSocketUrl(route);
                ServerSocket.disconnect();
            }

            String hash     = jsonData.optString(ServerData.hash, "");
            String token    = jsonData.optString(ServerData.token, "");

            Ad.parse(context, jsonData);

            Rewards.feedback = jsonData.optInt(ServerData.feedback, 0);

            final int       update = jsonData.optInt(ServerData.update, 0);
            final String updateMsg = jsonData.optString(ServerData.message, "");
            //-2: maintenance
            //0: no update
            //1: alert update
            //2: force update

            Prefs.setApiHash(context, hash);
            Prefs.setApiToken(context, token);

            Prefs.setLastDev(context, update <= 0 ? System.currentTimeMillis() : 0);

            if(TextUtils.isEmpty(payload.event))
            {
                if(payload.doneListener != null) payload.doneListener.call(update, updateMsg);
            }

            return new Object[]{ update, updateMsg};
        }
        catch (JSONException e)
        {
            if(TextUtils.isEmpty(payload.event))
            {
                if (payload.errorListener != null) payload.errorListener.call("AUTH error occurred!");
            }
            return null;
        }
    }

    @Override
    public boolean shouldHome(Context context)
    {
        return Device.shouldHome(context);
    }

    @Override
    public String getApp(Context context)
    {
        return Rewards.app;
    }

    @Override
    public String getAppVer(Context context)
    {
        return Rewards.appVer;
    }

    @Override
    public String getHash(Context context)
    {
        return Prefs.getApiHash(context);
    }

    @Override
    public String getToken(Context context)
    {
        return Prefs.getApiToken(context);
    }

    @Override
    public String getPush(Context context)
    {
        return Prefs.getPushToken(context);
    }

    @Override
    public float getLon(Context context)
    {
        return 0;
    }

    @Override
    public float getLat(Context context)
    {
        return 0;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AppEvent event)
    {
        if(event == null) return;

        if(event.name.equals(ServerData.socket_connected))
        {
        }
        else if(event.name.equals(ServerData.socket_registered))
        {
            //addGlobalListener();
        }
        else if(event.name.equals(ServerData.socket_disconnected))
        {
            //removeGlobalListener();
        }
    }

    private void addGlobalListener()
    {
        ServerSocket.onError(listenerSyncError);
        ServerSocket.onEvent("sync", listenerSyncEvent);
    }

    public void removeGlobalListener()
    {
        ServerSocket.offError(listenerSyncError);
        ServerSocket.offEvent("sync", listenerSyncEvent);
    }

    private Emitter.Listener listenerSyncError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {

        }
    };

    private Emitter.Listener listenerSyncEvent = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            try
            {
                JSONObject json = (JSONObject)args[0];
                if(json != null)
                {
                    String type = json.optString("type");
                    if(type == null)
                    {

                    }
                    else if(type.equals("reroute"))
                    {

                    }
                }
            }
            catch (Exception e)
            {

            }

        }
    };
}