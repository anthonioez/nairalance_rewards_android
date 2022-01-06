package com.miciniti.library;

import android.util.Log;

import com.nairalance.rewards.android.BuildConfig;

public class Logger
{
    public static void i(String tag, String message)
    {
        if (BuildConfig.DEBUG)
        {
            Log.i(tag, message);
        }
    }

    public static void e(String tag, String message)
    {
        if (BuildConfig.DEBUG)
        {
            Log.e(tag, message);
        }
    }

    public static void d(String tag, String message, Object... args)
    {
        if (BuildConfig.DEBUG)
        {
            Log.d(tag, String.format(message, args));
        }
    }

    public static void c(String tag, String code, Object[] args)
    {
        if (!BuildConfig.DEBUG) return;

        boolean logged = false;
        if(args != null && args.length > 0)
        {
            for(int i = 0; i < args.length; i++)
            {
                if(args[i] instanceof Exception)
                {
                    Throwable cause = ((Exception) args[i]).getCause();

                    Logger.e(tag, code + ":" + cause);
                    logged = true;
                }
            }
        }

        if(!logged)
        {
            Logger.e(tag, code + ":");
        }
    }


}
