package com.nairalance.rewards.android.modules.profile.socket;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.profile.Profile;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;

public class SocketJoin
{
    public static final String TAG = SocketJoin.class.getSimpleName();

    private SocketJoinCallback      mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;

    public SocketJoin(Context c, SocketJoinCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(String username, String referrer, byte[] photo)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Profile.EVENT_JOIN, listenerJoin);

        try
        {
            Object photoObj = photo;

            JSONObject json = new JSONObject();
            json.put(ServerData.username,   username);
            json.put(ServerData.referrer,   referrer);
            json.put(ServerData.photo,      photoObj);

            ServerSocket.output(mContext, Profile.EVENT_JOIN, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Profile.EVENT_JOIN, listenerJoin);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.joinStarted();
            }
        });
    }

    private void done(final String phone)
    {
        stop();

        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.joinSuccess(phone);
                mCallback = null;

            }
        });
    }

    private void error(final String msg)
    {
        stop();

        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(mCallback != null) mCallback.joinError(msg);

                mCallback = null;
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

    private Emitter.Listener listenerJoin = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            try
            {
                final String response = args[0].toString();
                //Logger.e(TAG, "listenerJoin:" + response);

                JSONObject json = new JSONObject(new JSONTokener(response));
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        JSONObject jsonData = json.optJSONObject(ServerData.data);
                        if(jsonData != null)
                        {
                            String username = jsonData.getString(ServerData.username);
                            String thumb    = jsonData.optString(ServerData.thumb);

                            if(username != null && username.trim().length() > 0)
                            {
                                Prefs.setThumbUrl(mContext, thumb);
                                Prefs.setUsername(mContext, username);

                                done(msg);
                                return;
                            }
                            else
                            {
                                mError = mContext.getString(R.string.err_server_invalid_data);
                            }
                        }
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
            }

            error(mError);
        }
    };

    public void setCallback(SocketJoinCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketJoinCallback
    {
        public void joinStarted();
        public void joinSuccess(String msg);
        public void joinError(String error);
    }

}