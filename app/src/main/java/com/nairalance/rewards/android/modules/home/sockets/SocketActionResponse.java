package com.nairalance.rewards.android.modules.home.sockets;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.Utils;
import com.miciniti.library.io.ServerSocket;
import com.miciniti.library.objects.AppEvent;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.RewardsEvent;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.home.Home;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;

public class SocketActionResponse
{
    private String TAG = SocketActionResponse.class.getSimpleName();

    private SocketActionResponseCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;
    private String      mAction;

    public SocketActionResponse(Context c, SocketActionResponseCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(String hash, String action, String data, String info)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Home.EVENT_RESPONSE, listenerList);

        try
        {
            this.mAction = action;

            JSONObject json = new JSONObject();
            json.put(ServerData.hash,   hash);
            json.put(ServerData.action, action);
            json.put(ServerData.data,   data);
            json.put(ServerData.info,   info);

            ServerSocket.output(mContext, Home.EVENT_RESPONSE, json, null, listenerError);
        }
        catch (Exception e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Home.EVENT_RESPONSE, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.actionResponseStarted();
            }
        });
    }

    private void done(final String msg, final int points)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.actionResponseSuccess(msg, points, mAction);
                mCallback = null;

                stop();
            }
        });
    }

    private void error(final String msg)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(mCallback != null) mCallback.actionResponseError(msg);
                mCallback = null;

                stop();
            }
        });

    }

    private Emitter.Listener listenerError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            error(ServerSocket.getError(mContext, args));
        }
    };

    private Emitter.Listener listenerList = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            try
            {
                JSONObject json = (JSONObject)args[0];
                //Logger.e(TAG, "listenerList:" + response);

                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        int points = json.optInt(ServerData.data, 0);

                        if(points > 0)
                        {
                            Prefs.setRewards(mContext, Prefs.getRewards(mContext) + points);

                            EventBus.getDefault().post(new AppEvent(RewardsEvent.REWARDED_POINTS, msg));
                        }

                        done(msg, points);
                    }
                    else
                    {
                        mError = msg;
                    }
                }
                else
                {
                    mError = mContext.getString(R.string.err_server_incorrect_response);
                }
            }
            catch (Exception e)
            {
                mError = mContext.getString(R.string.err_occurred);
                mError = mContext.getString(R.string.err_server_invalid_data);
            }

            error(mError);
        }
    };

    public void setCallback(SocketActionResponseCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketActionResponseCallback
    {
        public void actionResponseStarted();
        public void actionResponseSuccess(String msg, int points, String action);
        public void actionResponseError(String error);
    }

}