package com.nairalance.rewards.android.modules.profile.controllers;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.activities.ActivityOptions;
import com.nairalance.rewards.android.activities.ActivityPayout;
import com.nairalance.rewards.android.activities.ActivityProfile;

public class ControllerAccount extends ControllerActivity implements View.OnClickListener
{
    private LinearLayout layoutMain;
    private Button buttonCashout;
    private Button buttonProfile;
    private Button buttonSettings;

    public ControllerAccount(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.setContentView(R.layout.controller_account);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setTitle("Profile");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        layoutMain = activity.findViewById(R.id.layoutMain);
        UI.overrideFonts(activity, layoutMain, Rewards.appFont);

        buttonCashout = activity.findViewById(R.id.buttonCashout);
        buttonProfile = activity.findViewById(R.id.buttonProfile);
        buttonSettings = activity.findViewById(R.id.buttonSettings);

        buttonCashout.setOnClickListener(this);
        buttonProfile.setOnClickListener(this);
        buttonSettings.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        if(view == buttonCashout)
        {
            Intent intent = new Intent(activity, ActivityPayout.class);
            activity.startActivity(intent);
        }
        else if(view == buttonProfile)
        {
            Intent intent = new Intent(activity, ActivityProfile.class);
            activity.startActivity(intent);
        }
        else if(view == buttonSettings)
        {
            Intent intent = new Intent(activity, ActivityOptions.class);
            activity.startActivity(intent);
        }
    }

    @Override
    public void destroy()
    {

    }

}
