package com.nairalance.rewards.android.modules.rewards.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.rewards.Reward;
import com.nairalance.rewards.android.modules.rewards.objects.RewardItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketRewards
{
    private String TAG = SocketRewards.class.getSimpleName();

    private SocketRewardsCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;

    public SocketRewards(Context c, SocketRewardsCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(String type, int index, int size)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Reward.EVENT_LIST, listenerList);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.index, index);
            json.put(ServerData.size, size);
            json.put(ServerData.type, type);

            ServerSocket.output(mContext, Reward.EVENT_LIST, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Reward.EVENT_LIST, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.rewardStarted();
            }
        });
    }

    private void done(final List<RewardItem> items, final int index)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.rewardSuccess(items, index);

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
                if(mCallback != null) mCallback.rewardError(msg);
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
                            int index = json.optInt(ServerData.index);
                            int size = json.optInt(ServerData.size);

                            List<RewardItem> items = new ArrayList<>();

                            JSONArray jsonList = jsonData.getJSONArray(ServerData.list);
                            for(int i = 0; i < jsonList.length(); i++)
                            {
                                RewardItem item = new RewardItem();
                                if(item.copyJSON(jsonList.getJSONObject(i)))
                                {
                                    items.add(item);
                                }
                            }

                            done(items, index);
                            return;
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_rewards) : msg;
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

    public void setCallback(SocketRewardsCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketRewardsCallback
    {
        public void rewardStarted();
        public void rewardSuccess(List<RewardItem> items, int index);
        public void rewardError(String error);
    }

}