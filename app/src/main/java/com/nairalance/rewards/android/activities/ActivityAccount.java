package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.profile.controllers.ControllerAccount;

public class ActivityAccount extends AppCompatActivity implements ControllerListener
{
    private ControllerAccount accountController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        accountController = new ControllerAccount(this);
        accountController.create(savedInstanceState);
    }


    @Override
    public void onBackPressed()
    {
        if(!accountController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        accountController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        accountController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        accountController.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        accountController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(accountController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(accountController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        accountController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        accountController.permissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public ControllerActivity getController()
    {
        return accountController;
    }
}
