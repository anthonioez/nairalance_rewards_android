package com.nairalance.rewards.android.modules.phone.socket;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.phone.Phone;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;

public class SocketPhoneSend
{
    public static final String TAG = SocketPhoneSend.class.getSimpleName();

    private SocketPhoneSendCallback      mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;

    public SocketPhoneSend(Context c, SocketPhoneSendCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(String phone, boolean call)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Phone.EVENT_SIGNIN, listenerSend);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.phone, phone);
            json.put(ServerData.type, call);

            ServerSocket.output(mContext, Phone.EVENT_SIGNIN, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Phone.EVENT_SIGNIN, listenerSend);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.phoneSendStarted();
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
                if (mCallback != null) mCallback.phoneSendSuccess(phone);
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
                if(mCallback != null) mCallback.phoneSendError(msg);

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

    private Emitter.Listener listenerSend = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            try
            {
                final String response = args[0].toString();
                //Logger.e(TAG, "listenerSend:" + response);

                JSONObject json = new JSONObject(new JSONTokener(response));
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        //String data = json.optString(ServerData.data);
                        done(msg);
                        return;
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

    public void setCallback(SocketPhoneSendCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketPhoneSendCallback
    {
        public void phoneSendStarted();
        public void phoneSendSuccess(String message);
        public void phoneSendError(String error);
    }

}