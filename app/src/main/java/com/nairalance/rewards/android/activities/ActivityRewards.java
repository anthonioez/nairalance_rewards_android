package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.rewards.controllers.ControllerRewards;

public class ActivityRewards extends ActivityBase implements ControllerListener
{
    private ControllerRewards rewardsController = null;

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        if(rewardsController == null) rewardsController = new ControllerRewards(this);

        rewardsController.intent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(rewardsController == null) rewardsController = new ControllerRewards(this);

        rewardsController.create(savedInstanceState);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        rewardsController.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        rewardsController.destroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        rewardsController.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        rewardsController.pause();
    }

    @Override
    public void onBackPressed()
    {
        if(!rewardsController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode)
    {
        if (intent != null)
        {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        rewardsController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        rewardsController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(rewardsController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(rewardsController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ControllerActivity getController()
    {
        return rewardsController;
    }
}
