package com.nairalance.rewards.android.ads;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.miciniti.library.Utils;
import com.miciniti.library.helpers.UI;
import com.nairalance.rewards.android.Prefs;

public class AdmobBanner
{
    private static final String TAG = AdmobBanner.class.getSimpleName();

    public static final int SLOT_HOME       = 0b0000001;
    public static final int SLOT_REWARDS    = 0b0000010;
    public static final int SLOT_EARNINGS   = 0b0000100;
    public static final int SLOT_RANKINGS   = 0b0001000;
    public static final int SLOT_CASHOUT    = 0b0010000;
    public static final int SLOT_PAYOUTS    = 0b0100000;

    private final Context context;

    private RelativeLayout layoutBox;
    private AdView admobView;

    private static boolean animated = true;

    private int adPaddingTop = 0;
    private int adPaddingBottom = 0;
    private AdState.State adState;

    private boolean isReady = false;
    public String admobId;

    public AdmobBanner(Context context, String admobId, RelativeLayout layout, int paddingTop, int paddingBottom)
    {
        this.context = context;
        this.admobId = admobId;

        this.adPaddingTop = paddingTop;
        this.adPaddingBottom = paddingBottom;

        this.adState = AdState.State.FAILED;

        if (layout == null)
        {
            return;
        }

        layoutBox = layout;

        isReady = false;
    }

    public void prepare()
    {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        admobView = new AdView(context);
        admobView.setAdSize(AdSize.BANNER); //.SMART_BANNER);
        admobView.setAdUnitId(admobId);
        admobView.setLayoutParams(params);
        layoutBox.addView(admobView);

        admobView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded()
            {
                show();

                adState = AdState.State.LOADED;
            }

            // @Override
            public void onAdFailedToLoad(int i)
            {
                adState = AdState.State.FAILED;

                Ad.count(context, 1);
            }
        });

        isReady = true;
    }

    public void load()
    {
        if(!isReady) return;

        adState = AdState.State.LOADING;
        if (admobView != null)
        {
            admobView.loadAd(Ad.requestAdmob());
        }
    }

    private void check()
    {
        if(!isReady) return;

        if (adState == AdState.State.LOADED)
        {
            show();
        }
        else if (adState == AdState.State.FAILED)
        {
            load();
        }
    }

    public void resume(boolean chk)
    {
        if(!isReady) return;

        if (admobView != null)
        {
            admobView.resume();
        }
        if (chk)
        {
            check();
        }
    }

    public void pause()
    {
        if(!isReady) return;

        if (admobView != null)
        {
            admobView.pause();
        }
    }

    public void close()
    {
        if(!isReady) return;

        if (admobView != null)
        {
            admobView.destroy();
        }
    }

    private void show()
    {
        boolean anim = (layoutBox.getVisibility() != View.VISIBLE);

        layoutBox.setPadding(0, Utils.getPixels(context, adPaddingTop), 0, Utils.getPixels(context, adPaddingBottom));

        if (anim && animated)
        {
            UI.expand(layoutBox, 500);
        }
        else
        {
            layoutBox.setVisibility(View.VISIBLE);
        }
    }

    private void hide()
    {
        UI.collapse(layoutBox, 500);
    }

    public static void setAnimated(boolean b)
    {
        animated = b;
    }

    public static boolean hasSlot(Context context, int slot)
    {
        return (Prefs.getAdBannerSlots(context) & slot) > 0;
    }
}