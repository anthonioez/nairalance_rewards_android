package com.nairalance.rewards.android.modules.rankings.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.rankings.Ranking;
import com.nairalance.rewards.android.modules.rankings.objects.RankingItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketRankings
{
    private String TAG = SocketRankings.class.getSimpleName();

    private SocketRankingsCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;

    public SocketRankings(Context c, SocketRankingsCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start()
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Ranking.EVENT_LIST, listenerList);

        try
        {
            JSONObject json = new JSONObject();
            ServerSocket.output(mContext, Ranking.EVENT_LIST, json, null, listenerError);
        }
        catch (Exception e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Ranking.EVENT_LIST, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.rankingStarted();
            }
        });
    }

    private void done(final List<RankingItem> items, final long ranking, final int rewards)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.rankingSuccess(items, ranking, rewards);

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
                if(mCallback != null) mCallback.rankingError(msg);
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
                //Logger.e(TAG, "listenerList:" + response);
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        if(json.has(ServerData.data))
                        {
                            JSONObject jsonData = json.getJSONObject(ServerData.data);

                            long ranking = jsonData.optLong(ServerData.ranking);
                            int rewards = jsonData.optInt(ServerData.rewards);

                            Prefs.setRanking(mContext, ranking);
                            Prefs.setRewards(mContext, rewards);

                            List<RankingItem> items = new ArrayList<>();

                            JSONArray jsonList = jsonData.getJSONArray(ServerData.list);
                            for(int i = 0; i < jsonList.length(); i++)
                            {
                                RankingItem item = new RankingItem();
                                if(item.copyJSON(jsonList.getJSONObject(i)))
                                {
                                    items.add(item);
                                }
                            }

                            done(items, ranking, rewards);
                            return;
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_rankings) : msg;
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

    public void setCallback(SocketRankingsCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketRankingsCallback
    {
        public void rankingStarted();
        public void rankingSuccess(List<RankingItem> items, long ranking, int rewards);
        public void rankingError(String error);
    }

}