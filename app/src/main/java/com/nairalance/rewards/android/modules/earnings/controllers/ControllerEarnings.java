package com.nairalance.rewards.android.modules.earnings.controllers;

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
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.activities.ActivityPayout;
import com.nairalance.rewards.android.activities.ActivityPayouts;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobBanner;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.miciniti.library.listeners.RecyclerScrollListener;
import com.nairalance.rewards.android.modules.earnings.Earning;
import com.nairalance.rewards.android.modules.earnings.adapters.AdapterEarnings;
import com.nairalance.rewards.android.modules.earnings.objects.EarningItem;
import com.nairalance.rewards.android.modules.earnings.socket.SocketEarnings;
import com.nairalance.rewards.android.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ControllerEarnings extends ControllerActivity implements SocketEarnings.SocketEarningsCallback, SwipeRefreshLayout.OnRefreshListener, RecyclerOnItemListener, AdmobInterst.AdInterstListener
{
    public static final String TAG = ControllerEarnings.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout layoutFooter;
    private TextView textTotal;

    private SocketEarnings taskLoad;

    private int mIndex = 0;
    private int mTotal = 0;

    private boolean isLoaded  = false;
    private boolean isLoading  = false;

    private Handler handler = new Handler();

    private AdapterEarnings adapter = null;
    private EarningsScrollListener scrollListener = null;
    private LinearLayoutManager layoutManager = null;

    private MenuItem menuItemReload;
    private List<EarningItem> itemList = new ArrayList<>();

    private AdmobInterst adInterst = null;
    private AdmobBanner adBanner = null;

    public ControllerEarnings(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        super.create(savedInstanceState);

        activity.setContentView(R.layout.controller_earnings);

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Earnings");

        adapter = new AdapterEarnings(activity, itemList, this);

        layoutManager = new LinearLayoutManager(activity);

        scrollListener = new EarningsScrollListener(layoutManager, Rewards.SCROLL_THRESHOLD, Rewards.SCROLL_DELAY);

        layoutFooter = activity.findViewById(R.id.layoutFooter);
        layoutFooter.findViewById(R.id.textAction).setVisibility(View.GONE);
        layoutFooter.setVisibility(View.GONE);

        ImageView imageThumb = layoutFooter.findViewById(R.id.imageThumb);
        imageThumb.setImageResource(R.drawable.logo_white);

        TextView textTitle = layoutFooter.findViewById(R.id.textTitle);
        textTitle.setTypeface(Rewards.appFont);
        textTitle.setText("Total Earnings (points)");

        textTotal = layoutFooter.findViewById(R.id.textValue);
        textTotal.setTypeface(Rewards.appFont);
        textTotal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        progressBar = activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        recyclerView = (RecyclerView) activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setHasFixedSize(true);

        scrollListener.setReady();
        scrollListener.setDone(adapter.getItemCount() < Earning.PAGE_SIZE);

        registerBus();

        Ad.count(activity, 1);

        adBanner = new AdmobBanner(activity.getApplicationContext(), activity.getString(R.string.admob_banner), (RelativeLayout)activity.findViewById(R.id.layoutAd), 0, 0);
        if(AdmobBanner.hasSlot(activity, AdmobBanner.SLOT_EARNINGS))
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

        if(Ad.isPossible(activity) && adInterst.isFailed())
        {
            adInterst.load();
        }

        adBanner.resume(true);
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
        activity.getMenuInflater().inflate(R.menu.controller_earnings, menu);
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

            case R.id.action_cashout:
                cashout();
                break;

            case R.id.action_payouts:
                cashouts();
                break;

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
        //EarningItem item = adapter.getItem(position);
        //if(item == null) return;

        //Intent intent = new Intent(activity, ActivityYoutube.class);
        //intent.putExtra(Strings.code, item.code);
        //activity.startActivity(intent);
    }

    @Override
    public void onMenuClick(RecyclerView.Adapter adp, View childView, int position)
    {
        EarningItem item = adapter.getItem(position);
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

    private void updateFooter()
    {
        textTotal.setText(Utils.formatPoints(mTotal));
        layoutFooter.setVisibility(View.VISIBLE);
    }

    private void updateMenu()
    {
        if(menuItemReload != null) menuItemReload.setEnabled(!isLoading); //itemList.size() > 0);
    }

    private void cashout()
    {
        Intent intent = new Intent(activity, ActivityPayout.class);
        activity.startActivity(intent);
    }

    private void cashouts()
    {
        Intent intent = new Intent(activity, ActivityPayouts.class);
        activity.startActivity(intent);
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

        taskLoad = new SocketEarnings(activity, this);
        taskLoad.start(mIndex, Earning.PAGE_SIZE);
    }

    private void reload()
    {
        mIndex = 0;

        load();
    }

    @Override
    public void earningStarted()
    {
        isLoading = true;
        //swipeLayout.setEnabled(false);

        progressBar.setVisibility(mIndex != 0 ? View.GONE : View.VISIBLE);

        //swipeLayout.setVisibility(itemList.size() == 0 ? View.GONE : View.VISIBLE);

        updateMenu();
    }

    @Override
    public void earningSuccess(List<EarningItem> items, int index, int total)
    {
        progressBar.setVisibility(View.GONE);

        if (mIndex == 0)
        {
            mTotal = total;
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

        scrollListener.setDone(items.size() < Earning.PAGE_SIZE);
        scrollListener.setReady();
        mIndex = index + items.size();

        isLoaded = true;
        isLoading = false;
        updateMenu();

        updateFooter();
    }

    @Override
    public void earningError(String error)
    {
        isLoading = false;

        progressBar.setVisibility(View.GONE);

        scrollListener.setReady();

        if(!TextUtils.isEmpty(error))
        {
            if(itemList.size() > 0)
            {
            }

            //emptyLayout.setTitle(error);
        }
        else
        {
            //emptyLayout.setTitle(getString(R.string.no_interest_found));
        }

        UI.toast(activity, error);

        updateMenu();
    }

    public class EarningsScrollListener extends RecyclerScrollListener
    {
        public EarningsScrollListener(LinearLayoutManager lm, int visibleThreshold, int delay)
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

