package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.account.controllers.ControllerAbout;

public class ActivityAbout extends AppCompatActivity implements ControllerListener
{
    private ControllerAbout aboutController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        aboutController = new ControllerAbout(this);
        aboutController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        aboutController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        aboutController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        aboutController.destroy();
    }


    @Override
    public void onBackPressed()
    {
        if(!aboutController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        aboutController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        aboutController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(aboutController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ControllerActivity getController()
    {
        return aboutController;
    }
}
