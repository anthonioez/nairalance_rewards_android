package com.nairalance.rewards.android.ads;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.RewardsAnalytics;
import com.nairalance.rewards.android.RewardsEvent;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.home.sockets.SocketActionRequest;
import com.nairalance.rewards.android.modules.home.sockets.SocketActionResponse;
import com.miciniti.library.controls.LoadingDialog;
import com.nairalance.rewards.android.utils.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Miciniti on 13/04/2018.
 */

public class AdmobReward // implements RewardedVideoAdListener, SocketActionRequest.SocketActionRequestCallback, SocketActionResponse.SocketActionResponseCallback, Runnable
{
    /*
    private int FAIL_COUNT = 3;
    private final Activity activity;

    private Context context;
    private RewardedVideoAd adRewarded;

    private int     fails = 0;
    private boolean isLoading = false;
    private boolean showOnRequest = false;

    private long    rewardId = 0;
    private String  requestHash = "";

    private SocketActionRequest taskRequest = null;
    private SocketActionResponse taskResponse = null;
    private AdmobRewardListener listener = null;

    private Handler handler = new Handler();

    public AdmobReward(Activity activity, AdmobRewardListener listener)
    {
        this.activity = activity;
        this.listener = listener;
        this.context = activity.getApplicationContext();

        // Use an activity context to get the rewarded video instance.
        adRewarded = MobileAds.getRewardedVideoAdInstance(context);
        adRewarded.setRewardedVideoAdListener(this);

        fails = 0;
        load();
    }

    @Override
    public void run()
    {
        check();
    }

    private void load()
    {
        EventBus.getDefault().post(new AppEvent(RewardsEvent.GOOGLE_AD_CLEARED));

        if(TextUtils.isEmpty(Rewards.rewardAdmobId)) return;

        isLoading = true;
        handler.removeCallbacks(this);

        adRewarded.loadAd(Rewards.rewardAdmobId, new AdRequest.Builder().build());

    }

    public void recheck()
    {
        fails = 0;
        check();
    }

    public void check()
    {
        if(!isLoading && !adRewarded.isLoaded())
        {
            load();
        }
    }

    public boolean show(long reward)
    {
        if (adRewarded.isLoaded())
        {
            this.rewardId = reward;

            if(TextUtils.isEmpty(requestHash))
            {
                showOnRequest = true;
                request();
            }
            else
            {
                showAndClear();
            }

            return true;
        }
        else
        {
            if(fails >= FAIL_COUNT)
            {
                fails = 0;
                check();
            }

            Utils.toast(activity, "No videos ads available, please try again later!");

            return false;
        }
    }

    public void showAndClear()
    {
        EventBus.getDefault().post(new AppEvent(RewardsEvent.GOOGLE_AD_CLEARED));

        adRewarded.show();

        RewardsAnalytics.logEvent(context, "reward_admob", "");

        showOnRequest = false;
    }

    public void resume()
    {
        adRewarded.resume(context);
    }

    public void pause()
    {
        adRewarded.pause(context);
    }

    public void destroy()
    {
        adRewarded.destroy(context);
    }

    @Override
    public void onRewardedVideoAdLoaded()
    {
        isLoading = false;

        handler.removeCallbacks(this);

        Utils.toast(context, "Google video ad available!");

        EventBus.getDefault().post(new AppEvent(RewardsEvent.GOOGLE_AD_LOADED));
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i)
    {
        isLoading = false;

        fails++;
        if(fails < FAIL_COUNT)
        {
            handler.removeCallbacks(this);

            handler.postDelayed(this, 20000);
        }
    }


    @Override
    public void onRewardedVideoAdOpened()
    {
        //send action request
    }

    @Override
    public void onRewardedVideoStarted()
    {

    }

    @Override
    public void onRewardedVideoAdClosed()
    {
        //EventBus.getDefault().post(new AppEvent(RewardsEvent.GOOGLE_AD_CLEARED));

        requestHash = null;

        load();


        if(listener != null) listener.onAdmobRewardClosed();

    }

    @Override
    public void onRewarded(RewardItem reward)
    {
        response();
    }

    @Override
    public void onRewardedVideoAdLeftApplication()
    {

    }

    public void request()
    {
        requestHash = null;

        taskRequest = new SocketActionRequest(activity, null, this);
        taskRequest.start(rewardId, ServerData.watch, Rewards.rewardAdmobId);
    }

    @Override
    public void actionRequestStarted()
    {
        LoadingDialog.show(activity, activity.getString(R.string.reward_request));
    }

    @Override
    public void actionRequestSuccess(SocketActionRequest request, String hash)
    {
        LoadingDialog.dismiss(activity);

        requestHash = hash;

        if(showOnRequest)
        {
            showAndClear();
        }
    }

    @Override
    public void actionRequestError(String error)
    {
        LoadingDialog.dismiss(activity);

        requestHash = null;

        UI.toast(context, error);
    }


    public void response()
    {
        if(TextUtils.isEmpty(requestHash))
        {
            UI.toast(activity, activity.getString(R.string.reward_not_requested));
            return;
        }

        taskResponse = new SocketActionResponse(activity, this);
        taskResponse.start(requestHash, ServerData.watch, Rewards.rewardAdmobId, "");
    }

    @Override
    public void actionResponseStarted()
    {
        //LoadingDialog.show(activity, activity.getString(R.string.reward_claim));
        UI.toast(activity, activity.getString(R.string.reward_claim));
    }

    @Override
    public void actionResponseSuccess(String msg, int points, String action)
    {
        //LoadingDialog.dismiss();

        UI.toast(context, msg); //TODO animation and sound!!

        requestHash = null;
    }

    @Override
    public void actionResponseError(String error)
    {
        //LoadingDialog.dismiss();

        requestHash = null;

        UI.toast(context, error);
    }


    public interface AdmobRewardListener
    {
        void onAdmobRewardClosed();
    }*/
}
