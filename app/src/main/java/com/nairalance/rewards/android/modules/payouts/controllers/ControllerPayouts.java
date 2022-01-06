package com.nairalance.rewards.android.modules.payouts.controllers;

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
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobBanner;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.miciniti.library.listeners.RecyclerScrollListener;
import com.nairalance.rewards.android.modules.payouts.Payout;
import com.nairalance.rewards.android.modules.payouts.adapters.AdapterPayoutList;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutItem;
import com.nairalance.rewards.android.modules.payouts.socket.SocketPayoutCancel;
import com.nairalance.rewards.android.modules.payouts.socket.SocketPayoutList;
import com.miciniti.library.controls.LoadingDialog;
import com.nairalance.rewards.android.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ControllerPayouts extends ControllerActivity implements SocketPayoutList.SocketPayoutsCallback, SwipeRefreshLayout.OnRefreshListener, RecyclerOnItemListener, AdmobInterst.AdInterstListener, SocketPayoutCancel.SocketPayoutCancelCallback
{
    public static final String TAG = ControllerPayouts.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private int mIndex = 0;

    private boolean isLoaded  = false;
    private boolean isLoading  = false;

    private Handler handler = new Handler();

    private AdapterPayoutList adapter = null;
    private PayoutsScrollListener scrollListener = null;
    private LinearLayoutManager layoutManager = null;

    private MenuItem menuItemReload;
    private List<PayoutItem> itemList = new ArrayList<>();

    private AdmobInterst adInterst = null;

    private SocketPayoutList taskLoad = null;
    private SocketPayoutCancel taskCancel = null;

    private AdmobBanner adBanner = null;

    public ControllerPayouts(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        super.create(savedInstanceState);

        activity.setContentView(R.layout.controller_payouts);

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(R.string.cashout_history);

        adapter = new AdapterPayoutList(activity, itemList, this);

        layoutManager = new LinearLayoutManager(activity);

        scrollListener = new PayoutsScrollListener(layoutManager, Rewards.SCROLL_THRESHOLD, Rewards.SCROLL_DELAY);

        progressBar = activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        recyclerView = (RecyclerView) activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setHasFixedSize(true);

        scrollListener.setReady();
        scrollListener.setDone(adapter.getItemCount() < Payout.PAGE_SIZE);

        registerBus();

        Ad.count(activity, 1);

        adBanner = new AdmobBanner(activity.getApplicationContext(), activity.getString(R.string.admob_banner), (RelativeLayout)activity.findViewById(R.id.layoutAd), 0, 0);
        if(AdmobBanner.hasSlot(activity, AdmobBanner.SLOT_PAYOUTS))
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

    public void resume()
    {
        super.resume();

        if(!isLoaded && itemList.size() == 0)
        {
            reload();
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

    public void pause()
    {
        super.pause();

        adBanner.pause();
    }

    public void destroy()
    {
        super.destroy();

        adBanner.close();

        unload();
        uncancel();

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
        activity.getMenuInflater().inflate(R.menu.controller_payouts, menu);
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
        PayoutItem item = adapter.getItem(position);
        if(item == null) return;

        if(item.status == PayoutItem.PENDING)
        {
            dialog(item);
        }
    }

    @Override
    public void onMenuClick(RecyclerView.Adapter adp, View childView, int position)
    {
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

    @Override
    public void onRefresh()
    {
        if (isLoading || layoutManager.findFirstVisibleItemPosition() >  0)
        {
            //swipeLayout.setRefreshing(false);
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

    public void dialog(final PayoutItem item)
    {
        Utils.confirm(activity, "Cash Out", "Do you want to cancel this cash out request?", new Runnable()
        {
            @Override
            public void run()
            {
                cancel(item.id);
            }
        }, null);
    }

    private void updateMenu()
    {
        if(menuItemReload != null) menuItemReload.setEnabled(!isLoading); //itemList.size() > 0);
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

        taskLoad = new SocketPayoutList(activity, this);
        taskLoad.start(mIndex, Payout.PAGE_SIZE);
    }

    private void reload()
    {
        mIndex = 0;

        load();
    }

    @Override
    public void payoutStarted()
    {
        isLoading = true;
        //swipeLayout.setEnabled(false);

        progressBar.setVisibility(mIndex != 0 ? View.GONE : View.VISIBLE);

        //swipeLayout.setVisibility(itemList.size() == 0 ? View.GONE : View.VISIBLE);

        updateMenu();
    }

    @Override
    public void payoutSuccess(List<PayoutItem> items, int index, int total)
    {
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

        scrollListener.setDone(items.size() < Payout.PAGE_SIZE);
        scrollListener.setReady();
        mIndex = index + items.size();

        isLoaded = true;
        isLoading = false;
        updateMenu();
    }

    @Override
    public void payoutError(String error)
    {
        isLoading = false;

        progressBar.setVisibility(View.GONE);

        scrollListener.setReady();

        UI.toast(activity, error);

        updateMenu();
    }



    public void uncancel()
    {
        if (taskCancel != null)
        {
            taskCancel.setCallback(null);
            taskCancel = null;
        }
    }

    public void cancel(long id)
    {
        uncancel();

        taskCancel = new SocketPayoutCancel(activity, this);
        taskCancel.start(id);
    }

    @Override
    public void payoutCancelStarted()
    {
        LoadingDialog.show(activity, "Cancelling cash out request...");
    }

    @Override
    public void payoutCancelSuccess(String message)
    {
        LoadingDialog.dismiss(activity);

        UI.toast(activity, message);

        //TODO reload or remove index??

        reload();
    }

    @Override
    public void payoutCancelError(String error)
    {
        LoadingDialog.dismiss(activity);

        UI.alert(activity, Rewards.appName, error);
    }


    public class PayoutsScrollListener extends RecyclerScrollListener
    {
        public PayoutsScrollListener(LinearLayoutManager lm, int visibleThreshold, int delay)
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

            //int pos = listView.getFirstVisiblePosition();

            //swipeLayout.setEnabled(yes || itemList.size() == 0); // || c == null || Streetwize.mPosts.size() == 0);
        }
    }
}

