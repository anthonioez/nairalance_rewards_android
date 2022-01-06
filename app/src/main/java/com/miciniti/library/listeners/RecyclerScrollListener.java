package com.miciniti.library.listeners;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerScrollListener extends RecyclerView.OnScrollListener
{
    public static String TAG = RecyclerScrollListener.class.getSimpleName();

    private int     visibleThreshold        = 10;
    private int     loadDelay               = 3000;

    // True if we are still waiting for the last set of data to load.
    private boolean loading                = true;

    private boolean canMore                = true;

    private long    lasttime                = 0;

    private LinearLayoutManager mLinearLayoutManager;

    public RecyclerScrollListener(LinearLayoutManager linearLayoutManager, int visibleThreshold, int delay)
    {
        this.mLinearLayoutManager = linearLayoutManager;

        this.visibleThreshold = visibleThreshold;
        this.loadDelay = delay;
    }

    public void setReady() { this.loading = false; }

    //public boolean isLoading(){ return  loading; }

    public void setMor(boolean more)
    {
        this.canMore = more;
    }

    public void setDone(boolean done)
    {
        this.canMore = !done;
    }

    public boolean canMore(){ return  canMore; }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        onFirst(firstVisibleItem == 0);

        //Logger.i(TAG, "scroll: dy: " + dy );

        // If it isnt currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to
        // fetch the data.
        //if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold))
        int actualThreshold = (firstVisibleItem + visibleItemCount + visibleThreshold);
        if (!loading && (actualThreshold > totalItemCount) && dy > 0)
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
                loading = true;
                lasttime = now;
            }
        }
    }

    public abstract void onFirst(boolean yes);

    public abstract void onMore(int actualThreshold);

}