package com.nairalance.rewards.android.modules.payouts.socket;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.payouts.Payout;
import com.nairalance.rewards.android.modules.phone.Phone;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;

public class SocketPayoutCancel
{
    public static final String TAG = SocketPayoutCancel.class.getSimpleName();

    private SocketPayoutCancelCallback      mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;

    public SocketPayoutCancel(Context c, SocketPayoutCancelCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(long id)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Payout.EVENT_CANCEL, listenerSend);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.id, id);

            ServerSocket.output(mContext, Payout.EVENT_CANCEL, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Payout.EVENT_CANCEL, listenerSend);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.payoutCancelStarted();
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
                if (mCallback != null) mCallback.payoutCancelSuccess(phone);
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
                if(mCallback != null) mCallback.payoutCancelError(msg);

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
                JSONObject json = (JSONObject)args[0];
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
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

    public void setCallback(SocketPayoutCancelCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketPayoutCancelCallback
    {
        public void payoutCancelStarted();
        public void payoutCancelSuccess(String message);
        public void payoutCancelError(String error);
    }

}