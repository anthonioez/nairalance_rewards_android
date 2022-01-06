package com.nairalance.rewards.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.miciniti.library.objects.ControllerActivity;
import com.miciniti.library.objects.ControllerListener;
import com.nairalance.rewards.android.modules.help.controllers.ControllerFaq;

public class ActivityFaq extends AppCompatActivity implements ControllerListener
{
    private ControllerFaq faqController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        faqController = new ControllerFaq(this);
        faqController.create(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        faqController.resume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        faqController.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        faqController.destroy();
    }


    @Override
    public void onBackPressed()
    {
        if(!faqController.backPressed())
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        faqController.createMenu(menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(faqController.prepareMenu(menu))
            return true;

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(faqController.selectMenu(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        faqController.activityResult(requestCode, resultCode, data);
    }

    @Override
    public ControllerActivity getController()
    {
        return faqController;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        faqController.saveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        faqController.restoreInstanceState(savedInstanceState);
    }
}
