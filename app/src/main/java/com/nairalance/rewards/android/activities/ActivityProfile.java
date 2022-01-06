package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.profile.controllers.ControllerProfile;

public class ActivityProfile extends AppCompatActivity implements ControllerListener
{
    private ControllerProfile profileController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        profileController = new ControllerProfile(this);
        profileController.create(savedInstanceState);
    }


    @Override
    public void onBackPressed()
    {
        if(!profileController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        profileController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        profileController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        profileController.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        profileController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(profileController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(profileController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        profileController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        profileController.permissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public ControllerActivity getController()
    {
        return profileController;
    }
}
