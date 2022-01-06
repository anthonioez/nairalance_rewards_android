package com.nairalance.rewards.android.modules.help.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.nairalance.rewards.android.modules.help.adapters.AdapterHow;
import com.nairalance.rewards.android.modules.help.objects.HowItem;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ControllerHow extends ControllerActivity implements AdmobInterst.AdInterstListener
{
    public static final String TAG = ControllerHow.class.getSimpleName();

    private RecyclerView recyclerView;

    private Handler handler = new Handler();

    private AdapterHow adapter = null;
    private LinearLayoutManager layoutManager = null;

    private List<HowItem> itemList = new ArrayList<>();

    private AdmobInterst adInterst = null;

    public ControllerHow(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        super.create(savedInstanceState);

        activity.setContentView(R.layout.controller_how);

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("How It Works");

        adapter = new AdapterHow(activity, itemList);

        layoutManager = new LinearLayoutManager(activity);

        recyclerView = activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        registerBus();

        Ad.count(activity, 1);

        adInterst = new AdmobInterst(activity, activity.getString(R.string.admob_inter));
        if(Ad.isPossible(activity))
        {
            adInterst.load();
        }

        loadItems();
    }

    public void start()
    {

    }

    public void resume()
    {
        super.resume();

        adapter.notifyDataSetChanged();

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
            default:
                Ad.possible(activity);
                break;
        }
    }

    public void createMenu(Menu menu)
    {
        activity.getMenuInflater().inflate(R.menu.controller_how, menu);
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

            case R.id.action_tell:
                Rewards.invite(activity);
                return true;
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

    private void loadItems()
    {
        itemList.clear();
        itemList.add(new HowItem(R.drawable.intro_invite,   R.string.intro_invite,      R.string.intro_invite_desc));
        itemList.add(new HowItem(R.drawable.intro_play,     R.string.intro_play,        R.string.intro_play_desc));

        itemList.add(new HowItem(R.drawable.intro_earn,     R.string.intro_earn,        R.string.intro_earn_desc));
        itemList.add(new HowItem(R.drawable.intro_cashout,  R.string.intro_cashout,     R.string.intro_cashout_desc));
        adapter.notifyDataSetChanged();
    }

}

