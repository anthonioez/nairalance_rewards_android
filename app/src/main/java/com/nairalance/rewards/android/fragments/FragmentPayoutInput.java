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
import com.nairalance.rewards.android.modules.payouts.objects.PayoutRateItem;
import com.nairalance.rewards.android.modules.payouts.viewers.ViewerPayoutInput;

public class FragmentPayoutInput extends FragmentBase
{
    private ViewerPayoutInput inputViewer = new ViewerPayoutInput(this);

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        inputViewer.attach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inputViewer.init(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        inputViewer.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        inputViewer.pause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        inputViewer.quit();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        inputViewer.detach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inputViewer.createMenu(menu, inflater);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        inputViewer.prepareMenu(menu);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(inputViewer.selectMenu(item))
            return true;

        return false;   //super.onOptionsItemSelected(item);
    }
}
