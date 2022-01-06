package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.earnings.controllers.ControllerEarnings;

public class ActivityEarnings extends AppCompatActivity implements ControllerListener
{
    private ControllerEarnings earningsController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        earningsController = new ControllerEarnings(this);
        earningsController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        earningsController.resume();
    }


    @Override
    public void onBackPressed()
    {
        if(!earningsController.backPressed())
        {
            super.onBackPressed();
        }
    }


    @Override
    protected void onPause()
    {
        super.onPause();

        earningsController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        earningsController.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        earningsController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(earningsController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(earningsController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    public ControllerActivity getController()
    {
        return earningsController;
    }
}
