package com.nairalance.rewards.android.modules.payouts.socket;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.payouts.Payout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;

public class SocketPayoutRequest
{
    public static final String TAG = SocketPayoutRequest.class.getSimpleName();

    private SocketPayoutRequestCallback      mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;

    public SocketPayoutRequest(Context c, SocketPayoutRequestCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(long provider, long points, String name, String account)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Payout.EVENT_REQUEST, listenerSend);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.provider, provider);
            json.put(ServerData.points, points);
            json.put(ServerData.name, name);
            json.put(ServerData.account, account);

            ServerSocket.output(mContext, Payout.EVENT_REQUEST, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Payout.EVENT_REQUEST, listenerSend);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.payoutRequestStarted();
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
                if (mCallback != null) mCallback.payoutRequestSuccess(phone);
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
                if(mCallback != null) mCallback.payoutRequestError(msg);

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

                    int earnings = json.optInt(ServerData.data, 0);
                    if(earnings != 0)
                    {
                        Prefs.setRewards(mContext, earnings);
                    }

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

    public void setCallback(SocketPayoutRequestCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketPayoutRequestCallback
    {
        public void payoutRequestStarted();
        public void payoutRequestSuccess(String message);
        public void payoutRequestError(String error);
    }

}