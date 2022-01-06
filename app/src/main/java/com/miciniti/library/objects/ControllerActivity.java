package com.miciniti.library.objects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Miciniti onEvent 26/09/16.
 */
public class ControllerActivity
{
    public Activity activity;

    public ControllerActivity(Activity activity)
    {
        this.activity = activity;
    }

    protected void create(Bundle savedInstanceState)
    {

    }

    public void start()
    {

    }

    public void resume()
    {

    }

    public void pause()
    {
    }

    public void destroy()
    {
    }


    public boolean backPressed()
    {
        return false;
    }

    public void createMenu(Menu menu)
    {
    }

    public boolean prepareMenu(Menu menu)
    {
        return false;
    }

    public boolean selectMenu(MenuItem item)
    {
        return false;
    }

    public void activityResult(int requestCode, int resultCode, Intent data)
    {

    }

    public void permissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
    }

    public String getString(int id)
    {
        if(activity == null)
        {
            return "";
        }

        return activity.getString(id);
    }


    protected void unregisterBus()
    {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    protected void registerBus()
    {
        if(!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    public AppCompatActivity getAppCompactActivity()
    {
        return ((AppCompatActivity)activity);
    }


    public ActionBar setSupportActionBar(Toolbar toolbar)
    {
        getAppCompactActivity().setSupportActionBar(toolbar);

        return getSupportActionBar();
    }

    public ActionBar getSupportActionBar()
    {
        return getAppCompactActivity().getSupportActionBar();
    }
}

