package com.nairalance.rewards.android;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class RewardsAnalytics
{
    public static void logEvent(Context context, String type, String data)
    {
        // Obtain the FirebaseAnalytics instance.
        try
        {
            Bundle bundle = new Bundle();
            bundle.putString("hash",    Prefs.getApiHash(context));
            bundle.putLong  ("id",      Prefs.getId(context));
            bundle.putString("data",    data);
            FirebaseAnalytics.getInstance(context).logEvent(type, bundle);

            /*
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, data);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            */
        }
        catch (Exception e)
        {

        }
    }
}
