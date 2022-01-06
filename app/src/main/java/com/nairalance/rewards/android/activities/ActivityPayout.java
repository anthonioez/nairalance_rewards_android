package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.payouts.controllers.ControllerPayout;

public class ActivityPayout extends AppCompatActivity implements ControllerListener
{
    private ControllerPayout cashoutController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        cashoutController = new ControllerPayout(this);
        cashoutController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        cashoutController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        cashoutController.pause();
    }

    @Override
    public void onBackPressed()
    {
        if(!cashoutController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        cashoutController.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        cashoutController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(cashoutController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(cashoutController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    public ControllerActivity getController()
    {
        return cashoutController;
    }
}
