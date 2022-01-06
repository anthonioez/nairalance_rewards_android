package com.miciniti.library.objects;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Miciniti onEvent 26/09/16.
 */
public class ViewerFragment
{
    public Context context;
    public final FragmentBase fragment;

    public ViewerFragment(FragmentBase fragment)
    {
        this.fragment = fragment;
    }

    public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return null;
    }

    public void quit()
    {

    }

    public void resume()
    {
    }

    public void pause()
    {
    }

    public boolean backPressed()
    {
        return false;
    }

    public void ready(Context context)
    {
    }

    public void attach(Context context)
    {
        this.context = context;
    }

    public void detach()
    {
    }

    public void createMenu(Menu menu, MenuInflater inflater)
    {
    }

    public void prepareMenu(Menu menu)
    {
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

    public String getString(int resId)
    {
        String str = "";
        try
        {
            str = context.getString(resId);
        }
        catch (Exception e)
        {
            Log.e("gS", "ex:" + e.getMessage());
        }

        return str;
/*
        if(fragment.isAdded())
            return fragment.getString(resId);
        else
            return "";
*/
    }

    public View getView()
    {
        return fragment.getView();
    }

    public Context getContext()
    {
        if(context != null)
            return context;
        else
            return fragment.getContext();
    }

    public ControllerActivity getController()
    {
        FragmentActivity act = fragment.getActivity();


        if(act != null && act instanceof ControllerListener)
        {
            ControllerListener lst = (ControllerListener) act;
            return lst.getController();
        }

        return null;
    }

    public FragmentActivity getActivity()
    {
        FragmentActivity act = fragment.getActivity();

        return act;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AppEvent event)
    {
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
}