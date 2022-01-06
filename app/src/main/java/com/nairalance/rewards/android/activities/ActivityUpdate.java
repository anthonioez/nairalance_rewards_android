package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;

import com.nairalance.rewards.android.modules.start.controllers.ControllerUpdate;

public class ActivityUpdate extends AppCompatActivity implements ControllerListener
{
    private ControllerUpdate updateController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        updateController = new ControllerUpdate(this);
        updateController.create(savedInstanceState);
    }


    @Override
    public void onBackPressed()
    {
        if(!updateController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        updateController.resume();
    }

    @Override
    public void onPause() {

        super.onPause();

        updateController.pause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        updateController.destroy();
    }

    @Override
    public ControllerActivity getController()
    {
        return updateController;
    }
}
