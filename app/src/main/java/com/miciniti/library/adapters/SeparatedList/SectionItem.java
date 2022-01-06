package com.miciniti.library.adapters.SeparatedList;

import android.widget.Adapter;

/**
 * Created by Miciniti on 26/12/2016.
 */

public class SectionItem
{
    public String   key;
    public String   title;
    public Adapter adapter;

    public SectionItem(String key, String title, Adapter adapter)
    {
        this.key = key;
        this.title = title;
        this.adapter = adapter;
    }
}
