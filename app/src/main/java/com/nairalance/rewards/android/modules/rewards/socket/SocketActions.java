package com.nairalance.rewards.android.modules.rewards.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.rewards.Reward;
import com.nairalance.rewards.android.modules.rewards.objects.RewardActionItem;
import com.nairalance.rewards.android.modules.rewards.objects.RewardItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketActions
{
    private String TAG = SocketActions.class.getSimpleName();

    private SocketActionsCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;

    public SocketActions(Context c, SocketActionsCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(long id)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Reward.EVENT_ACTIONS, listenerList);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.id, id);

            ServerSocket.output(mContext, Reward.EVENT_ACTIONS, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Reward.EVENT_ACTIONS, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.actionStarted();
            }
        });
    }

    private void done(final List<RewardActionItem> items)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.actionSuccess(items);

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
                if(mCallback != null) mCallback.actionError(msg);
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
                            List<RewardActionItem> items = new ArrayList<>();

                            JSONArray jsonList = json.getJSONArray(ServerData.data);
                            for(int i = 0; i < jsonList.length(); i++)
                            {
                                RewardActionItem item = new RewardActionItem();
                                if(item.copyJSON(jsonList.getJSONObject(i)))
                                {
                                    items.add(item);
                                }
                            }

                            done(items);
                            return;
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_rewards_action) : msg;
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
                mError = mContext.getString(R.string.err_server_invalid_data);
            }

            error(mError);
        }
    };

    public void setCallback(SocketActionsCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketActionsCallback
    {
        public void actionStarted();
        public void actionSuccess(List<RewardActionItem> items);
        public void actionError(String error);
    }

}