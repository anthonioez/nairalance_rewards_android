package com.nairalance.rewards.android.modules.profile.controllers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.miciniti.library.Utils;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.io.Server;
import com.miciniti.library.objects.ControllerActivity;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.modules.profile.objects.AsyncReadImage;
import com.nairalance.rewards.android.views.RewardsInput;
import com.nairalance.rewards.android.modules.profile.objects.SearchUsername;
import com.nairalance.rewards.android.modules.profile.socket.SocketJoin;
import com.miciniti.library.controls.LoadingDialog;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ControllerJoin extends ControllerActivity implements View.OnClickListener, SearchUsername.SearchUsernameListener, PopupMenu.OnMenuItemClickListener, SocketJoin.SocketJoinCallback, AsyncReadImage.AsyncReadImageListener
{
    private static String TAG = ControllerJoin.class.getSimpleName();

    private static final int REQUEST_PERM_FILE      = 1000;
    private static final int REQUEST_PERM_CAMERA    = 1001;
    private static final int REQUEST_FILE           = 1002;
    private static final int REQUEST_CAMERA         = 1003;

    private LinearLayout layoutMain;
    private LinearLayout layoutOverlay;
    private ProgressBar progressBar;

    private CircleImageView imageCover;
    private ImageButton buttonCover;

    private RewardsInput inputUsername;
    private RewardsInput inputReferral;

    private Button buttonNext;

    private Handler handler = new Handler();
    private SearchUsername searchUser = null;
    private SearchUsername searchReferral = null;

    private byte[] imageData = null;
    private Bitmap imageBitmap = null;
    private SocketJoin task = null;
    private File cameraFile;
    private boolean isReady = false;

    public ControllerJoin(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        isReady = false;

        activity.setContentView(R.layout.controller_join);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        ActionBar actionBar = setSupportActionBar(toolbar);
        actionBar.setTitle("Create profile");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        layoutMain = activity.findViewById(R.id.layoutMain);
        UI.overrideFonts(activity, layoutMain, Rewards.appFont);

        layoutOverlay = activity.findViewById(R.id.layoutOverlay);


        imageCover      = activity.findViewById(R.id.imageCover);
        buttonCover     = activity.findViewById(R.id.buttonCover);
        buttonCover.setOnClickListener(this);


        inputUsername = activity.findViewById(R.id.inputUsername);
        inputUsername.getEditText().setFilters(UI.usernameFilter());

        searchUser = new SearchUsername(activity, inputUsername, true, this);
        inputUsername.getEditText().addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(!isReady) return;
                searchUser.start(s.toString().trim());
            }
        });

        inputReferral = activity.findViewById(R.id.inputReferral);
        inputReferral.getEditText().setFilters(UI.usernameFilter());

        searchReferral = new SearchUsername(activity, inputReferral, false, this);

        inputReferral.getEditText().addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                if(!isReady) return;

                String text = s.toString().trim();
                if(text.isEmpty())
                {
                    inputReferral.getSuffixProgress().setVisibility(View.INVISIBLE);
                    inputReferral.getSuffixImage().setVisibility(View.INVISIBLE);
                    inputReferral.getSuffixImage().setVisibility(View.VISIBLE);
                    inputReferral.getSuffixImage().setImageResource(R.drawable.ic_help_gray);
                    inputReferral.getSuffixImage().setOnClickListener(ControllerJoin.this);
                }
                else
                {
                    inputReferral.getSuffixImage().setOnClickListener(null);
                    inputReferral.getSuffixImage().setVisibility(View.INVISIBLE);

                    searchReferral.start(s.toString().trim());
                }

            }
        });

        inputReferral.getSuffixImage().setVisibility(View.VISIBLE);
        inputReferral.getSuffixImage().setImageResource(R.drawable.ic_help_gray);
        inputReferral.getSuffixImage().setOnClickListener(this);

        buttonNext  = activity.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(this);

        progressBar = activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        inputUsername.setInput(Prefs.getUsername(activity));

        nextState(false);
        referralState(false);

        isReady = true;
    }

    @Override
    public void destroy()
    {
        unjoin();
        searchUser.stop();
        searchReferral.stop();
        if(cameraFile != null && cameraFile.exists()) cameraFile.delete();
    }

    public void createMenu(Menu menu)
    {
        //activity.getMenuInflater().inflate(R.menu.activity_videos, menu);

    }

    public boolean prepareMenu(Menu menu)
    {
        return false;
    }

    public boolean selectMenu(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home:
                //activity.finish();
                return true;
        }

        return false;
    }

    @Override
    public void searchDone(SearchUsername su, boolean found)
    {
        if(su == searchUser)
        {
            nextState(!found);
            referralState(!found);
        }
        else if(su == searchReferral)
        {
            nextState(found);
        }
    }

    @Override
    public void searchFailed(SearchUsername su, String error)
    {
        UI.snack(layoutMain, error);

        nextState(true);
        referralState(true);
    }

    @Override
    public void activityResult(int requestCode, int resultCode, Intent data)
    {
        super.activityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Uri uri = data.getData();
                if(uri != null)
                {
                    Utils.startTask(new AsyncReadImage(activity, uri, this));
                }
                else
                {
                    UI.toast(activity, "File not available!");
                }
            }
        }
        else if (requestCode == REQUEST_CAMERA)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if(cameraFile != null && cameraFile.exists())
                {
                    Utils.startTask(new AsyncReadImage(activity, cameraFile, this));
                }
                else
                {
                    UI.toast(activity, activity.getString(R.string.no_picture_taken));
                }

            }
        }
    }


    @Override
    public void permissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.permissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case REQUEST_PERM_FILE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startPhoto();
                }
                else
                {
                    UI.alert(activity, Rewards.appName, activity.getString(R.string.storate_permission_denied), null);
                }
                break;
            }
            case REQUEST_PERM_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startCamera();
                }
                else
                {
                    UI.alert(activity, Rewards.appName, "Camera permission denied!", null);
                }
                break;
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        if(view == inputReferral.getSuffixImage())
        {
            UI.alert(activity, "Referral code", "Enter the username of the user that invited you.");
        }
        else if(view == buttonNext)
        {
            validate();
        }
        else if(view == buttonCover)
        {
            picture();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_open_camera:
                selectCamera();
                return true;

            case R.id.action_open_photos:
                selectPhoto();
                return true;

            default:
                return false;
        }
    }

    private void referralState(boolean active)
    {
        inputReferral.setEnabled(active);
        inputReferral.setAlpha(active ? 1.0f : 0.5f);
    }

    private void nextState(boolean active)
    {
        buttonNext.setEnabled(active);
        buttonNext.setAlpha(active ? 1.0f : 0.5f);
    }


    public void picture()
    {
        PopupMenu popup = new PopupMenu(activity, imageCover);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.content_join, popup.getMenu());
        popup.setOnMenuItemClickListener(this);
        popup.show();
    }

    public void selectPhoto()
    {
        int status = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (status != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERM_FILE);
        }
        else
        {
            startPhoto();
        }
    }

    public void startPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        try
        {
            activity.startActivityForResult(Intent.createChooser(intent, "Select Photo"), REQUEST_FILE);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            UI.toast(activity, "File picker app not available");
        }
    }

    public void selectCamera()
    {
        int status = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        if (status != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_PERM_CAMERA);
        }
        else
        {
            startCamera();
        }
    }

    public void startCamera()
    {
        cameraFile = Rewards.getCameraFile(activity);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try
        {
            Uri contentUri = FileProvider.getUriForFile(activity, "com.nairalance.rewards.android.provider", cameraFile);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,  contentUri);
            activity.startActivityForResult(intent, REQUEST_CAMERA);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            UI.toast(activity, "Unable to start the camera app!");
        }
    }

    private void validate()
    {
        String username = inputUsername.getInput().trim();
        String referrer = inputReferral.getInput().trim();

        if(username.isEmpty())
        {
            Snackbar.make(layoutMain, R.string.please_username, Snackbar.LENGTH_LONG).show();
            inputUsername.getEditText().requestFocus();
        }
        else if(username.length() > 15)
        {
            Snackbar.make(layoutMain, R.string.please_valid_username, Snackbar.LENGTH_LONG).show();
            inputUsername.getEditText().requestFocus();
        }
        else if(!referrer.isEmpty() && referrer.length() > 15)
        {
            Snackbar.make(layoutMain, R.string.please_valid_referrer, Snackbar.LENGTH_LONG).show();
            inputReferral.getEditText().requestFocus();
        }
        else if (!Server.isOnline(activity))
        {
            UI.alert(activity, Rewards.appName, activity.getString(R.string.err_no_connection));
        }
        else
        {
            join(username, referrer);
        }
    }

    public void unjoin()
    {
        if (task != null)
        {
            task.setCallback(null);
            task = null;
        }
    }

    public void join(String username, String referrer)
    {
        unjoin();

        if(imageData != null)
        {
            String encImage = Base64.encodeToString(imageData, Base64.DEFAULT);
            if(encImage.isEmpty())
            {

            }
        }
        else
        {
            imageData = new byte[]{};
        }

        task = new SocketJoin(activity, this);
        task.start(username, referrer, imageData);
    }

    private void joinUI(boolean disable)
    {
        layoutOverlay.setVisibility(disable ? View.VISIBLE : View.GONE);

        inputUsername.setEnabled(!disable);
        inputReferral.setEnabled(!disable);

        buttonNext.setEnabled(!disable);

        progressBar.setVisibility(disable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void joinStarted()
    {
        joinUI(true);
    }

    @Override
    public void joinSuccess(String phone)
    {
        joinUI(false);

        Rewards.startMain(activity);

        activity.finish();
    }

    @Override
    public void joinError(String error)
    {
        joinUI(false);

        UI.alert(activity, Rewards.appName, error);
    }

    @Override
    public void imageStarted()
    {
        LoadingDialog.show(activity, "Please wait...");
    }

    @Override
    public void imageDone(Bitmap bitmap, byte[] data)
    {
        imageCover.setImageBitmap(null);
        imageCover.setImageBitmap(imageBitmap);
    }

    @Override
    public void imageFailed(String error)
    {
        imageCover.setImageBitmap(null);

        imageData = null;
    }
}
