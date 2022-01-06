package com.nairalance.rewards.android.modules.profile.socket;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.profile.Profile;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class SocketProfileEmail
{
    public static final String TAG = SocketProfileEmail.class.getSimpleName();

    private SocketProfileEmailCallback      mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;

    public SocketProfileEmail(Context c, SocketProfileEmailCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(String email, String password)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Profile.EVENT_PROFILE_EMAIL, listenerProfile);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.email,      email);
            json.put(ServerData.password,   password);

            ServerSocket.output(mContext, Profile.EVENT_PROFILE_EMAIL, json, null, listenerError);
        }
        catch (Exception e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Profile.EVENT_PROFILE_EMAIL, listenerProfile);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.profileEmailStarted();
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
                if (mCallback != null) mCallback.profileEmailSuccess(msg);
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
                if(mCallback != null) mCallback.profileEmailError(msg);

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

    private Emitter.Listener listenerProfile = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            try
            {
                JSONObject json = (JSONObject)args[0]; //.toString();
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        JSONObject jsonData = json.optJSONObject(ServerData.data);
                        if(jsonData != null)
                        {
                            //String thumb    = jsonData.getString(ServerData.thumb);

                            done(msg);
                            return;
                        }

                        mError = mContext.getString(R.string.err_server_invalid_data);
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

    public void setCallback(SocketProfileEmailCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketProfileEmailCallback
    {
        public void profileEmailStarted();
        public void profileEmailSuccess(String message);
        public void profileEmailError(String error);
    }

}