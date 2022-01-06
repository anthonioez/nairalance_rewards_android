package com.nairalance.rewards.android.modules.profile.socket;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.io.ServerSocket;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.profile.Profile;
import com.nairalance.rewards.android.modules.profile.objects.ProfileItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import io.socket.emitter.Emitter;

public class SocketProfileGet
{
    public static final String TAG = SocketProfileGet.class.getSimpleName();

    private SocketJoinCallback      mCallback;
    private Context mContext;
    private String  mError;
    private Handler mHandler;

    public SocketProfileGet(Context c, SocketJoinCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start()
    {
        begin();

        ServerSocket.onError(listenerError);
        ServerSocket.onEvent(Profile.EVENT_PROFILE_GET, listenerProfile);

        try
        {
            JSONObject json = new JSONObject();

            ServerSocket.output(mContext, Profile.EVENT_PROFILE_GET, json, null, listenerError);
        }
        catch (Exception e)
        {
            error(mContext.getString(R.string.err_invalid_args));
        }
    }

    public void stop()
    {
        ServerSocket.offError(listenerError);
        ServerSocket.offEvent(Profile.EVENT_PROFILE_GET, listenerProfile);
    }

    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.profileGetStarted();
            }
        });
    }

    private void done(final ProfileItem profile)
    {
        stop();

        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.profileGetSuccess(profile);
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
                if(mCallback != null) mCallback.profileGetError(msg);

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
                //Logger.e(TAG, "listenerProfile:" + response);

                //JSONObject json = new JSONObject(new JSONTokener(response));
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        JSONObject jsonData = json.optJSONObject(ServerData.data);
                        if(jsonData != null)
                        {
                            ProfileItem item = new ProfileItem();
                            if(item.copyJSON(jsonData))
                            {
                                Prefs.setUsername(mContext, item.username);
                                Prefs.setThumbUrl(mContext, item.thumb);

                                done(item);
                                return;
                            }
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

    public void setCallback(SocketJoinCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketJoinCallback
    {
        public void profileGetStarted();
        public void profileGetSuccess(ProfileItem profile);
        public void profileGetError(String error);
    }

}