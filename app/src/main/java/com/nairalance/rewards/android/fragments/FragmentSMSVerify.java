package com.nairalance.rewards.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miciniti.library.objects.FragmentBase;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.modules.phone.viewers.ViewerPhoneVerify;
import com.nairalance.rewards.android.modules.start.objects.IntroItem;

public class FragmentSMSVerify extends FragmentBase
{
    private ViewerPhoneVerify verifyViewer  = new ViewerPhoneVerify(this);

    public static FragmentSMSVerify newInstance(boolean code)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(Strings.code, code);

        FragmentSMSVerify frag = new FragmentSMSVerify();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        verifyViewer.attach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return verifyViewer.init(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        verifyViewer.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        verifyViewer.pause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        verifyViewer.quit();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        verifyViewer.detach();
    }
}
