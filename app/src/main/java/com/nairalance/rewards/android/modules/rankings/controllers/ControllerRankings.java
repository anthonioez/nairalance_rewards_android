package com.nairalance.rewards.android.modules.rankings.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobBanner;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.nairalance.rewards.android.helpers.Image;
import com.nairalance.rewards.android.modules.rankings.adapters.AdapterRankings;
import com.nairalance.rewards.android.modules.rankings.objects.RankingItem;
import com.nairalance.rewards.android.modules.rankings.socket.SocketRankings;
import com.nairalance.rewards.android.utils.Utils;
import com.squareup.picasso.Callback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ControllerRankings extends ControllerActivity implements SocketRankings.SocketRankingsCallback, RecyclerOnItemListener, AdmobInterst.AdInterstListener
{
    public static final String TAG = ControllerRankings.class.getSimpleName();

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private LinearLayout layoutFooter;
    private TextView textPosition;
    private TextView textUsername;
    private TextView textValue;
    private ImageView imageHolder;
    private ImageView imageThumb;

    private SocketRankings taskLoad;

    private boolean isLoaded  = false;
    private boolean isLoading  = false;

    private Handler handler = new Handler();

    private AdapterRankings adapter = null;
    private LinearLayoutManager layoutManager = null;

    private MenuItem menuItemReload;

    private ArrayList<RankingItem> itemList = new ArrayList<>();

    private AdmobInterst adInterst = null;
    private AdmobBanner adBanner = null;

    private long mPosition = -1;
    private int mRewards = 0;

    public ControllerRankings(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        super.create(savedInstanceState);

        activity.setContentView(R.layout.controller_rankings);

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Rankings");

        layoutFooter = activity.findViewById(R.id.layoutFooter);

        textPosition = layoutFooter.findViewById(R.id.textPosition);
        textPosition.setTypeface(Rewards.appFontLight);

        textUsername = layoutFooter.findViewById(R.id.textUsername);
        textUsername.setTypeface(Rewards.appFont);

        textValue = layoutFooter.findViewById(R.id.textValue);
        textValue.setTypeface(Rewards.appFont);

        imageHolder     = layoutFooter.findViewById(R.id.imageHolder);
        imageThumb      = layoutFooter.findViewById(R.id.imageThumb);

        adapter = new AdapterRankings(activity, itemList, this);

        layoutManager = new LinearLayoutManager(activity);

        progressBar = activity.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        registerBus();

        Ad.count(activity, 1);


        adBanner = new AdmobBanner(activity.getApplicationContext(), activity.getString(R.string.admob_banner), (RelativeLayout)activity.findViewById(R.id.layoutAd), 0, 0);
        if(AdmobBanner.hasSlot(activity, AdmobBanner.SLOT_RANKINGS))
        {
            adBanner.prepare();
            adBanner.load();
        }

        adInterst = new AdmobInterst(activity, activity.getString(R.string.admob_inter));
        if(Ad.isPossible(activity))
        {
            adInterst.load();
        }

        mRewards = Prefs.getRewards(activity);
        mPosition = Prefs.getRanking(activity);

        updateFooter();
    }

    public void start()
    {

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
        activity.getMenuInflater().inflate(R.menu.controller_rankings, menu);

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
        RankingItem item = adapter.getItem(position);
        if(item == null) return;

        //Intent intent = new Intent(activity, ActivityYoutube.class);
        //intent.putExtra(Strings.code, item.code);
        //activity.startActivity(intent);
    }

    @Override
    public void onMenuClick(RecyclerView.Adapter adp, View childView, int position)
    {
        RankingItem item = adapter.getItem(position);
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
        if(menuItemReload != null) menuItemReload.setEnabled(!isLoading);
    }

    private void updateFooter()
    {
        textUsername.setText(Prefs.getUsername(activity));
        textPosition.setText(mPosition == -1 ? "-" : String.valueOf(mPosition));
        textValue.setText(Utils.formatPoints(mRewards));

        String image = Prefs.getThumbUrl(activity);
        Image.loadFull(activity, image, 0, imageThumb, false, new Callback()
        {
            @Override
            public void onSuccess()
            {
                imageThumb.setVisibility(View.VISIBLE);
                imageHolder.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                imageThumb.setVisibility(View.GONE);
                imageHolder.setVisibility(View.VISIBLE);
            }
        });
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

        taskLoad = new SocketRankings(activity, this);
        taskLoad.start();
    }

    private void reload()
    {
        load();
    }

    @Override
    public void rankingStarted()
    {
        isLoading = true;

        progressBar.setVisibility(View.VISIBLE);

        updateMenu();
    }

    @Override
    public void rankingSuccess(List<RankingItem> items, long ranking, int rewards)
    {
        progressBar.setVisibility(View.GONE);

        mRewards = rewards;
        mPosition = ranking;

        itemList.clear();
        itemList.addAll(items);

        adapter.notifyDataSetChanged();

        if (items.size() > 0)
        {
            //recyclerView.smoothScrollBy(0, 100);
        }

        isLoaded = true;
        isLoading = false;

        updateMenu();

        updateFooter();
    }

    @Override
    public void rankingError(String error)
    {
        isLoading = false;

        progressBar.setVisibility(View.GONE);

        if(!TextUtils.isEmpty(error))
        {
            UI.toast(activity, error);
        }
        else
        {
            //emptyLayout.setTitle(getString(R.string.no_rankings_found));
        }

        updateMenu();
    }
}

