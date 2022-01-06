package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.home.controllers.ControllerHome;

public class ActivityHome extends AppCompatActivity implements ControllerListener
{
    private ControllerHome homeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        homeController = new ControllerHome(this);
        homeController.create(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        if(homeController == null) homeController = new ControllerHome(this);

        homeController.intent(intent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        homeController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        homeController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        homeController.destroy();
    }


    @Override
    public void onBackPressed()
    {
        if(!homeController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        homeController.createMenu(menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(homeController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(homeController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        homeController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public ControllerActivity getController()
    {
        return homeController;
    }
}
