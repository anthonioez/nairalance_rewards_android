package com.nairalance.rewards.android.modules.profile.objects;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.views.RewardsInput;
import com.nairalance.rewards.android.modules.profile.socket.SocketUsername;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Miciniti on 06/05/2018.
 */

public class SearchUsername implements SocketUsername.SocketUsernameCallback
{
    private Context context;
    private SearchUsernameListener listener;
    private RewardsInput input;
    public String last = "";
    private boolean existing = false;

    private Timer timer = null;
    private SocketUsername socket = null;
    private Handler handler;
    public String allowed = null;

    public SearchUsername(Context context, RewardsInput input, boolean existing, SearchUsernameListener listener)
    {
        this.context = context;
        this.listener = listener;
        this.input = input;
        this.existing = existing;

        this.handler = new Handler();
    }

    public void start(String text)
    {
        final String qq = text.trim().toLowerCase();

        reset();

        if(last.equals(qq))
        {
            return;
        }
        else if(qq.length() < 3)
        {
            usernameError("");
            return;
        }
        else
        {
            stop();

            timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            socket(qq);
                        }
                    });
                }
            }, 1000);
        }
    }

    public void reset()
    {
        input.getSuffixProgress().setVisibility(View.INVISIBLE);
        input.getSuffixImage().setVisibility(View.INVISIBLE);
    }

    public void stop()
    {
        reset();

        untimer();
        unsocket();
    }

    private void untimer()
    {
        if (timer != null)
        {
            try{ timer.cancel(); timer.purge(); }
            catch (Exception e){}

            timer = null;
        }
    }

    private void unsocket()
    {
        if(socket != null)
        {
            socket.stop();
            socket.setCallback(null);
            socket = null;
        }
    }

    public void socket(String username)
    {
        unsocket();

        socket = new SocketUsername(context, this);
        socket.start(username, existing);
    }

    @Override
    public void usernameStarted()
    {
        input.getSuffixProgress().setVisibility(View.VISIBLE);
        input.getSuffixImage().setVisibility(View.INVISIBLE);
    }

    @Override
    public void usernameSuccess(String username, boolean data)
    {
        last = username;

        input.getSuffixProgress().setVisibility(View.INVISIBLE);
        input.getSuffixImage().setVisibility(View.VISIBLE);

        done(data);
    }

    public void done(boolean data)
    {
        if(existing)
        {
            input.getSuffixImage().setImageResource(data ? R.drawable.ic_close_red : R.drawable.ic_done_green);
        }
        else
        {
            input.getSuffixImage().setImageResource(data ? R.drawable.ic_done_green : R.drawable.ic_close_red);
        }

        if(listener != null) listener.searchDone(this, data);
     }

    @Override
    public void usernameError(String error)
    {
        input.getSuffixProgress().setVisibility(View.INVISIBLE);
        input.getSuffixImage().setVisibility(View.INVISIBLE);

        if(listener != null) listener.searchFailed(this, error);
    }

    public interface SearchUsernameListener
    {
        void searchDone(SearchUsername su, boolean found);
        void searchFailed(SearchUsername su, String error);
    }
}

