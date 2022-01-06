package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.account.controllers.ControllerOptions;

public class ActivityOptions extends AppCompatActivity implements ControllerListener
{
    private ControllerOptions optionsController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        optionsController = new ControllerOptions(this);
        optionsController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        optionsController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        optionsController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        optionsController.destroy();
    }


    @Override
    public void onBackPressed()
    {
        if(!optionsController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        optionsController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        optionsController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(optionsController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ControllerActivity getController()
    {
        return optionsController;
    }
}
