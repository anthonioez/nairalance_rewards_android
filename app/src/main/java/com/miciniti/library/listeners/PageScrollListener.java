package com.miciniti.library.listeners;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class PageScrollListener implements OnScrollListener
{
    private int visibleThreshold = 10;
    private long lastLoad = 0;

    public PageScrollListener(int visibleThreshold)
    {
        this.visibleThreshold = visibleThreshold;

        lastLoad  = System.currentTimeMillis();
    }

    public PageScrollListener(int visibleThreshold, int startPage)
    {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        onFirst(firstVisibleItem == 0);

        if(totalItemCount > 0)
        {
            int lastItem = (firstVisibleItem + visibleItemCount);
            if (totalItemCount - lastItem <= visibleThreshold)
            {
                long now = System.currentTimeMillis();
                if ((now - lastLoad) >= 2000)
                {
                    //Logger.i(TAG, "onScroll more");
                    
                    onMore(lastItem);
                    
                    lastLoad = now;
                }
            }
        }        
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {

    }

    public abstract void onFirst(boolean yes);

    // Defines the process for actually loading more data based onEvent page
    public abstract void onMore(int index);
}