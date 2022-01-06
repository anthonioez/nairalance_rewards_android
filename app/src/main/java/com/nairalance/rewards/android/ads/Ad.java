package com.nairalance.rewards.android.ads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.miciniti.library.Logger;

import org.json.JSONObject;

import com.nairalance.rewards.android.Prefs;

public class Ad
{
    private static final String TAG = Ad.class.getSimpleName();

    public static void parse(Context context, JSONObject json)
    {
        int adinter  = json.optInt("adinters", 0);
        int limit = adinter % 100;
        int type = adinter / 100;

        Prefs.setAdIntersType(context, type);
        Prefs.setAdIntersLimit(context, limit);

        int adbanner  = json.optInt("adbanner", 0xFF);  //TODO
        Prefs.setAdBannerSlots(context, adbanner);
    }

    public static void count(Context context, int count)
    {
        int units = Prefs.getAdIntersCount(context);
        units += count;
        Log.e(TAG, "" + units);
        Prefs.setAdIntersCount(context, units);

        int limit = Prefs.getAdIntersLimit(context);
        if(limit > 0 && units >= limit)
        {
            //show(context);
        }
    }

    public static void makePossible(Context context)
    {
        Prefs.setAdIntersCount(context, Prefs.getAdIntersLimit(context) + Prefs.getAdIntersCount(context));
    }

    public static boolean isPossible(Context context)
    {
        int units = Prefs.getAdIntersCount(context);
        units += 1;
        Logger.e(TAG, "" + units);
        Prefs.setAdIntersCount(context, units);

        int limit = Prefs.getAdIntersLimit(context);
        if(limit > 0 && units >= limit)
        {
            return true;
        }

        return false;
    }

    public static void possible(Context context)
    {
        if(isPossible(context))
        {
            Ad.fullscreen(context);
        }
    }

    private static void fullscreen(Context context)
    {

    }

    public static void resetCount(Context context)
    {
        if(context == null) return;

        int limit = Prefs.getAdIntersLimit(context);
        int count = Prefs.getAdIntersCount(context);
        if(limit > 0)
            count = count % limit;
        else
            count = 0;
        Prefs.setAdIntersCount(context, count);
    }

    public static AdRequest requestAdmob()
    {
        AdRequest.Builder adRequest = new AdRequest.Builder();
        //adRequest.addTestDevice("B9DC157D59F400D0F283A9180667BC8A");
        return adRequest.build();
    }
}
