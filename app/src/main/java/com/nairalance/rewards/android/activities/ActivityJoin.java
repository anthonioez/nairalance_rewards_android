package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.profile.controllers.ControllerJoin;

public class ActivityJoin extends AppCompatActivity implements ControllerListener
{
    private ControllerJoin joinController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        joinController = new ControllerJoin(this);
        joinController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        joinController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        joinController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        joinController.destroy();
    }


    @Override
    public void onBackPressed()
    {
        if(!joinController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        joinController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(joinController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(joinController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        joinController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        joinController.permissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public ControllerActivity getController()
    {
        return joinController;
    }
}
