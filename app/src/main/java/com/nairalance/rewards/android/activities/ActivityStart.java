package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.start.controllers.ControllerStart;

public class ActivityStart extends AppCompatActivity implements ControllerListener
{
    private ControllerStart startController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        startController = new ControllerStart(this);
        startController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        startController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        startController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        startController.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        startController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public ControllerActivity getController()
    {
        return startController;
    }
}
