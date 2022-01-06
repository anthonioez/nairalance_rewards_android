package com.nairalance.rewards.android.modules.start.objects;

import com.miciniti.library.objects.FragmentBase;

/**
 * Created by Miciniti on 10/05/2018.
 */

public class IntroItem
{
    public int icon;
    public int title;
    public int desc;
    public int action;
    public FragmentBase fragment;

    public IntroItem()
    {
        this.icon = 0;
        this.title = 0;
        this.desc = 0;
        this.action = 0;
        this.fragment = null;
    }

    public IntroItem(int icon, int title, int desc, int action, FragmentBase fragment)
    {
        this.icon = icon;
        this.title = title;
        this.desc = desc;
        this.action = action;
        this.fragment = fragment;
    }

}
