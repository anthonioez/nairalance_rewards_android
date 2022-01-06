package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatCallback;
import androidx.appcompat.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.rewards.controllers.ControllerYoutube;

public class ActivityYoutube extends YouTubeBaseActivity implements ControllerListener
{
    private ControllerYoutube youtubeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(youtubeController == null) youtubeController = new ControllerYoutube(this);

        youtubeController.create(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        if(youtubeController == null) youtubeController = new ControllerYoutube(this);

        youtubeController.intent(intent);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        youtubeController.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        youtubeController.destroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        youtubeController.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        youtubeController.pause();
    }

    @Override
    public void onBackPressed()
    {
        if(!youtubeController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode)
    {
        if (intent != null)
        {
            super.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        youtubeController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        youtubeController.createMenu(menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(youtubeController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(youtubeController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public ControllerActivity getController()
    {
        return youtubeController;
    }
}
