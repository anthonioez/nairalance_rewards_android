package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.features.controllers.ControllerFeature;

public class ActivityFeatures extends AppCompatActivity implements ControllerListener
{
    private ControllerFeature featuresController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        featuresController = new ControllerFeature(this);
        featuresController.create(savedInstanceState);
    }


    @Override
    public void onBackPressed()
    {
        if(!featuresController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        featuresController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        featuresController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        featuresController.destroy();
    }

    @Override
    public ControllerActivity getController()
    {
        return featuresController;
    }
}
