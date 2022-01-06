package com.nairalance.rewards.android.modules.start.controllers;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.ybq.android.spinkit.SpinKitView;
import com.miciniti.library.Utils;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.io.ServerSocket;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.activities.ActivityJoin;
import com.nairalance.rewards.android.activities.ActivityPhone;
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.fragments.FragmentSlide;
import com.nairalance.rewards.android.helpers.Device;
import com.nairalance.rewards.android.modules.start.objects.IntroItem;
import com.nairalance.rewards.android.modules.start.socket.SocketStart;

import java.util.ArrayList;

public class ControllerStart extends ControllerActivity implements SocketStart.SocketSplashCallback, View.OnClickListener, ViewPager.OnPageChangeListener
{
    private static final int REQUEST_SIGNIN = 323;

    private LinearLayout layoutMain;
    private LinearLayout layoutSplash;
    private SpinKitView spinKit;

    private LinearLayout layoutIntro;
    private ViewPager viewPager;
    private LinearLayout layoutDot;

    private LinearLayout layoutStart;

    private Button buttonStart;

    private ArrayList<IntroItem> pages = new ArrayList<>();
    private ArrayList<ImageView> pageDots = new ArrayList<>();;

    private SocketStart task;

    private Handler handler = new Handler();

    private StartViewPagerAdapter adapter;

    public ControllerStart(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        activity.setContentView(R.layout.controller_start);


        layoutMain = activity.findViewById(R.id.layoutMain);
        UI.overrideFonts(activity, layoutMain, Rewards.appFont);

        layoutSplash = activity.findViewById(R.id.layoutSplash);
        layoutSplash.setVisibility(View.VISIBLE);

        spinKit = activity.findViewById(R.id.spinKit);
        spinKit.setVisibility(View.INVISIBLE);

        layoutIntro = activity.findViewById(R.id.layoutIntro);
        layoutIntro.setVisibility(View.GONE);

        layoutDot   = activity.findViewById(R.id.layoutDot);
        viewPager   = activity.findViewById(R.id.vpPager);

        layoutStart = activity.findViewById(R.id.layoutStart);
        layoutStart.setVisibility(View.GONE);

        buttonStart = activity.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(this);


        adapter = new StartViewPagerAdapter(getAppCompactActivity().getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
        resetSlides();

        Ad.makePossible(activity);

        String action = activity.getIntent().getAction();
        if(action != null && action.equals(Intent.ACTION_VIEW))
        {
            preStart();
        }
        else
        {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    preStart();
                }
            }, 2000);
        }
    }

    @Override
    public void activityResult(int requestCode, int resultCode, Intent data)
    {
        super.activityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SIGNIN)
        {
            if(resultCode == activity.RESULT_OK)
            {
                activity.finish();
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view == buttonStart)
        {
            startSignin();
       }
    }

    @Override
    public void destroy()
    {
        unload();
    }

    private void preStart()
    {
        if(ServerSocket.isConnected() && ServerSocket.isRegistered())
        {
            startApp();
        }
        else
        {
            load();
        }
    }

    private void startApp()
    {
        if (Rewards.isUser(activity))
        {
            if(TextUtils.isEmpty(Prefs.getUsername(activity)))
            {
                startJoin();
            }
            else
            {
                startMain();
            }
        }
        else
        {
            if (Prefs.getRunCount(activity) == 0)
            {
                showSlide();
            }
            else
            {
                showStart();
            }
        }
    }

    private void startSignin()
    {
        Intent intent = new Intent(activity, ActivityPhone.class);
        activity.startActivity(intent);

        activity.finish();
    }


    private void startJoin()
    {
        Intent intent = new Intent(activity, ActivityJoin.class);
        activity.startActivity(intent);

        activity.finish();
    }

    private void startMain()
    {
        Rewards.startMain(activity);

        activity.finish();
        //activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void resetSlides()
    {
        pages.clear();
        pages.add(new IntroItem(R.drawable.logo_white,          R.string.intro_welcome,     R.string.intro_welcome_desc,    0, null));
        pages.add(new IntroItem(R.drawable.intro_invite,        R.string.intro_invite,      R.string.intro_invite_desc,     0, null));
        pages.add(new IntroItem(R.drawable.intro_play,          R.string.intro_play,        R.string.intro_play_desc,       0, null));

        pages.add(new IntroItem(R.drawable.intro_earn,          R.string.intro_earn,        R.string.intro_earn_desc,       0, null));
        pages.add(new IntroItem(R.drawable.intro_cashout,       R.string.intro_cashout,     R.string.intro_cashout_desc,    0, null));
        pages.add(new IntroItem(R.drawable.logo_white,          R.string.intro_thanks,      R.string.blank,                 0, null)); //R.string.intro_click_start, null));

        adapter.notifyDataSetChanged();

        layoutDot.removeAllViews();

        int size = (int) Utils.convertDpToPixel(12);
        int spacing = (int) Utils.convertDpToPixel(8);

        pageDots.clear();
        for(int i = 0; i < adapter.getCount(); i++)
        {
            ImageView iv = new ImageView(activity);
            iv.setImageResource(i == 0 ? R.drawable.ic_circle_white : R.drawable.ic_circle_gray);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(spacing/2, 0, spacing/2, 0);
            layoutDot.addView(iv, params);
            pageDots.add(iv);
        }
    }

    public void slideAction(int page)
    {
        showStart();
    }

    private void showSlide()
    {
        //UI.collapse(layoutStart, 750);
        layoutStart.setVisibility(View.GONE);   //fade out
        //layoutSplash.setVisibility(View.GONE);   //fade out

        UI.fadeOut(layoutSplash, 200);

        UI.fadeIn(layoutIntro, 1500);
    }

    private void showStart()
    {
//        layoutIntro.setVisibility(View.GONE);
        //UI.fadeOut(layoutIntro, 500);

        //UI.fadeIn(layoutSplash, 750);

        UI.expand(layoutStart, 750);
    }

    private void unload()
    {
        if (task != null)
        {
            task.setCallback(null);
            task.stop();
            task = null;
        }
    }

    private void load()
    {
        task = new SocketStart(activity, this);
        task.start();
    }

    @Override
    public void splashStarted()
    {
        spinKit.setVisibility(View.VISIBLE);
    }

    @Override
    public void splashSuccess(int update, String msg)
    {
        spinKit.setVisibility(View.INVISIBLE);

        Device.update(activity, update, msg, new Runnable()
        {
            @Override
            public void run()
            {
                startApp();
            }
        });
    }

    @Override
    public void splashError(String error)
    {
        spinKit.setVisibility(View.INVISIBLE);

        error = TextUtils.isEmpty(error) ? getString(R.string.err_occurred) : error;

        UI.alert(activity, Rewards.appName, error, new Runnable()
        {
            @Override
            public void run()
            {
                startApp();
            }
        });

        //Rewards.registerForPush(activity);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        for(int i = 0; i < pageDots.size(); i++)
        {
            if(i == position)
                pageDots.get(i).setImageResource(R.drawable.ic_circle_white);
            else
                pageDots.get(i).setImageResource(R.drawable.ic_circle_gray);
        }
    }

    @Override
    public void onPageSelected(int position)
    {
        if(position == pages.size() - 1)
        {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    layoutDot.setVisibility(View.GONE);

                    Prefs.setRunCount(activity, 1);

                    UI.expand(layoutStart, 500);
                }
            }, 1000);
        }
        else
        {
            if(layoutStart.getVisibility() == View.VISIBLE) {
                UI.collapse(layoutStart, 200);
            }

            //UI.fadeIn(layoutDot, 750);
            layoutDot.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    public class StartViewPagerAdapter extends FragmentPagerAdapter
    {
        public StartViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position) {
                default:
                    if(pages.get(position).fragment == null)
                        pages.get(position).fragment = FragmentSlide.newInstance(pages.get(position));
                    return pages.get(position).fragment;
            }
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return getString(pages.get(position).title);
        }
    }

}
