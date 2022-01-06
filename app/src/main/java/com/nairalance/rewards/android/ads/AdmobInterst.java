package com.nairalance.rewards.android.ads;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.ads.AdListener;
// import com.google.android.gms.ads.InterstitialAd;
import com.miciniti.library.Logger;

import com.nairalance.rewards.android.RewardsAnalytics;

public class AdmobInterst
{
    private static String TAG = AdmobInterst.class.getSimpleName();

    private final Context context;
    private final String admobId;
    // private InterstitialAd ad;
    private AdInterstListener listener;
    private boolean loading = false;
    private boolean loaded = false;
    private boolean failed = false;

    public AdmobInterst(Context context, String admobId)
    {
        this.context = context;
        this.admobId = admobId;
        this.loaded = false;
    }

    public void load()
    {
        loading = true;

        /*
        ad = new InterstitialAd(context);
        ad.setAdUnitId(admobId);
        ad.setAdListener(new AdListener()
        {
            @Override
            public void onAdFailedToLoad(int i)
            {
                closeIntersAdmob(ad);
                failed = true;
                loading = false;

                if(listener == null)
                {
                    //TODO load ad again??
                }
                else
                {
                    listener.onAdInterstFailed();
                }
            }

            @Override
            public void onAdClosed()
            {
                closeIntersAdmob(ad);

                failed = false;
                loading = false;
                if(listener != null) listener.onAdInterstClosed();
            }

            @Override
            public void onAdLoaded()
            {
                loading = false;
                loaded = true;
                failed = false;
                Logger.d(TAG, "adinterst loaded");
            }
        });

        ad.loadAd(Ad.requestAdmob());
        */
    }

    public void show(AdInterstListener listener)
    {
        this.listener = listener;

        loaded = false;

        /*
        if(ad != null && ad.isLoaded())
        {
            ad.show();

            RewardsAnalytics.logEvent(context, "inters_admob", "");

            Ad.resetCount(context);
        }
        */
    }

    public void close()
    {
        this.listener = null;
        // closeIntersAdmob(ad);
    }

    /*
    public static void closeIntersAdmob(InterstitialAd ad)
    {
        if (ad != null)
        {
            ad.setAdListener(null);
        }
        //view = null;
    }*/

    public boolean isLoading()
    {
        return loading;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    public void showOrFinish(Activity activity, AdmobInterst.AdInterstListener listener)
    {
        if(isLoaded())
        {
            show(listener);
        }
        else
        {
            activity.finish();
        }
    }

    public boolean isFailed()
    {
        return failed;
    }

    public interface AdInterstListener
    {
        void onAdInterstClosed();
        void onAdInterstFailed();
    }
}
