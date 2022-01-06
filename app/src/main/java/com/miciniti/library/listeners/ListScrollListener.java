package com.miciniti.library.listeners;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public abstract class ListScrollListener implements OnScrollListener
{
    private static final String TAG = ListScrollListener.class.getSimpleName();

    // The minimum points of items to have below your current scroll position
    // before loading more.
    private int     visibleThreshold        = 10;
    private int     loadDelay               = 3000;

    // True if we are still waiting for the last set of data to load.
    private boolean loading                = true;

    private boolean canMore                = true;

    private int     mLastFirstVisibleItem   = -1;
    private int     mScrolling              = 0;
    private long    lasttime                = 0;

    public ListScrollListener(int visibleThreshold, int delay)
    {
        this.visibleThreshold = visibleThreshold;
        this.loadDelay = delay;
    }

    public void setReady() { this.loading = false; }

    //public boolean isLoading(){ return  loading; }

    public void setDone(boolean done)
    {
        this.canMore = !done;
    }

    public void setMorer(boolean more)
    {
        this.canMore = more;
    }

    public boolean canMore(){ return  canMore; }

    // This happens many times a second during a scroll, so be wary of the code
    // you place here.
    // We are given a few useful parameters to help us work out if we need to
    // load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if(firstVisibleItem == 0 && totalItemCount > 0)
        {
            onFirst();
        }

        if(mLastFirstVisibleItem == -1)
            mScrolling = 0;
        else if (firstVisibleItem > mLastFirstVisibleItem)
            mScrolling = 1;
        else if (firstVisibleItem < mLastFirstVisibleItem)
            mScrolling = -1;
        //else
            //mScrolling = 0;

        //Logger.i(TAG, "scroll: " + mScrolling );

        mLastFirstVisibleItem = firstVisibleItem;

        // If it isnt currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to
        // fetch the data.
        //if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
        int actualThreshold = (firstVisibleItem + visibleItemCount + visibleThreshold);
        if (!loading && (actualThreshold > totalItemCount) && mScrolling == 1)
        {
            long now = System.currentTimeMillis();
            if(lasttime == 0 || (now - lasttime) > loadDelay)
            {

                //Logger.i(TAG, "more?");
                if(canMore)
                {
                    onMore(actualThreshold);
                }

                //mLastFirstVisibleItem = -1;
                mScrolling = 0;
                loading = true;
                lasttime = now;
            }
        }

    }


    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
    }

    public abstract void onFirst();

    public abstract void onMore(int actualThreshold);
}