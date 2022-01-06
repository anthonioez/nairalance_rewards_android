package com.nairalance.rewards.android.modules.account.controllers;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.RewardsAnalytics;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.nairalance.rewards.android.modules.account.adapters.AdapterOptions;
import com.nairalance.rewards.android.modules.account.objects.OptionItem;
import com.nairalance.rewards.android.modules.account.objects.OptionListener;

import java.util.ArrayList;
import java.util.List;

public class ControllerOptions extends ControllerActivity implements OptionListener, AdmobInterst.AdInterstListener
{
    private static final int SELECT_SOUND   = 393;

    private static final int OPT_STATUS     = 1;
    private static final int OPT_SOUND      = 2;
    //private static final int OPT_VIBRATE    = 3;
    //private static final int OPT_LED        = 4;

    private ActionBar actionBar;
    private AdapterOptions adapter;
    private RecyclerView recyclerView;

    private List<OptionItem> options = new ArrayList<>();
    private AdmobInterst adInterst = null;

    public ControllerOptions(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        activity.setContentView(R.layout.controller_options);

        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        actionBar = setSupportActionBar(toolbar);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.settings);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        options = new ArrayList<>();
        options.add(OptionItem.newHeader(0,             getString(R.string.pref_header_notifications)));
        options.add(OptionItem.newSwitch(OPT_STATUS,    getString(R.string.pref_title_status),          getString(R.string.pref_push_on),               getString(R.string.pref_push_off)));
        options.add(OptionItem.newLink  (OPT_SOUND,     getString(R.string.pref_title_sound),           soundName(Prefs.getPushSound(activity))));
        //options.add(OptionItem.newSwitch(OPT_VIBRATE,   getString(R.string.pref_title_vibrate),         getString(R.string.pref_vibrate_on),            getString(R.string.pref_vibrate_off)));
        //options.add(OptionItem.newSwitch(OPT_LED,       getString(R.string.pref_title_led),             getString(R.string.pref_led_on),                getString(R.string.pref_led_off)));

        //options.add(OptionItem.newHeader(0,                 getString(R.string.pref_header_image)));
        //options.add(OptionItem.newSwitch(OPT_IMAGE_FEED,    getString(R.string.pref_title_feed),          getString(R.string.pref_feed_image_on),           getString(R.string.pref_feed_image_off)));
        //options.add(OptionItem.newSwitch(OPT_IMAGE_PUSH,    getString(R.string.pref_title_push),          getString(R.string.pref_push_image_on),           getString(R.string.pref_push_image_off)));

        //options.add(OptionItem.newHeader(0,             getString(R.string.pref_header_content)));

        adapter = new AdapterOptions(activity, options, true, this);

        recyclerView = activity.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

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
    public void resume()
    {
        adapter.notifyDataSetChanged();

        if(Ad.isPossible(activity) && adInterst.isFailed())
        {
            adInterst.load();
        }
    }

    public void destroy()
    {
        if(adInterst != null) adInterst.close();
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
        if(requestCode == SELECT_SOUND)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String name;
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null)
                {
                    String ringTonePath = uri.toString();
                    Prefs.setPushSound(activity, ringTonePath);

                    name = soundName(ringTonePath);
                }
                else
                {
                    Prefs.setPushSound(activity, "");

                    name = "No sound";
                }

                soundEdit(name);
            }
        }
    }

    private void soundEdit(String name)
    {
        for (OptionItem item : options)
        {
            if(item.id == OPT_SOUND)
            {
                item.descOn = name;
                adapter.notifyDataSetChanged();
            }
        }
    }
    public void createMenu(Menu menu)
    {
        //menu.clear();
        //activity.getMenuInflater().inflate(R.menu.activity_auth, menu);

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
        }

        return false;
    }

    private String soundName(String sound)
    {
        if(TextUtils.isEmpty(sound))
        {
            return "No sound";
        }

        Ringtone ringtone = RingtoneManager.getRingtone(activity, Uri.parse(sound));
        if(ringtone == null)
        {
            return "No sound";
        }

        String title = ringtone.getTitle(activity);

        return title;
    }

    private void soundSelect()
    {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Sound");

        // for existing ringtone
        Uri uri = Uri.parse(Prefs.getPushSound(activity)); //RingtoneManager.getActualDefaultRingtoneUri(activity, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, uri);
        activity.startActivityForResult(intent, SELECT_SOUND);
    }

    @Override
    public void itemClicked(RecyclerView.Adapter adp, View v, int position)
    {
        OptionItem item = adapter.getItem(position);
        if(item != null)
        {
            switch (item.id)
            {
                case OPT_STATUS:
                    Prefs.setPushStatus(activity, !Prefs.getPushStatus(activity));
                    Prefs.setPushTokenSent(activity, false);
                    adapter.notifyDataSetChanged();
                    break;

                case OPT_SOUND:
                    soundSelect();
                    break;

                /*
                case OPT_VIBRATE:
                    Prefs.setPushVibrate(activity, !Prefs.getPushVibrate(activity));
                    adapter.notifyDataSetChanged();
                    break;

                case OPT_LED:
                    Prefs.setPushLED(activity, !Prefs.getPushLED(activity));
                    adapter.notifyDataSetChanged();
                    break;
                */
                default:
                    break;
            }
        }
    }

    @Override
    public boolean activeValue(int id)
    {
        if(Prefs.getPushStatus(activity) == false)
        {
            if(id == OPT_SOUND /*|| id == OPT_VIBRATE || id == OPT_LED*/)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean switchValue(int id)
    {
        switch (id)
        {
            case OPT_STATUS:
                return Prefs.getPushStatus(activity);

            /*
            case OPT_VIBRATE:
                return Prefs.getPushVibrate(activity);

            case OPT_LED:
                return Prefs.getPushLED(activity);
            */
        }

        return false;
    }

    @Override
    public void switchChanged(int id, boolean state)
    {
        switch (id)
        {
            case OPT_STATUS:
                Prefs.setPushStatus(activity, state);
                Prefs.setPushTokenSent(activity, false);
                adapter.notifyDataSetChanged();

                RewardsAnalytics.logEvent(activity, "push_state", String.valueOf(state));
                break;

            /*
            case OPT_VIBRATE:
                Prefs.setPushVibrate(activity, state);
                break;

            case OPT_LED:
                Prefs.setPushLED(activity, state);
                break;
            */
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
}
