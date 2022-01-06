package com.nairalance.rewards.android.modules.phone.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.phone.Phone;
import com.nairalance.rewards.android.modules.rewards.Reward;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import io.socket.emitter.Emitter;

public class SocketPhoneVerify
{
    public static final String TAG = SocketPhoneVerify.class.getSimpleName();

    private SocketPhoneVerifyCallback      mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;
    private String mPhone;

    public SocketPhoneVerify(Context c, SocketPhoneVerifyCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(String phone, String code)
    {
        begin();

        mPhone = phone;

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Phone.EVENT_VERIFY, listenerVerify);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.phone, phone);
            json.put(ServerData.code, code);

            ServerSocket.output(mContext, Phone.EVENT_VERIFY, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Phone.EVENT_VERIFY, listenerVerify);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.phoneVerifyStarted();
            }
        });
    }

    private void done(final String msg)
    {
        stop();

        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.phoneVerifySuccess(msg);
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
                if(mCallback != null) mCallback.phoneVerifyError(msg);
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

    private Emitter.Listener listenerVerify = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            try
            {
                final String response = args[0].toString();
                //Logger.e(TAG, "listenerForgot:" + response);

                JSONObject json = new JSONObject(new JSONTokener(response));
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        JSONObject jsonData = json.optJSONObject(ServerData.data);
                        if(jsonData != null)
                        {
                            long id         = jsonData.getLong(ServerData.id);
                            String token    = jsonData.getString(ServerData.token);
                            String username = jsonData.getString(ServerData.username);
                            String email    = jsonData.getString(ServerData.email);

                            if(id > 0 && token != null && token.trim().length() > 0)
                            {
                                Prefs.setPhone(mContext, mPhone);

                                Prefs.setId(mContext, id);
                                Prefs.setApiToken(mContext, token);

                                Prefs.setUsername(mContext, username);
                                Prefs.setEmail(mContext, email);
                                Prefs.setPushTokenSent(mContext, false);

                                if(TextUtils.isEmpty(username))
                                    Rewards.signin(mContext);
                                else
                                    Rewards.login(mContext);

                                done(msg);
                                return;
                            }
                            else
                            {
                                mError = mContext.getString(R.string.err_server_invalid_data);
                            }
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
            }

            error(mError);
        }
    };

    public void setCallback(SocketPhoneVerifyCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketPhoneVerifyCallback
    {
        public void phoneVerifyStarted();
        public void phoneVerifySuccess(String msg);
        public void phoneVerifyError(String error);
    }

}