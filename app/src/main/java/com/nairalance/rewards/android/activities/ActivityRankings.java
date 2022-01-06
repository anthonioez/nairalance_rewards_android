package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.rankings.controllers.ControllerRankings;

public class ActivityRankings extends AppCompatActivity implements ControllerListener
{
    private ControllerRankings rankingsController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        rankingsController = new ControllerRankings(this);
        rankingsController.create(savedInstanceState);
    }


    @Override
    public void onBackPressed()
    {
        if(!rankingsController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        rankingsController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        rankingsController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        rankingsController.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        rankingsController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(rankingsController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(rankingsController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    public ControllerActivity getController()
    {
        return rankingsController;
    }
}
