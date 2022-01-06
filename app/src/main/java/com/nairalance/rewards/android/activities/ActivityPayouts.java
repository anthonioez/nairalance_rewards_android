package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.payouts.controllers.ControllerPayouts;

public class ActivityPayouts extends AppCompatActivity implements ControllerListener
{
    private ControllerPayouts payoutsController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        payoutsController = new ControllerPayouts(this);
        payoutsController.create(savedInstanceState);
    }


    @Override
    public void onBackPressed()
    {
        if(!payoutsController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        payoutsController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        payoutsController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        payoutsController.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        payoutsController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(payoutsController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(payoutsController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


    @Override
    public ControllerActivity getController()
    {
        return payoutsController;
    }
}
