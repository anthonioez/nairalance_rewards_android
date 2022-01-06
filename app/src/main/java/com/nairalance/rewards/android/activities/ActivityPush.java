package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.nairalance.rewards.android.Rewards;

public class ActivityPush extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(isTaskRoot())
        {
            Intent intent = getIntent();
            intent.setClass(this, ActivityHome.class);
            startActivity(intent);

            finish();
        }
        else
        {
            Intent intent = getIntent();
            Rewards.processIntent(this, intent);
            finish();
        }
    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);


    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
