package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.help.controllers.ControllerHow;

public class ActivityHow extends AppCompatActivity implements ControllerListener
{
    private ControllerHow howController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        howController = new ControllerHow(this);
        howController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        howController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        howController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        howController.destroy();
    }


    @Override
    public void onBackPressed()
    {
        if(!howController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        howController.createMenu(menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(howController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(howController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        howController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public ControllerActivity getController()
    {
        return howController;
    }
}
