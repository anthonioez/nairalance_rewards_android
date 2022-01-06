package com.nairalance.rewards.android.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.friends.controllers.ControllerFriends;

public class ActivityFriends extends AppCompatActivity implements ControllerListener
{
    private ControllerFriends friendsController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        friendsController = new ControllerFriends(this);
        friendsController.create(savedInstanceState);
    }


    @Override
    public void onBackPressed()
    {
        if(!friendsController.backPressed())
        {
            super.onBackPressed();
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        friendsController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        friendsController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        friendsController.destroy();
    }

    @Override
    public ControllerActivity getController()
    {
        return friendsController;
    }
}
