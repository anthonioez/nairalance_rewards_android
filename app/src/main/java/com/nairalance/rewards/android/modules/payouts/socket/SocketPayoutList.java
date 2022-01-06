package com.nairalance.rewards.android.modules.payouts.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.payouts.Payout;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketPayoutList
{
    private String TAG = SocketPayoutList.class.getSimpleName();

    private SocketPayoutsCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;

    public SocketPayoutList(Context c, SocketPayoutsCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(int index, int size)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Payout.EVENT_LIST, listenerList);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.index, index);
            json.put(ServerData.size, size);

            ServerSocket.output(mContext, Payout.EVENT_LIST, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Payout.EVENT_LIST, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.payoutStarted();
            }
        });
    }

    private void done(final List<PayoutItem> items, final int index, final int total)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.payoutSuccess(items, index, total);
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
                if(mCallback != null) mCallback.payoutError(msg);
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
                            int index = jsonData.optInt(ServerData.index);
                            int size = jsonData.optInt(ServerData.size);

                            int payouts = jsonData.optInt(ServerData.payouts, 0);

                            List<PayoutItem> items = new ArrayList<>();

                            JSONArray jsonList = jsonData.getJSONArray(ServerData.list);
                            for(int i = 0; i < jsonList.length(); i++)
                            {
                                PayoutItem item = new PayoutItem();
                                if(item.copyJSON(jsonList.getJSONObject(i)))
                                {
                                    items.add(item);
                                }
                            }

                            done(items, index, payouts);
                            return;
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_payouts) : msg;
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

    public void setCallback(SocketPayoutsCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketPayoutsCallback
    {
        public void payoutStarted();
        public void payoutSuccess(List<PayoutItem> items, int index, int payouts);
        public void payoutError(String error);
    }

}