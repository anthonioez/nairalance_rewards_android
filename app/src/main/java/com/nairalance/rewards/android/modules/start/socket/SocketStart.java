package com.nairalance.rewards.android.modules.start.socket;

import android.content.Context;
import android.os.Handler;

import com.miciniti.library.io.ServerSocket;

import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class SocketStart
{
    public static final String TAG = SocketStart.class.getSimpleName();

    private SocketSplashCallback mCallback;
    private Context mContext;
    private Handler mHandler;
    private boolean isStopped;

    public SocketStart(Context c, SocketSplashCallback callback)
    {
        mContext = c;
        mCallback = callback;

        mHandler = new Handler();
    }

    public void start()
    {
        begin();

        isStopped = false;

        JSONObject json = new JSONObject();
        ServerSocket.output(mContext, "", json, listenerSuccess, listenerError);
    }

    public void stop()
    {
        isStopped = true;
    }

    private Emitter.Listener listenerSuccess = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            if(isStopped) return;

            int update = 0;
            String msg = "";
            try
            {
                if(args != null)
                {
                    if(args.length > 0 && args[0] instanceof Integer)
                    {
                        update = (int)args[0];
                    }
                    if(args.length > 1 && args[1] instanceof String)
                    {
                        msg = (String)args[1];
                    }
                }
            }
            catch (Exception e)
            {
            }

            done(update, msg);
        }
    };

    private Emitter.Listener listenerError = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            if(isStopped) return;

            error(ServerSocket.getError(mContext, args));
        }
    };


    private void begin()
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.splashStarted();
            }
        });
    }

    private void done(final int update, final String msg)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCallback != null) mCallback.splashSuccess(update, msg);

                stop();
            }
        });
    }

    private void error(final String msg)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(mCallback != null) mCallback.splashError(msg);
            }
        });

        stop();
    }

    public void setCallback(SocketSplashCallback cb)
    {
        this.mCallback = cb;
    }

    public abstract interface SocketSplashCallback
    {
        public void splashStarted();
        public void splashSuccess(int update, String msg);
        public void splashError(String error);
    }

}
