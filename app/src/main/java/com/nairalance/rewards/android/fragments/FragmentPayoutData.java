package com.nairalance.rewards.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.miciniti.library.objects.FragmentBase;
import com.nairalance.rewards.android.modules.payouts.viewers.ViewerPayoutData;

public class FragmentPayoutData extends FragmentBase
{
    private ViewerPayoutData dataViewer = new ViewerPayoutData(this);

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        dataViewer.attach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return dataViewer.init(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        dataViewer.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        dataViewer.pause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        dataViewer.quit();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        dataViewer.detach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        dataViewer.createMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        dataViewer.prepareMenu(menu);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(dataViewer.selectMenu(item))
            return true;

        return false;   //super.onOptionsItemSelected(item);
    }

}
