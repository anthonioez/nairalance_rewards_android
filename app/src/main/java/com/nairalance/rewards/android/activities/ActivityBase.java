package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityBase extends AppCompatActivity
{
    private static final String TAG = ActivityBase.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();


    }

    @Override
    public void onResume()
    {
        super.onResume();

        //interShow();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }



}
