package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.start.controllers.ControllerMaintain;


public class ActivityMaintain extends AppCompatActivity implements ControllerListener
{
    private ControllerMaintain maintainController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        maintainController = new ControllerMaintain(this);
        maintainController.create(savedInstanceState);
    }

    @Override
    public void onResume() {

        super.onResume();

        maintainController.resume();
    }


    @Override
    public void onBackPressed()
    {
        if(!maintainController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {

        super.onPause();

        maintainController.pause();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        maintainController.destroy();
    }

    @Override
    public ControllerActivity getController()
    {
        return maintainController;
    }
}
