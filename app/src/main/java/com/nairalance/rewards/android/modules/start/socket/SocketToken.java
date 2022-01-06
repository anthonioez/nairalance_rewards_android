package com.nairalance.rewards.android.modules.start.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.helpers.Device;
import com.nairalance.rewards.android.io.ServerData;

public class SocketToken
{
    public static final String TAG = SocketToken.class.getSimpleName();

    private SocketTokenCallback mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;

    public SocketToken(Context c, SocketTokenCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start()
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Device.EVENT_TOKEN, listenerToken);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.token, Prefs.getPushToken(mContext));
            json.put(ServerData.status, Prefs.getPushStatus(mContext) ? Strings.one_ : Strings.zero_);

            ServerSocket.output(mContext, Device.EVENT_TOKEN, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Device.EVENT_TOKEN, listenerToken);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.tokenStarted();
            }
        });
    }


    private void done(final String msg)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.tokenSuccess(msg);

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
                if(mCallback != null) mCallback.tokenError(msg);
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

    private Emitter.Listener listenerToken = new Emitter.Listener()
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
                            Prefs.setPushTokenSent(mContext, true);
                            done(msg);
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.unable_to_store_token) : msg;
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

    public void setCallback(SocketTokenCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketTokenCallback
    {
        public void tokenStarted();
        public void tokenSuccess(String msg);
        public void tokenError(String error);
    }

}
