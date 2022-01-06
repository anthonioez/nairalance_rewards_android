package com.nairalance.rewards.android.modules.payouts.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.payouts.Payout;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutItem;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutRateItem;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutTypeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SocketPayoutData
{
    private String TAG = SocketPayoutData.class.getSimpleName();

    private SocketPayoutDataCallback      mCallback;
    private Context     mContext;
    private String      mError;
    private Handler     mHandler;

    public SocketPayoutData(Context c, SocketPayoutDataCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start()
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Payout.EVENT_DATA, listenerList);

        try
        {
            JSONObject json = new JSONObject();

            ServerSocket.output(mContext, Payout.EVENT_DATA, json, null, listenerError);
        }
        catch (Exception e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Payout.EVENT_DATA, listenerList);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.payoutDataStarted();
            }
        });
    }

    private void done(final int earnings, final String message, final List<PayoutRateItem> rates, final List<PayoutTypeItem> types)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.payoutDataSuccess(earnings, message, rates, types);
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
                if(mCallback != null) mCallback.payoutDataError(msg);
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

                            int earnings = jsonData.optInt(ServerData.earnings, 0);
                            String message = jsonData.optString(ServerData.message, "");

                            List<PayoutRateItem> rates = new ArrayList<>();
                            JSONArray jsonRates = jsonData.getJSONArray(ServerData.rates);
                            for(int i = 0; i < jsonRates.length(); i++)
                            {
                                PayoutRateItem item = new PayoutRateItem();
                                if(item.copyJSON(jsonRates.getJSONObject(i)))
                                {
                                    rates.add(item);
                                }
                            }

                            List<PayoutTypeItem> types = new ArrayList<>();
                            JSONArray jsonTypes = jsonData.getJSONArray(ServerData.types);
                            for(int i = 0; i < jsonTypes.length(); i++)
                            {
                                PayoutTypeItem item = new PayoutTypeItem();
                                if(item.copyJSON(jsonTypes.getJSONObject(i)))
                                {
                                    types.add(item);
                                }
                            }

                            done(earnings, message, rates, types);
                            return;
                        }
                        else
                        {
                            mError = TextUtils.isEmpty(msg) ? mContext.getString(R.string.err_unable_to_load_payout_data) : msg;
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

    public void setCallback(SocketPayoutDataCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketPayoutDataCallback
    {
        public void payoutDataStarted();
        public void payoutDataSuccess(int earnings, String message, List<PayoutRateItem> rates, List<PayoutTypeItem> types);
        public void payoutDataError(String error);
    }

}