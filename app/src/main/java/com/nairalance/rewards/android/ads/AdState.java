package com.nairalance.rewards.android.ads;

/**
 * Created by Miciniti on 17/09/2017.
 */

public class AdState
{
    public enum State {
        FAILED,
        LOADING,
        LOADED
    }

    public Object   ad;
    public int      type;
    public State    state;
    public long     time;
    public String   admobId;

    public AdStateListener listener;

    public AdState(Object ad, int type, String admobId, State state, AdStateListener listener)
    {
        this.ad = ad;
        this.type = type;
        this.admobId = admobId;
        this.state = state;
        this.listener = listener;
        this.time = System.currentTimeMillis();
    }

    public boolean isLoading()
    {
        return state == State.LOADING;
    }

    public boolean isLoaded()
    {
        return state == State.LOADED;
    }

    public boolean isFailed()
    {
        return state == State.FAILED;
    }

    public boolean canReload()
    {
        if(isFailed() && (System.currentTimeMillis() - time) > 30000)
        {
            return true;
        }
        return false;
    }

    public interface AdStateListener
    {
        void onAdState(State state);
    }
}
