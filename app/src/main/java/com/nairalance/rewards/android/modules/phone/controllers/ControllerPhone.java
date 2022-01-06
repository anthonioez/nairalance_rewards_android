package com.nairalance.rewards.android.modules.phone.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.FragmentBase;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.activities.ActivityJoin;
import com.nairalance.rewards.android.fragments.FragmentSMSSend;
import com.nairalance.rewards.android.fragments.FragmentSMSVerify;

public class ControllerPhone extends ControllerActivity implements View.OnClickListener
{
    private LinearLayout layoutMain;

    private Handler handler = new Handler();
    private ActionBar actionBar;
    private FragmentBase fragCurrent;
    private FragmentSMSSend fragSend;
    private FragmentSMSVerify fragVerify;
    public String phone = "";
    public boolean hasCode = false;

    public ControllerPhone(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        activity.setContentView(R.layout.controller_phone);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        actionBar = setSupportActionBar(toolbar);
        actionBar.setTitle("Sign in");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        layoutMain = activity.findViewById(R.id.layoutMain);
        UI.overrideFonts(activity, layoutMain, Rewards.appFont);

        send();
    }

    @Override
    public boolean backPressed()
    {
        if(fragCurrent == fragSend)
        {
            return false;
        }
        else
        {
            fragVerify = null;

            send();
            return true;
        }
    }

    public void send()
    {
        if(fragSend == null)
            fragSend = new FragmentSMSSend();

        show(fragSend);
    }

    public void verify(boolean code)
    {
        this.hasCode = code;

        if(fragVerify == null)
            fragVerify = new FragmentSMSVerify();

        show(fragVerify);
    }


    public void main()
    {
        if(TextUtils.isEmpty(Prefs.getUsername(activity)))
        {
            Intent intent = new Intent(activity, ActivityJoin.class);
            activity.startActivity(intent);
        }
        else
        {
            Rewards.startMain(activity);
        }

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
                if(fragCurrent == fragSend)
                {
                    activity.onBackPressed();
                }
                else
                {
                    send();
                }
                return true;

            case R.id.action_share:
                Rewards.shareApp(activity);
                return true;
        }

        return false;
    }

    @Override
    public void onClick(View view)
    {
    }

    @Override
    public void destroy()
    {

    }
}
