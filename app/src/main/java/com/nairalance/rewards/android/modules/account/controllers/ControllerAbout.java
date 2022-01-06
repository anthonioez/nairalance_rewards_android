package com.nairalance.rewards.android.modules.account.controllers;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.miciniti.library.Links;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.activities.ActivityFaq;
import com.nairalance.rewards.android.activities.ActivityHow;
import com.nairalance.rewards.android.activities.ActivityOptions;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.nairalance.rewards.android.modules.account.adapters.AdapterOptions;
import com.nairalance.rewards.android.modules.account.objects.OptionItem;
import com.nairalance.rewards.android.modules.account.objects.OptionListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class ControllerAbout extends ControllerActivity implements OptionListener, AdmobInterst.AdInterstListener
{
    private static final int OPT_HOWS = 1;
    private static final int OPT_FAQS = 2;

    private static final int OPT_SETS = 11;

    private static final int OPT_VERS = 20;
    private static final int OPT_RATE = 21;
    private static final int OPT_MORE = 22;

    private static final int OPT_SUPT = 30;
    private static final int OPT_WEBS = 31;
    private static final int OPT_PUBR = 32;
    private static final int OPT_DEVR = 33;

    private static final int OPT_FCBK = 40;
    private static final int OPT_TWTR = 41;
    private static final int OPT_TELL = 42;
    private static final int OPT_INVT = 43;

    private static final int OPT_LOGT = 50;
    private static final int SELECT_SOUND = 221;

    private ActionBar actionBar;

    private AdapterOptions adapter;
    private RecyclerView recyclerView;
    private List<OptionItem> options = new ArrayList<>();


    private AdmobInterst adInterst = null;

    public ControllerAbout(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        activity.setContentView(R.layout.controller_accounts);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        actionBar = setSupportActionBar(toolbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.about);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        adapter = new AdapterOptions(activity, options, false, this);

        recyclerView = activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        loadItems();

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

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {
        loadItems();

        if(Ad.isPossible(activity) && adInterst.isFailed())
        {
            adInterst.load();
        }
    }

    public void destroy()
    {
        super.destroy();

        if(adInterst != null) adInterst.close();

        unregisterBus();
    }

    @Override
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
        //menu.clear();
        activity.getMenuInflater().inflate(R.menu.controller_account, menu);

    }

    public boolean prepareMenu(Menu menu)
    {
        return true;
    }

    public boolean selectMenu(MenuItem item)
    {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                adInterst.showOrFinish(activity, this);
                return true;

            case R.id.action_share:
                Rewards.shareApp(activity);
                return true;

            case R.id.action_settings:
                settings();
                return true;
        }

        return false;
    }

    private void loadItems()
    {
        options.clear();
        options.add(OptionItem.newHeader(0,         getString(R.string.help)));

        options.add(OptionItem.newLink  (OPT_HOWS,      getString(R.string.help),       getString(R.string.about_help),                                   R.drawable.about_help));
        options.add(OptionItem.newLink  (OPT_FAQS,      getString(R.string.faqs),               getString(R.string.about_faqs),         R.drawable.about_faq));

        options.add(OptionItem.newHeader(0,         getString(R.string.info)));
        options.add(OptionItem.newLink  (OPT_WEBS,      getString(R.string.website),            getString(R.string.app_domain),         R.drawable.about_website));
        options.add(OptionItem.newLink  (OPT_VERS,      getString(R.string.version),            Rewards.appVer,                         R.drawable.about_ver));
        options.add(OptionItem.newLink  (OPT_RATE,      getString(R.string.rate),               getString(R.string.about_rate),         R.drawable.about_rate));
        options.add(OptionItem.newLink  (OPT_MORE,      getString(R.string.more_apps),          getString(R.string.about_more),         R.drawable.about_apps));

        options.add(OptionItem.newHeader(0,         getString(R.string.connect_us)));
        options.add(OptionItem.newLink  (OPT_FCBK,      getString(R.string.like_facebook),  getString(R.string.about_facebook),             R.drawable.about_facebook));
        options.add(OptionItem.newLink  (OPT_TWTR,      getString(R.string.follow_twitter), getString(R.string.about_twitter),              R.drawable.about_twitter));
        options.add(OptionItem.newLink  (OPT_TELL,      getString(R.string.tell_a_friend),  getString(R.string.about_tell),         R.drawable.about_tell));

        options.add(OptionItem.newHeader(0,         getString(R.string.contact_us)));
        options.add(OptionItem.newLink  (OPT_SUPT,      getString(R.string.support),        getString(R.string.app_email),                  R.drawable.about_support));
        options.add(OptionItem.newLink  (OPT_PUBR,      getString(R.string.publisher),      getString(R.string.about_miciniti),                             R.drawable.about_miciniti));
        options.add(OptionItem.newLink  (OPT_DEVR,      getString(R.string.developer),      getString(R.string.about_aince),                                R.drawable.about_developer));

        adapter.notifyDataSetChanged();

        //Other
        //  privacy policy
        //  terms and conditions
        //  feedback
        //  help and support

    }

    private void hows()
    {
        activity.startActivity(new Intent(activity, ActivityHow.class));
    }

    private void faqs()
    {
        activity.startActivity(new Intent(activity, ActivityFaq.class));
    }

    private void settings()
    {
        activity.startActivity(new Intent(activity, ActivityOptions.class));
    }

    private void logoutAsk()
    {
        UI.confirm(activity, Rewards.appName, getString(R.string.logout_confirm), new Runnable()
        {
            @Override
            public void run()
            {
                logout();
            }
        }, null);
    }

    private void logout()
    {
        Rewards.logout(activity);

        UI.toast(activity, getString(R.string.logout_successful));

        loadItems();

        recyclerView.smoothScrollToPosition(0);
    }

    @Override
    public boolean switchValue(int id)
    {
        return false;
    }

    @Override
    public boolean activeValue(int id)
    {

        return true;
    }

    @Override
    public void itemClicked(RecyclerView.Adapter adp, View view, int pos)
    {
        OptionItem item = adapter.getItem(pos);
        if(item == null) return;

        switch (item.id)
        {
            case OPT_HOWS:
                hows();
                break;

            case OPT_FAQS:
                faqs();
                break;

            case OPT_SETS:
                settings();
                break;


            case OPT_VERS:
                Links.openUrl(activity, getString(R.string.website_apps), false);
                break;

            case OPT_RATE:
                Rewards.rateApp(activity);
                break;

            case OPT_MORE:
                Links.moreApps(activity);
                break;


            case OPT_FCBK:
                Links.openFacebook(activity, Rewards.facebookHandle, getString(R.string.fb_page_id));
                break;

            case OPT_TWTR:
                Links.openTwitter(activity, Rewards.twitterHandle);
                break;

            case OPT_INVT:
                Rewards.invite(activity);
                break;

            case OPT_TELL:
                Rewards.invite(activity);
                //Rewards.shareApp(activity);
                break;


            case OPT_SUPT:
                Rewards.openSupport(activity);
                break;

            case OPT_WEBS:
                Links.openUrl(activity, getString(R.string.website_url), false);
                break;

            case OPT_PUBR:
                Links.openUrl(activity, getString(R.string.publisher_url), false);
                break;

            case OPT_DEVR:
                Links.openTwitter(activity, getString(R.string.aince));
                break;


            case OPT_LOGT:
                logoutAsk();
                break;

        }
    }

    @Override
    public void switchChanged(int id, boolean state)
    {

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

        if(event.name.equals(Strings.login))
        {
            loadItems();
        }
        else if(event.name.equals(Strings.logout))
        {
            loadItems();
        }
        else if(event.name.endsWith(Strings.account))
        {
            resume();
        }
    }
}
