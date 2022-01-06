package com.nairalance.rewards.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miciniti.library.objects.FragmentBase;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.modules.start.objects.IntroItem;
import com.nairalance.rewards.android.modules.start.viewers.ViewerSlide;

/**
 * Created by Miciniti on 08/05/2018.
 */

public class FragmentSlide extends FragmentBase
{
    private ViewerSlide slideViewer = new ViewerSlide(this);

    public static FragmentSlide newInstance(IntroItem item)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(Strings.title, item.title);
        bundle.putInt(Strings.desc, item.desc);
        bundle.putInt(Strings.icon, item.icon);
        bundle.putInt(Strings.action, item.action);

        FragmentSlide frag = new FragmentSlide();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        slideViewer.attach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return slideViewer.init(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        slideViewer.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        slideViewer.pause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        slideViewer.quit();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();

        slideViewer.detach();
    }
}
