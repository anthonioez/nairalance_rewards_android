package com.nairalance.rewards.android.modules.help.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.help.Support;
import com.nairalance.rewards.android.modules.help.objects.FaqItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketFaq
{
    private String TAG = SocketFaq.class.getSimpleName();

    private SocketFaqCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;

    public SocketFaq(Context c, SocketFaqCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(int index, int size)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Support.EVENT_FAQS, listenerList);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.app,    "rw");
            json.put(ServerData.index,  index);
            json.put(ServerData.size,   size);

            ServerSocket.output(mContext, Support.EVENT_FAQS, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Support.EVENT_FAQS, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.faqStarted();
            }
        });
    }

    private void done(final List<FaqItem> items, final int index)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.faqSuccess(items, index);
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
                if(mCallback != null) mCallback.faqError(msg);
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
                            int index = jsonData.optInt(ServerData.index);
                            int size = jsonData.optInt(ServerData.size);

                            List<FaqItem> items = new ArrayList<>();

                            JSONArray jsonList = jsonData.getJSONArray(ServerData.list);
                            for(int i = 0; i < jsonList.length(); i++)
                            {
                                FaqItem item = FaqItem.copyJSON(jsonList.getJSONObject(i));
                                if(item != null)
                                {
                                    items.add(item);
                                }
                            }

                            done(items, index);
                            return;
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_faqs) : msg;
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

    public void setCallback(SocketFaqCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketFaqCallback
    {
        public void faqStarted();
        public void faqSuccess(List<FaqItem> items, int index);
        public void faqError(String error);
    }

}