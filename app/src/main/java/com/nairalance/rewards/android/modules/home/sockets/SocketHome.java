package com.nairalance.rewards.android.modules.home.sockets;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.home.Home;
import com.nairalance.rewards.android.modules.home.objects.HomeItem;
import com.nairalance.rewards.android.modules.rewards.objects.RewardTypeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketHome
{
    private String TAG = SocketHome.class.getSimpleName();

    private SocketHomeCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;

    public SocketHome(Context c, SocketHomeCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start()
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Home.EVENT_HOME, listenerList);

        try
        {
            JSONObject json = new JSONObject();

            ServerSocket.output(mContext, Home.EVENT_HOME, json, null, listenerError);
        }
        catch (Exception e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Home.EVENT_HOME, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.homeStarted();
            }
        });
    }

    private void done(final HomeItem data)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.homeSuccess(data);
                mCallback = null;

            }
        });

        stop();
    }

    private void error(final String msg)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(mCallback != null) mCallback.homeError(msg);
                mCallback = null;
            }
        });

        stop();
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
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);


                    if (status)
                    {
                        if(json.has(ServerData.data))
                        {
                            JSONObject jsonData = json.getJSONObject(ServerData.data);

                            HomeItem item = new HomeItem();
                            if(item.copyJSON(mContext, jsonData))
                            {
                                Prefs.setUsername(mContext, item.username);
                                Prefs.setThumbUrl(mContext, item.thumb);
                                Prefs.setRewards(mContext, item.rewards);
                                Prefs.setRanking(mContext, item.ranking);

                                done(item);
                                return;
                            }
                        }

                        mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_homes) : msg;
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

    public void setCallback(SocketHomeCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketHomeCallback
    {
        public void homeStarted();
        public void homeSuccess(HomeItem data);
        public void homeError(String error);
    }

}