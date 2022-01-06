package com.nairalance.rewards.android.modules.friends.controllers;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;

public class ControllerFriends extends ControllerActivity implements View.OnClickListener
{
    public ControllerFriends(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.setContentView(R.layout.activity_friends);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setTitle("Search Friends");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //layoutMain = activity.findViewById(R.id.layoutMain);
        //UI.overrideFonts(activity, layoutMain, Rewards.appFont);
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
