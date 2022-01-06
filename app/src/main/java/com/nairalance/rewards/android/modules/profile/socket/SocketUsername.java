package com.nairalance.rewards.android.modules.profile.socket;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.profile.Profile;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;

public class SocketUsername
{
    public static final String TAG = SocketUsername.class.getSimpleName();

    private SocketUsernameCallback      mCallback;
    private Context mContext;
    private String  mUsername;
    private String  mError;
    private Handler mHandler;

    public SocketUsername(Context c, SocketUsernameCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(String username, boolean existing)
    {
        begin();

        mUsername  = username;

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Profile.EVENT_USERNAME, listenerUsername);

        try
        {
            JSONObject json = new JSONObject();
            json.put(ServerData.username,   username);
            json.put(ServerData.existing,   existing ? Strings.one_ : Strings.zero_);

            ServerSocket.output(mContext, Profile.EVENT_USERNAME, json, null, listenerError);
        }
        catch (JSONException e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Profile.EVENT_USERNAME, listenerUsername);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.usernameStarted();
            }
        });
    }

    private void done(final boolean data)
    {
        stop();

        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.usernameSuccess(mUsername, data);
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
                if(mCallback != null) mCallback.usernameError(msg);

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

    private Emitter.Listener listenerUsername = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            try
            {
                final String response = args[0].toString();
                //Logger.e(TAG, "listenerSend:" + response);

                JSONObject json = new JSONObject(new JSONTokener(response));
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        boolean data = json.getBoolean(ServerData.data);
                        done(data);
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

    public void setCallback(SocketUsernameCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketUsernameCallback
    {
        public void usernameStarted();
        public void usernameSuccess(String username, boolean data);
        public void usernameError(String error);
    }

}