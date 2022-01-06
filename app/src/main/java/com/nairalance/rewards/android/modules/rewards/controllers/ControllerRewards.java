package com.nairalance.rewards.android.modules.rewards.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.activities.ActivityYoutube;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobBanner;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.miciniti.library.listeners.RecyclerScrollListener;
import com.nairalance.rewards.android.modules.rewards.Reward;
import com.nairalance.rewards.android.modules.rewards.adapters.AdapterRewards;
import com.nairalance.rewards.android.modules.rewards.objects.RewardItem;
import com.nairalance.rewards.android.modules.rewards.socket.SocketRewards;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ControllerRewards extends ControllerActivity implements SocketRewards.SocketRewardsCallback, RecyclerOnItemListener, AdmobInterst.AdInterstListener, SwipeRefreshLayout.OnRefreshListener
{
    public static final String TAG = ControllerRewards.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private String mType = "";
    private int mIndex = 0;
    private boolean isLoaded  = false;
    private boolean isLoading  = false;

    private Handler handler = new Handler();

    private AdapterRewards adapter = null;
    private RewardsScrollListener scrollListener = null;
    private LinearLayoutManager layoutManager = null;

    private MenuItem menuItemReload;
    private ArrayList<RewardItem> itemList = new ArrayList<>();

    private AdmobBanner adBanner = null;
    private AdmobInterst adInterst = null;
    private SocketRewards taskLoad;
    private SwipeRefreshLayout swipeLayout;


    public ControllerRewards(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        super.create(savedInstanceState);

        activity.setContentView(R.layout.controller_rewards);

        Intent intent = activity.getIntent();
        String title = intent.getStringExtra("title");
        String type = intent.getStringExtra("type");

        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(type))
        {
            UI.toast(activity, "Invalid parameters!");
            activity.finish();
            return;
        }

        mType = type;

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(String.format("%s Rewards", title));

        adapter = new AdapterRewards(activity, itemList, this);

        layoutManager = new LinearLayoutManager(activity);

        scrollListener = new RewardsScrollListener(layoutManager, Rewards.SCROLL_THRESHOLD, Rewards.SCROLL_DELAY);

        swipeLayout = (SwipeRefreshLayout) activity.findViewById(R.id.swipeLayout);
        swipeLayout.setDistanceToTriggerSync(100);


        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setHasFixedSize(true);

        scrollListener.setReady();
        scrollListener.setDone(adapter.getItemCount() < Rewards.PAGE_SIZE);

        swipeLayout.setEnabled(true);
        swipeLayout.setOnRefreshListener(this);


        registerBus();

        Ad.count(activity, 1);

        adBanner = new AdmobBanner(activity.getApplicationContext(), activity.getString(R.string.admob_banner), (RelativeLayout)activity.findViewById(R.id.layoutAd), 0, 0);
        if(AdmobBanner.hasSlot(activity, AdmobBanner.SLOT_REWARDS))
        {
            adBanner.prepare();
            adBanner.load();
        }

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

        adBanner.pause();
    }

    public void resume()
    {
        super.resume();

        if(!isLoaded && itemList.size() == 0)
        {
            load();
        }
        else
        {
            adapter.notifyDataSetChanged();
        }

        adBanner.resume(true);

        if(Ad.isPossible(activity) && adInterst.isFailed())
        {
            adInterst.load();
        }
    }


    public void destroy()
    {
        super.destroy();

        adBanner.close();

        unload();

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
        switch (requestCode)
        {
            case Rewards.REQUEST_BROWSER:
            case Rewards.REQUEST_CONTENT_LIST:
            case Rewards.REQUEST_CONTENT_VIEW:
            default:
                Ad.possible(activity);
                break;
        }
    }

    public void createMenu(Menu menu)
    {
        activity.getMenuInflater().inflate(R.menu.controller_rewards, menu);
    }

    public boolean prepareMenu(Menu menu)
    {
        menuItemReload = menu.findItem(R.id.action_reload);

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


            case R.id.action_reload:
                reload();
                break;
        }

        return false;
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
        RewardItem item = adapter.getItem(position);
        if(item == null) return;

        if(item.type.equals("youtube"))
        {
            Intent intent = new Intent(activity, ActivityYoutube.class);
            intent.putExtra(Strings.item, item.toJSON().toString());
            activity.startActivity(intent);
        }
    }

    @Override
    public void onMenuClick(RecyclerView.Adapter adp, View childView, int position)
    {
        RewardItem item = adapter.getItem(position);
        if(item == null) return;

        adapter.notifyItemChanged(position);

        updateMenu();
    }

    @Override
    public boolean onItemLongClick(RecyclerView.Adapter adapter, View view, int position)
    {
        return false;
    }

    public void onNext(int actual)
    {
        if(isLoading)
            return;

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                load();
            }
        }, 500);
    }

    private void updateMenu()
    {
        //if(menuItemReload != null) menuItemReload.setEnabled(Setup.interests.size() > 0);
    }

    private void unload()
    {
        if (taskLoad != null) {
            taskLoad.setCallback(null);
            taskLoad = null;
        }
    }

    private void load()
    {
        unload();

        taskLoad = new SocketRewards(activity, this);
        taskLoad.start(mType, mIndex, Reward.PAGE_SIZE);
    }

    private void reload()
    {
        mIndex = 0;

        load();
    }

    @Override
    public void rewardStarted()
    {
        isLoading = true;

        progressBar.setVisibility(swipeLayout.isRefreshing() ? View.GONE : View.VISIBLE);

        updateMenu();
    }

    @Override
    public void rewardSuccess(List<RewardItem> items, int index)
    {
        swipeLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        if (mIndex == 0)
        {
            itemList.clear();
        }

        if(items.size() > 0)
        {
            itemList.addAll(items);
        }

        adapter.notifyDataSetChanged();

        if (items.size() > 0 && mIndex > 0)
        {
            recyclerView.smoothScrollBy(0, 100);
        }

        scrollListener.setDone(items.size() < Reward.PAGE_SIZE);
        scrollListener.setReady();
        mIndex = index + items.size();

        isLoaded = true;
        isLoading = false;
        updateMenu();
    }

    @Override
    public void rewardError(String error)
    {
        isLoading = false;

        swipeLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);

        scrollListener.setReady();

        UI.toast(activity, error);

        updateMenu();
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

    public class RewardsScrollListener extends RecyclerScrollListener
    {
        public RewardsScrollListener(LinearLayoutManager lm, int visibleThreshold, int delay)
        {
            super(lm, visibleThreshold, delay);
        }

        @Override
        public void onMore(int actual)
        {
            onNext(actual);
        }

        @Override
        public void onFirst(boolean yes)
        {
            int scrollY = 0;
            View c = recyclerView.getChildAt(0);
            if(c != null)
            {
                scrollY = c.getTop();
            }
        }
    }
}

