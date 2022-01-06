package com.nairalance.rewards.android.modules.earnings.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.earnings.Earning;
import com.nairalance.rewards.android.modules.earnings.objects.EarningItem;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketEarnings
{
    private String TAG = SocketEarnings.class.getSimpleName();

    private SocketEarningsCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;

    public SocketEarnings(Context c, SocketEarningsCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(int index, int size)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Earning.EVENT_LIST, listenerList);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.index, index);
            json.put(ServerData.size, size);

            ServerSocket.output(mContext, Earning.EVENT_LIST, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Earning.EVENT_LIST, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.earningStarted();
            }
        });
    }

    private void done(final List<EarningItem> items, final int index, final int total)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.earningSuccess(items, index, total);
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
                if(mCallback != null) mCallback.earningError(msg);
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
                final String response = args[0].toString();
                //Logger.e(TAG, "listenerList:" + response);

                JSONObject json = new JSONObject(new JSONTokener(response));
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

                            int earnings = jsonData.optInt(ServerData.earnings, 0);

                            List<EarningItem> items = new ArrayList<>();

                            JSONArray jsonList = jsonData.getJSONArray(ServerData.list);
                            for(int i = 0; i < jsonList.length(); i++)
                            {
                                EarningItem item = new EarningItem();
                                if(item.copyJSON(jsonList.getJSONObject(i)))
                                {
                                    items.add(item);
                                }
                            }

                            done(items, index, earnings);
                            return;
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_earnings) : msg;
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

    public void setCallback(SocketEarningsCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketEarningsCallback
    {
        public void earningStarted();
        public void earningSuccess(List<EarningItem> items, int index, int earnings);
        public void earningError(String error);
    }

}