package com.miciniti.library.adapters.SeparatedList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Miciniti on 26/12/2016.
 */

public class SectionBaseAdapter extends BaseAdapter
{
    protected int[]    ids;

    public SectionBaseAdapter()
    {
    }

    @Override
    public int getCount()
    {
        return (ids == null ? 0 : ids.length);
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        return null;
    }

}

