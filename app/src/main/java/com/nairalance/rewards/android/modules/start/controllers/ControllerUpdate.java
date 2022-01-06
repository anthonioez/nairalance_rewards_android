package com.nairalance.rewards.android.modules.start.controllers;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miciniti.library.Links;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.ControllerActivity;

import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Strings;

/**
 * Created by Miciniti onEvent 26/09/16.
 */
public class ControllerUpdate extends ControllerActivity implements View.OnClickListener
{
    private static final String TAG = ControllerUpdate.class.getSimpleName();

    private TextView    textTitle;
    private Button buttonUpdate;
    private FrameLayout layoutMain;

    public ControllerUpdate(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        super.create(savedInstanceState);

        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        activity.setContentView(R.layout.controller_update);

        layoutMain = activity.findViewById(R.id.layoutMain);
        UI.overrideFonts(activity, layoutMain, Rewards.appFont);

        textTitle = (TextView) activity.findViewById(R.id.textTitle);
        textTitle.setTypeface(Rewards.appFontLight);

        buttonUpdate = (Button) activity.findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);

        String text = activity.getIntent().getStringExtra(Strings.message);
        if(!TextUtils.isEmpty(text))
        {
            textTitle.setText(Html.fromHtml(text));
        }

    }


    @Override
    public void resume()
    {
        super.resume();
    }

    @Override
    public void destroy()
    {

    }

    @Override
    public void pause()
    {
        super.pause();
    }

    @Override
    public void onClick(View view)
    {
        if(view == buttonUpdate)
        {
            Links.openStoreUrl(activity);

            activity.finish();
        }
    }

}
