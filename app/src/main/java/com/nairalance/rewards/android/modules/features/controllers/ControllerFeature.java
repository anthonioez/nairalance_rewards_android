package com.nairalance.rewards.android.modules.features.controllers;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.R;

public class ControllerFeature extends ControllerActivity implements View.OnClickListener
{
    public ControllerFeature(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.setContentView(R.layout.activity_features);

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
