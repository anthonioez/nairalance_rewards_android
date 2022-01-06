package com.nairalance.rewards.android.modules.home.sockets;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.Utils;
import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.home.Home;

import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;

public class SocketActionRequest
{
    private String TAG = SocketActionRequest.class.getSimpleName();

    private SocketActionRequestCallback      mCallback;
    private Context     mContext;
    private Object      mData;
    private String      mError;
    private Handler     mHandler;

    public SocketActionRequest(Context c, Object data, SocketActionRequestCallback callback)
    {
        mContext = c;
        mCallback = callback;
        mData = data;

        mHandler = new Handler();
    }

    public void start(long id, String actions, String data)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Home.EVENT_REQUEST, listenerList);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.id,         id);
            json.put(ServerData.actions,    actions);
            json.put(ServerData.data,       data);

            ServerSocket.output(mContext, Home.EVENT_REQUEST, json, null, listenerError);
        }
        catch (Exception e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Home.EVENT_REQUEST, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.actionRequestStarted();
            }
        });
    }

    private void done(final String hash)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.actionRequestSuccess(SocketActionRequest.this, hash);
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
                if(mCallback != null) mCallback.actionRequestError(msg);
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
                JSONObject json = (JSONObject) args[0];

                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        if(json.has(ServerData.data))
                        {
                            String hash = json.getString(ServerData.data);

                            done(hash);
                            return;
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_request) : msg;
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
                mError = mContext.getString(R.string.err_server_invalid_data);
            }

            error(mError);
        }
    };

    public void setCallback(SocketActionRequestCallback cb)
    {
        this.mCallback = cb;
    }

    public Object getData()
    {
        return mData;
    }

    public abstract interface SocketActionRequestCallback
    {
        public void actionRequestStarted();
        public void actionRequestSuccess(SocketActionRequest request, String hash);
        public void actionRequestError(String error);
    }

}