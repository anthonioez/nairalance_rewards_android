package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.phone.controllers.ControllerPhone;

public class ActivityPhone extends AppCompatActivity implements ControllerListener
{
    private ControllerPhone phoneController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        phoneController = new ControllerPhone(this);
        phoneController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        phoneController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        phoneController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        phoneController.destroy();
    }

    @Override
    public void onBackPressed()
    {
        if(!phoneController.backPressed())
        {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        phoneController.createMenu(menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(phoneController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(phoneController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ControllerActivity getController()
    {
        return phoneController;
    }
}
