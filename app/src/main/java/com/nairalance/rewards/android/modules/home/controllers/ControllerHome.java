package com.nairalance.rewards.android.modules.home.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.miciniti.library.helpers.DateTime;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.RewardsAnalytics;
import com.nairalance.rewards.android.RewardsEvent;
import com.nairalance.rewards.android.activities.ActivityAbout;
import com.nairalance.rewards.android.activities.ActivityEarnings;
import com.nairalance.rewards.android.activities.ActivityProfile;
import com.nairalance.rewards.android.activities.ActivityRankings;
import com.nairalance.rewards.android.activities.ActivityRewards;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobBanner;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.nairalance.rewards.android.ads.AdmobReward;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.nairalance.rewards.android.helpers.Image;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.home.adapters.AdapterHome;
import com.nairalance.rewards.android.modules.home.objects.HomeItem;
import com.nairalance.rewards.android.modules.home.sockets.SocketActionRequest;
import com.nairalance.rewards.android.modules.home.sockets.SocketActionResponse;
import com.nairalance.rewards.android.modules.home.sockets.SocketHome;
import com.nairalance.rewards.android.modules.rewards.objects.RewardActionItem;
import com.nairalance.rewards.android.modules.rewards.objects.RewardTypeItem;
import com.miciniti.library.controls.LoadingDialog;
import com.nairalance.rewards.android.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ControllerHome extends ControllerActivity implements View.OnClickListener, RecyclerOnItemListener, SwipeRefreshLayout.OnRefreshListener, SocketHome.SocketHomeCallback, AdmobInterst.AdInterstListener, SocketActionRequest.SocketActionRequestCallback, SocketActionResponse.SocketActionResponseCallback, OnInitializationCompleteListener {
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 787;
    private static final int REQUEST_PROFILE = 32;

    private LinearLayout layoutMain;

    private ImageButton buttonProfile;
    private ImageButton buttonSettings;

    private SwipeRefreshLayout swipeLayout;
    private LinearLayout layoutCover;
    private ImageView imageCover;
    private TextView textUser;

    private LinearLayout layoutRanking;
    private LinearLayout layoutEarnings;
    private TextView textBalance;
    private TextView textBalanceText;
    private TextView textRanking;

    private Button buttonEarnings;
    private Button buttonRankings;

    private RecyclerView recyclerView;

    private ProgressBar progressBar;

    private AdmobInterst adInterst = null;
    private AdmobBanner adBanner = null;


    private AdapterHome adapter;
    private Handler handler = new Handler();

    private boolean isLoading = false;
    private boolean firstTime;
    private boolean profileRefresh = false;
    private String requestHash = null;

    private SocketHome taskLoad = null;
    private SocketActionRequest taskRequest = null;
    private SocketActionResponse taskResponse = null;

    public ControllerHome(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        Prefs.setRunCount(activity, Prefs.getRunCount(activity) + 1);

        MobileAds.initialize(activity.getApplicationContext(), this);

        // Rewards.rewardAdmob = new AdmobReward(activity, this);
        //Rewards.rewardAudience = new AudienceReward(activity);

        activity.setContentView(R.layout.controller_home);

        layoutMain = activity.findViewById(R.id.layoutMain);
        UI.overrideFonts(activity, layoutMain, Rewards.appFont);

        buttonProfile = activity.findViewById(R.id.buttonProfile);
        buttonSettings = activity.findViewById(R.id.buttonSettings);
        buttonProfile.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);

        swipeLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipeLayout);
        swipeLayout.setDistanceToTriggerSync(100);

        layoutCover = activity.findViewById(R.id.layoutCover);
        layoutCover.setOnClickListener(this);

        imageCover = activity.findViewById(R.id.imageCover);

        textUser = activity.findViewById(R.id.textUser);
        textUser.setTypeface(Rewards.appFontBold);

        layoutEarnings = activity.findViewById(R.id.layoutEarnings);
        layoutEarnings.setOnClickListener(this);
        textBalance = activity.findViewById(R.id.textBalance);
        textBalanceText = activity.findViewById(R.id.textBalanceText);

        layoutRanking = activity.findViewById(R.id.layoutRanking);
        layoutRanking.setOnClickListener(this);
        textRanking = activity.findViewById(R.id.textRanking);
        textBalance.setTypeface(Rewards.appFontBold);
        textRanking.setTypeface(Rewards.appFontBold);

        TextView textBalanceText = activity.findViewById(R.id.textBalanceText);
        textBalanceText.setTypeface(Rewards.appFontLight);

        TextView textRankingText = activity.findViewById(R.id.textRankingText);
        textRankingText.setTypeface(Rewards.appFontLight);

        adapter = new AdapterHome(activity, this);

        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView = (RecyclerView) activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        swipeLayout.setEnabled(true);
        swipeLayout.setOnRefreshListener(this);

        buttonEarnings = activity.findViewById(R.id.buttonEarnings);
        buttonRankings = activity.findViewById(R.id.buttonRankings);

        buttonEarnings.setOnClickListener(this);
        buttonRankings.setOnClickListener(this);

        checkPlayServices();

        //Appirater.appLaunched(activity);

        Prefs.setRunCount(activity, Prefs.getRunCount(activity) + 1);

        registerBus();

        if(Rewards.feedback != 0 && Rewards.feedback > Prefs.getLastFeedback(activity) && Prefs.getRunCount(activity) > 5 )
        {
            Rewards.askFeedback(activity);
        }

        Rewards.registerForPush(activity);

        RewardsAnalytics.logEvent(activity, "launch", "");

        intent(activity.getIntent());

        Ad.count(activity, 1);

        //adBanner = new AdmobBanner(activity.getApplicationContext(), activity.getString(R.string.admob_banner), (RelativeLayout)activity.findViewById(R.id.layoutAd), 0, 15);

        adBanner = new AdmobBanner(activity.getApplicationContext(), activity.getString(R.string.admob_banner), (RelativeLayout)activity.findViewById(R.id.layoutAd), 15, 0);
        if(AdmobBanner.hasSlot(activity, AdmobBanner.SLOT_HOME))
        {
            adBanner.prepare();
        }

        adInterst = new AdmobInterst(activity.getApplicationContext(), activity.getString(R.string.admob_inter));
        if(Ad.isPossible(activity))
        {
            adInterst.load();
        }

        firstTime = true;
    }

    public void intent(Intent intent)
    {
        if(intent == null) return;

        Rewards.processIntent(activity, intent);
    }

    public boolean backPressed()
    {
        if(adInterst.isLoaded())
        {
            adInterst.show(this);
            return true;
        }

        return false;
    }

    @Override
    public void resume()
    {
        showProfile();

        if(firstTime)
        {
            adBanner.load();

            firstTime = false;

            reload();
        }
        else
        {
            adBanner.resume(true);

        }

        // Rewards.rewardAdmob.check();
        //Rewards.rewardAudience.check();
    }

    @Override
    public void pause()
    {
        adBanner.pause();
    }

    @Override
    public void destroy()
    {
        unload();

        adBanner.close();
    }

    @Override
    public void activityResult(int requestCode, int resultCode, Intent data)
    {
        super.activityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_PROFILE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                profileRefresh = true;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AppEvent event)
    {
        if(event.name.equals(RewardsEvent.GOOGLE_AD_LOADED))
        {
            adapter.setCount("admob", 1);
        }
        else if(event.name.equals(RewardsEvent.GOOGLE_AD_CLEARED))
        {
            adapter.setCount("admob", 0);
        }

        else if(event.name.equals(RewardsEvent.FACEBOOK_AD_LOADED))
        {
            adapter.setCount("audience", 1);
        }
        else if(event.name.equals(RewardsEvent.FACEBOOK_AD_CLEARED))
        {
            adapter.setCount("audience", 0);
        }

        else if(event.name.equals(RewardsEvent.PROFILE_CHANGED))
        {
            showProfile();
        }
        else if(event.name.equals(RewardsEvent.REWARDED_POINTS))
        {
            showProfile();
        }
    }

    @Override
    public void onClick(View view)
    {

        if(view == buttonEarnings || view == layoutEarnings)
        {
            Intent intent = new Intent(activity, ActivityEarnings.class);
            activity.startActivity(intent);
        }
        else if(view == buttonRankings || view == layoutRanking)
        {
            Intent intent = new Intent(activity, ActivityRankings.class);
            activity.startActivity(intent);
        }
        else if(view == buttonProfile || view == layoutCover)
        {
            Intent intent = new Intent(activity, ActivityProfile.class);
            activity.startActivityForResult(intent, REQUEST_PROFILE);
        }
        else if(view == buttonSettings)
        {
            Intent intent = new Intent(activity, ActivityAbout.class);
            activity.startActivity(intent);
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adp, View childView, int position)
    {
        RewardTypeItem item = adapter.getItem(position);
        if(item == null) return;

        if(item.type.equals("admob"))
        {
            // Rewards.rewardAdmob.show(item.reward);
        }
        else if(item.type.equals("audience"))
        {
            //Rewards.rewardAudience.show(item.reward);
        }
        else if(item.type.equals("points"))
        {
            request(item, "daily");
        }
        else if(item.type.equals("invite"))
        {
            shareInvite(item);
        }
        else if(item.type.equals("youtube"))
        {
            Intent intent = new Intent(activity, ActivityRewards.class);
            intent.putExtra(ServerData.id,       item.id);
            intent.putExtra(ServerData.type,     item.type);
            intent.putExtra(ServerData.title,    item.title);
            activity.startActivity(intent);
            return;
        }

        Ad.count(activity, 1);
    }

    @Override
    public void onMenuClick(RecyclerView.Adapter adp, View childView, int position)
    {
        RewardTypeItem item = adapter.getItem(position);
        if(item == null) return;

        adapter.notifyItemChanged(position);

        //updateMenu();
    }

    @Override
    public boolean onItemLongClick(RecyclerView.Adapter adapter, View view, int position)
    {
        return false;
    }

    @Override
    public void onRefresh()
    {
        if (isLoading)
        {
            swipeLayout.setRefreshing(false);
            return;
        }

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                reload();
            }
        }, 500);
    }

    // @Override
    public void onAdmobRewardClosed()
    {
        if(adInterst.isLoaded())
        {
            adInterst.show(null);
        }
        else if((adInterst.isFailed() || !adInterst.isLoading()) && Ad.isPossible(activity))
        {
            adInterst.load();
        }
    }

    @Override
    public void onAdInterstClosed()
    {
        if(activity != null) activity.finish();
    }

    @Override
    public void onAdInterstFailed()
    {
        if(activity != null) activity.finish();
    }

    private boolean checkPlayServices()
    {
        GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
        int resultCode = googleApi.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (googleApi.isUserResolvableError(resultCode))
            {
                googleApi.getErrorDialog(activity, resultCode, REQUEST_GOOGLE_PLAY_SERVICES).show();
            }
            else
            {
                //Logger.i(TAG, "This device is not supported.");
                //finish();
            }
            return false;
        }
        return true;
    }

    private void shareInvite(RewardTypeItem item)
    {
        String text = String.format(item.data, Prefs.getUsername(activity));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        activity.startActivity(Intent.createChooser(intent, "Invite your friends to earn Rewards!"));
    }

    private void showProfile()
    {
        textRanking.setText(Utils.formatRanking(Prefs.getRanking(activity)));
        textBalance.setText(Utils.formatPoints(Prefs.getRewards(activity)));
        textBalanceText.setText( R.string.points_caps );

        textUser.setText(Prefs.getUsername(activity));

        String url = Prefs.getThumbUrl(activity);
        if(!TextUtils.isEmpty(url))
        {
            Image.loadFull(activity, url, 0, imageCover, profileRefresh, null);
            profileRefresh = false;
        }
        else
        {
            imageCover.setImageBitmap(null);
        }
    }

    private void reload()
    {
        load();
    }

    private void unload()
    {
        if (taskLoad != null)
        {
            taskLoad.setCallback(null);
            taskLoad = null;
        }
    }

    private void load()
    {
        unload();

        taskLoad = new SocketHome(activity, this);
        taskLoad.start();
    }

    @Override
    public void homeStarted()
    {
        isLoading = true;
        progressBar.setVisibility(swipeLayout.isRefreshing() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void homeSuccess(HomeItem data)
    {
        swipeLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        showProfile();

        adapter.updateList(data.items);

        // Rewards.rewardAdmob.recheck();
        //Rewards.rewardAudience.check();

        isLoading = false;
    }

    @Override
    public void homeError(String error)
    {
        swipeLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        isLoading = false;

        UI.toast(activity, error);
    }

    public void request(RewardTypeItem item, String action)
    {
        requestHash = null;

        RewardActionItem actionItem = new RewardActionItem();
        actionItem.action = action;
        actionItem.data = item.data;
        actionItem.info = DateTime.getDateTimeFormat(System.currentTimeMillis());

        taskRequest = new SocketActionRequest(activity,  actionItem, this);
        taskRequest.start(item.reward, action, item.data);
    }

    @Override
    public void actionRequestStarted()
    {
        LoadingDialog.show(activity, activity.getString(R.string.reward_request));
    }

    @Override
    public void actionRequestSuccess(final SocketActionRequest request, final String hash)
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                LoadingDialog.dismiss(activity);

                requestHash = hash;

                RewardActionItem actionItem = (RewardActionItem)request.getData();
                if(actionItem != null)
                {
                    response(actionItem.action, actionItem.data, actionItem.info);
                }
            }
        }, 1500);
    }

    @Override
    public void actionRequestError(final String error)
    {
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                LoadingDialog.dismiss(activity);

                requestHash = null;

                UI.toast(activity, error);
            }
        }, 1500);
    }


    public void response(String action, String data, String info)
    {
        if(TextUtils.isEmpty(requestHash))
        {
            UI.toast(activity, activity.getString(R.string.reward_not_requested));
            return;
        }

        taskResponse = new SocketActionResponse(activity, this);
        taskResponse.start(requestHash, action, data, info);
    }

    @Override
    public void actionResponseStarted()
    {
        LoadingDialog.show(activity, activity.getString(R.string.reward_claim));
    }

    @Override
    public void actionResponseSuccess(final String msg, int points, String action)
    {
        requestHash = null;

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                LoadingDialog.dismiss(activity);

                UI.toast(activity, msg); //TODO animation and sound!!
            }
        }, 1500);
    }

    @Override
    public void actionResponseError(final String error)
    {
        requestHash = null;

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                LoadingDialog.dismiss(activity);

                UI.toast(activity, error);

            }
        }, 1500);
    }

    @Override
    public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

    }
}
