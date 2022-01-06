package com.nairalance.rewards.android.modules.help.controllers;

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

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.miciniti.library.listeners.RecyclerScrollListener;
import com.nairalance.rewards.android.modules.help.Support;
import com.nairalance.rewards.android.modules.help.objects.FaqItem;
import com.nairalance.rewards.android.modules.help.socket.SocketFaq;
import com.nairalance.rewards.android.modules.help.adapters.AdapterFaq;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ControllerFaq extends ControllerActivity implements SocketFaq.SocketFaqCallback, SwipeRefreshLayout.OnRefreshListener, AdmobInterst.AdInterstListener
{
    public static final String TAG = ControllerFaq.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private SocketFaq taskLoad;

    private int mIndex = 0;

    private boolean isLoaded  = false;
    private boolean isLoading  = false;

    private Handler handler = new Handler();

    private AdapterFaq adapter = null;
    private FaqScrollListener scrollListener = null;
    private LinearLayoutManager layoutManager = null;

    private MenuItem menuItemReload;
    private List<FaqItem> itemList = new ArrayList<>();

    private AdmobInterst adInterst = null;

    public ControllerFaq(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        super.create(savedInstanceState);

        activity.setContentView(R.layout.controller_faq);

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Frequently Asked Questions");

        adapter = new AdapterFaq(activity, itemList);

        layoutManager = new LinearLayoutManager(activity);

        scrollListener = new FaqScrollListener(layoutManager, Rewards.SCROLL_THRESHOLD, Rewards.SCROLL_DELAY);

        progressBar = activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        recyclerView = activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setHasFixedSize(false);

        // RecyclerView has some built in animations to it, using the DefaultItemAnimator.
        // Specifically when you call notifyItemChanged() it does a fade animation for the changing
        // of the data in the ViewHolder. If you would like to disable this you can use the following:
        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator)
        {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

        scrollListener.setReady();
        scrollListener.setDone(adapter.getItemCount() < Rewards.PAGE_SIZE);

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

        if(Ad.isPossible(activity) && adInterst.isFailed())
        {
            adInterst.load();
        }
    }

    public void pause()
    {
        super.pause();
    }

    public void destroy()
    {
        super.destroy();

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
    }

    public void createMenu(Menu menu)
    {
        activity.getMenuInflater().inflate(R.menu.controller_faqs, menu);
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

    private void updateMenu()
    {
        if(menuItemReload != null)
        {
            menuItemReload.setEnabled(!isLoading);
            menuItemReload.setVisible(itemList.size() == 0);
        }
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

        taskLoad = new SocketFaq(activity, this);
        taskLoad.start(mIndex, Support.PAGE_SIZE);
    }

    private void reload()
    {
        mIndex = 0;

        load();
    }

    @Override
    public void faqStarted()
    {
        isLoading = true;
        //swipeLayout.setEnabled(false);

        progressBar.setVisibility(mIndex != 0 ? View.GONE : View.VISIBLE);

        //swipeLayout.setVisibility(itemList.size() == 0 ? View.GONE : View.VISIBLE);

        updateMenu();
    }

    @Override
    public void faqSuccess(List<FaqItem> items, int index)
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

        adapter = new AdapterFaq(activity, itemList);
        recyclerView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        if (items.size() > 0 && mIndex > 0)
        {
            recyclerView.smoothScrollBy(0, 100);
        }

        scrollListener.setDone(items.size() < Support.PAGE_SIZE);
        scrollListener.setReady();
        mIndex = index;

        isLoaded = true;
        isLoading = false;
        updateMenu();
    }

    @Override
    public void faqError(String error)
    {
        isLoading = false;

        progressBar.setVisibility(View.GONE);

        scrollListener.setReady();

        if(!TextUtils.isEmpty(error))
        {
            if(itemList.size() > 0)
            {
            }
        }
        else
        {
        }

        UI.toast(activity, error);

        updateMenu();
    }

    public void saveInstanceState(Bundle outState)
    {
        adapter.onSaveInstanceState(outState);
    }

    public void restoreInstanceState(Bundle outState)
    {
        adapter.onRestoreInstanceState(outState);
    }

    public class FaqScrollListener extends RecyclerScrollListener
    {
        public FaqScrollListener(LinearLayoutManager lm, int visibleThreshold, int delay)
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

