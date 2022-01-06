package com.nairalance.rewards.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.miciniti.library.objects.FragmentBase;
import com.nairalance.rewards.android.modules.phone.viewers.ViewerPhoneSend;

public class FragmentSMSSend extends FragmentBase
{
    private ViewerPhoneSend sendViewer  = new ViewerPhoneSend(this);

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        sendViewer.attach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return sendViewer.init(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        sendViewer.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        sendViewer.pause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        sendViewer.quit();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        sendViewer.detach();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(sendViewer.selectMenu(item))
            return true;

        return false;   //super.onOptionsItemSelected(item);
    }

}
