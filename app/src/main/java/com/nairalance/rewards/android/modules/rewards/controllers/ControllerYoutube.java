package com.nairalance.rewards.android.modules.rewards.controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.miciniti.library.Links;
import com.miciniti.library.Utils;
import com.miciniti.library.helpers.DateTime;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.RewardsAnalytics;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.nairalance.rewards.android.modules.home.sockets.SocketActionRequest;
import com.nairalance.rewards.android.modules.home.sockets.SocketActionResponse;
import com.nairalance.rewards.android.modules.rewards.adapters.AdapterActions;
import com.nairalance.rewards.android.modules.rewards.objects.RewardActionItem;
import com.nairalance.rewards.android.modules.rewards.objects.RewardItem;
import com.nairalance.rewards.android.modules.rewards.socket.SocketActions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ControllerYoutube extends ControllerActivity implements AppCompatCallback, YouTubePlayer.OnInitializedListener, RecyclerOnItemListener, AdmobInterst.AdInterstListener, SocketActions.SocketActionsCallback, YouTubePlayer.PlaybackEventListener, YouTubePlayer.PlayerStateChangeListener, SocketActionRequest.SocketActionRequestCallback, SocketActionResponse.SocketActionResponseCallback, View.OnClickListener
{
    public static final String TAG = ControllerYoutube.class.getSimpleName();
    private static final int RECOVERY_REQUEST = 1;

    private YouTubePlayerView youtubeView;
    private LinearLayout layoutHeader;
    private TextView textDesc;
    private TextView textCount;
    private TextView textAction;

    private FrameLayout layoutOverlay;
    private TextView textOverlay;
    private ProgressBar progressOverlay;

    private RecyclerView recyclerView;
    private Button buttonShare;

    private AdapterActions adapter = null;
    private LinearLayoutManager layoutManager = null;

    private AdmobInterst adInterst = null;
    private AppCompatDelegate delegate;

    private List<RewardActionItem> itemList = new ArrayList<>();

    private YouTubePlayer youtubePlayer = null;

    private Timer timer = null;
    private RewardItem reward = null;
    private String requestHash = null;

    private boolean wasSeeked = false;
    private boolean wasPaused = false;

    private boolean hasShort = false;
    private boolean hasFull = false;

    private boolean isLoading  = false;
    private boolean isVideoLoaded = false;
    private boolean isActionsLoaded = false;

    private SocketActions taskLoad = null;
    private SocketActionRequest taskRequest = null;
    private SocketActionResponse taskResponse = null;

    private Handler handler = new Handler();

    private List<String> claimList = new ArrayList<>();
    private String actions = "";

    private int responseBackOff = 0;
    private int shortDuration = -1;

    private HashMap<String, Boolean> countMap = new HashMap<>();

    public ControllerYoutube(Activity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        super.create(savedInstanceState);

        //let's create the delegate, passing the activity at both arguments (Activity, AppCompatCallback)
        delegate = AppCompatDelegate.create(activity, this);

        //we need to call the onCreate() of the AppCompatDelegate
        delegate.onCreate(savedInstanceState);


        reward = new RewardItem();
        if(!reward.fromJSON(activity.getIntent().getStringExtra(Strings.item)))
        {
            UI.toast(activity, "Invalid data!");
            activity.finish();
        }

        activity.setContentView(R.layout.controller_youtube);

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);
        delegate.setSupportActionBar(toolbar);

        ActionBar actionBar = delegate.getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(reward.title);

        youtubeView = activity.findViewById(R.id.youtube_view);

        textCount = activity.findViewById(R.id.textCount);
        textCount.setText("");

        textDesc = activity.findViewById(R.id.textDesc);
        textDesc.setText(reward.desc);

        textAction = activity.findViewById(R.id.textAction);
        textAction.setVisibility(View.GONE);

        adapter = new AdapterActions(activity, itemList, this);

        layoutManager = new LinearLayoutManager(activity);

        layoutHeader = activity.findViewById(R.id.layoutHeader);

        layoutOverlay = activity.findViewById(R.id.layoutOverlay);
        textOverlay = activity.findViewById(R.id.textOverlay);
        progressOverlay = activity.findViewById(R.id.progressOverlay);
        layoutOverlay.setVisibility(View.GONE);

        recyclerView = activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        buttonShare = activity.findViewById(R.id.buttonShare);
        buttonShare.setOnClickListener(this);

        youtubeView.initialize(Rewards.youtubeApiKey, this);

        registerBus();

        Ad.count(activity, 1);

        adInterst = new AdmobInterst(activity, activity.getString(R.string.admob_inter));
        if(Ad.isPossible(activity))
        {
            adInterst.load();
        }
    }

    public void start()
    {

    }

    public void intent(Intent intent)
    {
        if(intent == null) return;

        String sender = intent.getStringExtra(Strings.sender);
        if(sender != null)
        {
            if(sender.equals(Strings.push))
            {
                //processPush(intent);
            }
        }
        else
        {
            //processLink(intent);
        }
    }

    public void pause()
    {
        super.pause();

        //Rewards.rewardAdmob.pause();
    }

    public void resume()
    {
        super.resume();


        if(isVideoLoaded && !isActionsLoaded && itemList.size() == 0)
        {
            load();
        }
        else
        {
            adapter.notifyDataSetChanged();
        }

        if(Ad.isPossible(activity) && adInterst.isFailed())
        {
            adInterst.load();
        }
    }

    public void destroy()
    {
        super.destroy();

        youtubePlayer = null;
        handler.removeCallbacksAndMessages(null);

        unload();
        unresponse();
        unrequest();
        untimer();

        unregisterBus();
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

    public void activityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == RECOVERY_REQUEST)
        {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Rewards.youtubeApiKey, this);
        }
        else
        {
            Ad.possible(activity);
        }
    }

    public void createMenu(Menu menu)
    {
        activity.getMenuInflater().inflate(R.menu.controller_youtube, menu);

    }

    public boolean prepareMenu(Menu menu)
    {
        return false;
    }

    public boolean selectMenu(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home:
                adInterst.showOrFinish(activity, this);
                return true;

            case R.id.action_open_youtube:
                Links.openUrl(activity, reward.link, true);
                break;

            case R.id.action_reload:
                load();
                break;
        }

        return false;
    }

    @Override
    public void onSupportActionModeStarted(ActionMode mode) {
        //let's leave this empty, for now
    }

    @Override
    public void onSupportActionModeFinished(ActionMode mode) {
        // let's leave this empty, for now
    }

    @Nullable
    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback)
    {
        return null;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AppEvent event)
    {
        if(event == null) return;

        if(event.name.equals(Strings.interstitial))
        {
        }
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adp, View childView, int position)
    {
        RewardActionItem item = adapter.getItem(position);
        if(item == null) return;

        adapter.selectedPosition = position;
    }

    @Override
    public void onMenuClick(RecyclerView.Adapter adp, View childView, int position)
    {
        RewardActionItem item = adapter.getItem(position);
        if(item == null) return;
    }

    @Override
    public boolean onItemLongClick(RecyclerView.Adapter adapter, View view, int position)
    {
        return false;
    }

    private void updateCounter()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if(youtubePlayer != null)
                {
                    long position = youtubePlayer.getCurrentTimeMillis();
                    String posText = DateTime.formatDuration(position);

                    textCount.setVisibility(View.VISIBLE);
                    textCount.setText(posText);

                    countMap.put(posText, true);

                    if(shortDuration != -1 && position >= (shortDuration * 1000))
                    {
                        if(hasShort && canRespond("short"))
                        {
                            if(responseBackOff <= 0)
                            {
                                response("short");
                            }
                            else
                            {
                                responseBackOff--;
                            }
                        }
                    }
                }
            }
        });
    }

    private boolean canRespond(String action)
    {
        return (requestHash != null && !wasSeeked && !isLoading && !claimList.contains(action));
    }

    private void timer()
    {
        timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                updateCounter();
            }
        }, 0, 500);
    }

    private void untimer()
    {
        if(timer != null)
        {
            Utils.stopTimer(timer);
            timer = null;
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored)
    {
        player.setPlayerStateChangeListener(this);
        player.setPlaybackEventListener(this);

        if (!wasRestored && !TextUtils.isEmpty(reward.data))
        {
            player.cueVideo(reward.data);
        }

        this.youtubePlayer = player;
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason)
    {
        if (errorReason.isUserRecoverableError())
        {
            errorReason.getErrorDialog(activity, RECOVERY_REQUEST).show();
        }
        else
        {
            String error = errorReason.toString();

            UI.toast(activity, error);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider()
    {
        return youtubeView;
    }


    @Override
    public void onPlaying()
    {
        // Called when playback starts, either due to user action or call to play().

        Log.d(TAG, "video playing");

        //start counter
        if(wasPaused || timer == null)
        {
            timer();
            wasPaused = false;
        }
    }

    @Override
    public void onPaused()
    {
        // Called when playback is paused, either due to user action or call to pause().
        //showMessage("Paused");

        Log.d(TAG, "video paused");

        wasPaused = true;

        untimer();
        //stop counter
    }

    @Override
    public void onStopped()
    {
        // Called when playback stops for a reason other than being paused.
        //showMessage("Stopped");

        Log.d(TAG, "video stopped");

        //cancel counter
        untimer();

    }

    @Override
    public void onBuffering(boolean b)
    {
        // Called when buffering starts or ends.

        Log.d(TAG, "video buffering");
    }

    @Override
    public void onSeekTo(int i)
    {
        // Called when a jump in playback position occurs, either
        // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()

        Log.d(TAG, "video seek");

        wasSeeked = true;

        if(timer != null)
        {
            UI.toast(activity, "Seek not allowed, reward points will not be awarded!");
        }

        //cancel counter
        untimer();

        updateCounter();
    }

    @Override
    public void onLoading()
    {
        // Called when the player is loading a video
        // At this point, it's not ready to accept commands affecting playback such as play() or pause()

        Log.d(TAG, "video loading");
    }

    @Override
    public void onLoaded(String s)
    {
        // Called when a video is done loading.
        // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.

        Log.d(TAG, "video loaded");

        RewardsAnalytics.logEvent(activity, "youtube_loaded", reward.data);

        countMap.clear();

        if(!isActionsLoaded && !isLoading)
        {
            load();
        }
    }

    @Override
    public void onAdStarted()
    {
        // Called when playback of an advertisement starts.

        Log.d(TAG, "ad started");
    }

    @Override
    public void onVideoStarted()
    {
        // Called when playback of the video starts.

        Log.d(TAG, "video started");

        request();
    }

    @Override
    public void onVideoEnded()
    {
        // Called when the video reaches its end.

        Log.d(TAG, "video ended");

        untimer();

        int duration = (youtubePlayer.getDurationMillis() / 1000);

        if(hasFull && canRespond("full") && countMap.size() + 10 > duration)
        {
            response("full");
        }
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason)
    {
        // Called when an error occurs.

        Log.d(TAG, "video errored");

        //cancel counter
        untimer();
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

        taskLoad = new SocketActions(activity, this);
        taskLoad.start(reward.id);
    }

    @Override
    public void actionStarted()
    {
        isLoading = true;

        textOverlay.setText("Loading rewards...");
        layoutOverlay.setVisibility(View.VISIBLE);
        textAction.setVisibility(View.GONE);

        youtubeView.setEnabled(false);
    }

    @Override
    public void actionSuccess(List<RewardActionItem> list)
    {
        itemList.clear();

        hasShort = false;
        hasFull = false;

        for(RewardActionItem item : list)
        {
            if(item.claimed)
            {
                claimList.add(item.action); //continue;
            }

            if(item.action.equals("short"))
            {
                shortDuration = Utils.getInt(item.data);
                hasShort = true;
            }
            else if(item.action.equals("full"))
            {
                hasFull = true;
            }

            actions += (actions.trim().length() == 0 ? "" : ",");
            actions += item.action;

            itemList.add(item);
        }

        textAction.setText(itemList.size() > 0 ? "Available rewards:" : "No rewards available.");
        adapter.notifyDataSetChanged();

        isLoading = false;
        isActionsLoaded = true;

        layoutOverlay.setVisibility(View.GONE);
        textAction.setVisibility(View.VISIBLE);
    }

    @Override
    public void actionError(String error)
    {
        isLoading = false;

        layoutOverlay.setVisibility(View.GONE);
        textAction.setVisibility(View.GONE);

        UI.toast(activity, error);
    }

    public void unrequest()
    {
        if (taskRequest != null)
        {
            taskRequest.setCallback(null);
            taskRequest = null;
        }
    }

    public void request()
    {
        unrequest();

        if(claimList.size() == itemList.size())
        {
            return;
        }

        requestHash = null;
        isLoading = true;

        taskRequest = new SocketActionRequest(activity, null,this);
        taskRequest.start(reward.id, actions, reward.data);
    }

    @Override
    public void actionRequestStarted()
    {
        isLoading = true;

        textOverlay.setText("Sending reward request...");
        layoutOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void actionRequestSuccess(SocketActionRequest request, String hash)
    {
        requestHash = hash;

        layoutOverlay.setVisibility(View.GONE);

        isLoading = false;
    }

    @Override
    public void actionRequestError(String error)
    {
        requestHash = null;

        layoutOverlay.setVisibility(View.GONE);

        UI.toast(activity, error);

        isLoading = false;
    }

    public void unresponse()
    {
        if (taskResponse != null)
        {
            taskResponse.setCallback(null);
            taskResponse = null;
        }
    }

    public void response(String action)
    {
        if(TextUtils.isEmpty(requestHash))
        {
            UI.toast(activity, activity.getString(R.string.reward_not_requested));
            return;
        }

        isLoading = true;

        taskResponse = new SocketActionResponse(activity, this);
        taskResponse.start(requestHash, action, reward.data, "");
    }

    @Override
    public void actionResponseStarted()
    {
        isLoading = true;

        textOverlay.setText(R.string.reward_claim);
        layoutOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void actionResponseSuccess(String msg, int points, String action)
    {
        layoutOverlay.setVisibility(View.GONE);

        claimList.add(action);

        UI.toast(activity, msg); //TODO animation and sound!!

        isLoading = false;
    }

    @Override
    public void actionResponseError(String error)
    {
        layoutOverlay.setVisibility(View.GONE);

        UI.toast(activity, error);

        isLoading = false;

        responseBackOff = 15;   //retry 10 seconds
    }

    @Override
    public void onClick(View v)
    {
        String title = String.format("%s", reward.title);   //Youtube Video:
        String message = String.format("%s %s", reward.desc, reward.link);
        Links.shareText(activity, title, message, "Share video...");
    }
}

