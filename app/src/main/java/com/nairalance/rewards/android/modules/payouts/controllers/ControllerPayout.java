package com.nairalance.rewards.android.modules.payouts.controllers;

import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.FragmentBase;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.nairalance.rewards.android.fragments.FragmentPayoutInput;
import com.nairalance.rewards.android.fragments.FragmentPayoutData;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutRateItem;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutTypeItem;

import java.util.ArrayList;
import java.util.List;

public class ControllerPayout extends ControllerActivity implements AdmobInterst.AdInterstListener
{
    private LinearLayout layoutMain;

    private Handler handler = new Handler();
    private ActionBar actionBar;

    private FragmentBase fragCurrent;
    private FragmentPayoutData fragData;
    private FragmentPayoutInput fragInput;

    private AdmobInterst adInterst = null;

    public PayoutRateItem rate = null;

    public List<PayoutRateItem> rateList = new ArrayList<>();
    public List<PayoutTypeItem> typeList = new ArrayList<>();
    public int earnings;
    public String message;

    public ControllerPayout(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        activity.setContentView(R.layout.controller_payout);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        actionBar = setSupportActionBar(toolbar);
        actionBar.setTitle("Cash Out");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        layoutMain = activity.findViewById(R.id.layoutMain);
        UI.overrideFonts(activity, layoutMain, Rewards.appFont);

        data();
        //input();

        Ad.count(activity, 1);

        adInterst = new AdmobInterst(activity, activity.getString(R.string.admob_inter));
        if(Ad.isPossible(activity))
        {
            adInterst.load();
        }

    }

    @Override
    public boolean backPressed()
    {
        if(fragCurrent == fragData)
        {
            if(adInterst.isLoaded())
            {
                adInterst.show(this);
                return true;
            }

            return false;
        }
        else
        {
            fragInput = null;

            data();
            return true;
        }
    }


    public void data()
    {
        if(fragData == null)
            fragData = new FragmentPayoutData();

        show(fragData);
    }

    public void input()
    {
        if(fragInput == null)
            fragInput = new FragmentPayoutInput();

        show(fragInput);
    }


    public void done()
    {
        activity.finish();
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void show(FragmentBase frag)
    {
        FragmentTransaction ft = getAppCompactActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.layoutFrame, frag);
        ft.commitAllowingStateLoss();

        fragCurrent = frag;
    }

    public boolean selectMenu(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                if(fragCurrent == fragData)
                {
                    activity.onBackPressed();
                }
                else
                {
                    data();
                }
                return true;

            case R.id.action_share:
                Rewards.shareApp(activity);
                return true;
        }

        return false;
    }

    @Override
    public void destroy()
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

}
