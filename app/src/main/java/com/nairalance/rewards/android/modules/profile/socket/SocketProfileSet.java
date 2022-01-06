package com.nairalance.rewards.android.modules.profile.socket;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.profile.Profile;
import com.nairalance.rewards.android.modules.profile.objects.ProfileItem;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class SocketProfileSet
{
    public static final String TAG = SocketProfileSet.class.getSimpleName();

    private SocketProfileSetCallback      mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;

    public SocketProfileSet(Context c, SocketProfileSetCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start(String username, int gender, String city, String state, byte[] photo)
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Profile.EVENT_PROFILE_SET, listenerProfile);

        try
        {
            Object photoObj = photo;

            JSONObject json = new JSONObject();
            json.put(ServerData.username,   username);
            json.put(ServerData.gender,     gender);
            json.put(ServerData.city,       city);
            json.put(ServerData.state,      state);
            json.put(ServerData.photo,      photoObj);

            ServerSocket.output(mContext, Profile.EVENT_PROFILE_SET, json, null, listenerError);
        }
        catch (Exception e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Profile.EVENT_PROFILE_SET, listenerProfile);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.profileSetStarted();
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
                if (mCallback != null) mCallback.profileSetSuccess(msg);
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
                if(mCallback != null) mCallback.profileSetError(msg);

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

    public void setCallback(SocketProfileSetCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketProfileSetCallback
    {
        public void profileSetStarted();
        public void profileSetSuccess(String message);
        public void profileSetError(String error);
    }

}