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
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.nairalance.rewards.android.ads.Ad;
import com.nairalance.rewards.android.ads.AdmobInterst;
import com.nairalance.rewards.android.modules.profile.objects.AsyncReadImage;
import com.nairalance.rewards.android.modules.profile.socket.SocketProfileEmail;
import com.nairalance.rewards.android.modules.profile.socket.SocketProfileVerify;
import com.nairalance.rewards.android.views.RewardsDigits;
import com.nairalance.rewards.android.views.RewardsInput;
import com.nairalance.rewards.android.helpers.Image;
import com.nairalance.rewards.android.modules.profile.objects.ProfileItem;
import com.nairalance.rewards.android.modules.profile.socket.SocketProfileGet;
import com.nairalance.rewards.android.modules.profile.socket.SocketProfileSet;
import com.miciniti.library.controls.LoadingDialog;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class ControllerProfile extends ControllerActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, SocketProfileSet.SocketProfileSetCallback, SocketProfileGet.SocketJoinCallback, RewardsInput.OnPickerChangedListener, RewardsInput.OnInputChangedListener, AdmobInterst.AdInterstListener, AsyncReadImage.AsyncReadImageListener, SocketProfileEmail.SocketProfileEmailCallback, SocketProfileVerify.SocketProfileVerifyCallback
{
    private static String TAG = ControllerProfile.class.getSimpleName();

    private static final int REQUEST_PERM_FILE      = 1000;
    private static final int REQUEST_PERM_CAMERA    = 1001;
    private static final int REQUEST_FILE           = 1002;
    private static final int REQUEST_CAMERA         = 1003;

    private LinearLayout layoutMain;
    private LinearLayout layoutOverlay;
    private ProgressBar progressBar;

    private CircleImageView imageCover;
    private ImageButton buttonCover;

    private LinearLayout layoutEmail;
    private RewardsInput inputEmail;
    private RewardsInput inputPassword;
    private RewardsInput inputPassword2;
    private Button buttonEmail;

    private LinearLayout layoutVerify;
    private RewardsDigits inputCode;
    private Button buttonVerify;

    private LinearLayout layoutBio;
    private RewardsInput inputUsername;
    private RewardsInput inputPhone;
    private RewardsInput inputGender;
    private RewardsInput inputCity;
    private RewardsInput inputState;

    private Button buttonUpdate;

    private byte[] imageData = null;
    private File cameraFile;

    private Handler handler = new Handler();
    private SocketProfileSet taskSet = null;
    private SocketProfileGet taskGet = null;
    private SocketProfileEmail taskEmail = null;
    private SocketProfileVerify taskVerify = null;

    private MenuItem menuReload = null;
    private AdmobInterst adInterst = null;
    private ActionBar actionBar;

    public ControllerProfile(AppCompatActivity activity)
    {
        super(activity);
    }

    public void create(Bundle savedInstanceState)
    {
        activity.setContentView(R.layout.controller_profile);

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        UI.overrideFonts(activity, toolbar, Rewards.appFontBold);

        actionBar = setSupportActionBar(toolbar);
        actionBar.setTitle("Edit Profile");
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        layoutMain = activity.findViewById(R.id.layoutMain);
        UI.overrideFonts(activity, layoutMain, Rewards.appFont);

        layoutOverlay = activity.findViewById(R.id.layoutOverlay);

        imageCover      = activity.findViewById(R.id.imageCover);
        buttonCover     = activity.findViewById(R.id.buttonCover);
        buttonCover.setOnClickListener(this);

        layoutEmail = activity.findViewById(R.id.layoutEmail);
        inputEmail = activity.findViewById(R.id.inputEmail);
        inputEmail.setOnInputChanged(this);

        inputPassword = activity.findViewById(R.id.inputPassword);
        inputPassword.setOnInputChanged(this);

        inputPassword2 = activity.findViewById(R.id.inputPassword2);
        inputPassword2.setOnInputChanged(this);

        buttonEmail  = activity.findViewById(R.id.buttonEmail);
        buttonEmail.setOnClickListener(this);

        layoutVerify = activity.findViewById(R.id.layoutVerify);
        inputCode = activity.findViewById(R.id.inputCode);
        inputCode.getEditText().addTextChangedListener(new TextWatcher()
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
                boolean active = inputCode.getCode().trim().length() == 4;
                buttonVerify.setEnabled(active);
                buttonVerify.setAlpha(active ? 1.0f : 0.5f);
            }
        });

        buttonVerify  = activity.findViewById(R.id.buttonVerify);
        buttonVerify.setOnClickListener(this);

        layoutBio = activity.findViewById(R.id.layoutBio);
        inputUsername = activity.findViewById(R.id.inputUsername);
        inputUsername.setOnInputChanged(this);

        inputPhone = activity.findViewById(R.id.inputPhone);
        inputPhone.setOnInputChanged(this);

        inputGender = activity.findViewById(R.id.inputGender);
        inputGender.setList(activity.getResources().getStringArray(R.array.gender));
        inputGender.setOnPickerChanged(this);

        inputCity = activity.findViewById(R.id.inputCity);
        inputCity.setOnInputChanged(this);

        inputState = activity.findViewById(R.id.inputState);
        inputState.setList(activity.getResources().getStringArray(R.array.states));
        inputState.setOnPickerChanged(this);

        buttonUpdate  = activity.findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);

        progressBar = activity.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        inputUsername.setEnabled(false);
        inputPhone.setEnabled(false);

        inputUsername.setInput(Prefs.getUsername(activity));
        Image.loadFull(activity, Prefs.getThumbUrl(activity), 0, imageCover, false, null);

        updateState();

        showSection(null);

        get();

        Ad.count(activity, 1);

        adInterst = new AdmobInterst(activity, activity.getString(R.string.admob_inter));
        if(Ad.isPossible(activity))
        {
            adInterst.load();
        }
    }

    @Override
    public void resume()
    {
        if(Ad.isPossible(activity) && adInterst.isFailed())
        {
            adInterst.load();
        }
    }

    @Override
    public void destroy()
    {
        unset();
        unget();
        unmail();
        unverify();

        if(cameraFile != null && cameraFile.exists()) cameraFile.delete();
    }

    public boolean backPressed()
    {
        if(adInterst.isLoaded())
        {
            adInterst.show(this);
            return true;
        }

        return false;
    }

    public void createMenu(Menu menu)
    {
        activity.getMenuInflater().inflate(R.menu.controller_profile, menu);

    }

    public boolean prepareMenu(Menu menu)
    {
        menuReload = menu.findItem(R.id.action_reload);
        return false;
    }

    public boolean selectMenu(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home:
                adInterst.showOrFinish(activity, this);
                return true;

            case R.id.action_reload:
                get();
                break;
        }

        return false;
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
                    UI.toast(activity, "No picture taken!");
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
                    UI.alert(activity, Rewards.appName, "Storage permission denied!", null);
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
        if(view == buttonEmail)
        {
            validateEmail();
        }
        else if(view == buttonVerify)
        {
            validateCode();
        }
        else if(view == buttonUpdate)
        {
            validateBio();
        }
        else if(view == buttonCover)
        {
            picture();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_open_camera:
                selectCamera();
                return true;

            case R.id.action_open_photos:
                selectPhoto();
                return true;

            case R.id.action_delete:
                imageData = new byte[]{0};
                imageCover.setImageBitmap(null);
                return true;

            default:
                return false;
        }
    }


    @Override
    public void onInputChanged(RewardsInput input, String text)
    {
        updateState();
    }

    @Override
    public void onPickerChanged(RewardsInput input, String text, int pos)
    {
        updateState();
    }

    @Override
    public void onAdInterstClosed()
    {
        if(activity != null) activity.finish();
    }

    @Override
    public void onAdInterstFailed()
    {
        if(activity != null) activity.finish();
    }


    private void updateState()
    {
        boolean active = false;

        if(layoutEmail.getVisibility() == View.VISIBLE)
        {
            String email = inputEmail.getInput().trim();

            active = email.length() > 0 && Utils.isValidEmail(email) &&
                inputPassword.getInput().trim().length() > 0 &&
                inputPassword2.getInput().trim().length() > 0;

            buttonEmail.setEnabled(active);
            buttonEmail.setAlpha(active ? 1.0f : 0.5f);
        }

        if(layoutVerify.getVisibility() == View.VISIBLE)
        {
            active = inputCode.getCode().trim().length() == 4;

            buttonVerify.setEnabled(active);
            buttonVerify.setAlpha(active ? 1.0f : 0.5f);
        }

        if(layoutBio.getVisibility() == View.VISIBLE)
        {
            active = inputGender.getInput().trim().length() > 0 &&
                inputCity.getInput().trim().length() > 3 &&
                inputState.getInput().trim().length() > 4;

            buttonUpdate.setEnabled(active);
            buttonUpdate.setAlpha(active ? 1.0f : 0.5f);
        }
    }

    public void picture()
    {
        PopupMenu popup = new PopupMenu(activity, imageCover);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.profile_cover, popup.getMenu());
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT,  contentUri);
            activity.startActivityForResult(intent, REQUEST_CAMERA);
        }
        catch (android.content.ActivityNotFoundException ex)
        {
            UI.toast(activity, "Unable to start the camera app!");
        }
    }

    private void showSection(View layout)
    {
        for(int i = 0; i < layoutMain.getChildCount(); i++)
        {
            View v = layoutMain.getChildAt(i);
            if(v == null) continue;

            v.setVisibility(v == layout ? View.VISIBLE : View.GONE);
        }

        if(layout == layoutEmail)
        {
            actionBar.setTitle("Setup Account");
        }
        else if(layout == layoutVerify)
        {
            actionBar.setTitle("Verify Email Address");
        }
        else //if(view == viewProfile)
        {
            actionBar.setTitle("Edit Profile");
        }
    }

    private void validateEmail()
    {
        String email        = inputEmail.getInput().toString().trim();
        String password     = inputPassword.getInput().toString().trim();
        String password2    = inputPassword2.getInput().toString().trim();

        UI.hideKeyboard(activity, layoutMain);

        if(email.isEmpty())
        {
            Snackbar.make(layoutMain, R.string.please_email, Snackbar.LENGTH_LONG).show();
        }
        else if(email.length() < 4 || !Utils.isValidEmail(email))
        {
            Snackbar.make(layoutMain, R.string.please_valid_email, Snackbar.LENGTH_LONG).show();
        }
        else if(password.length() < 8)
        {
            Snackbar.make(layoutMain, R.string.please_valid_password_min, Snackbar.LENGTH_LONG).show();
        }
        else if(password2.length() < 8)
        {
            Snackbar.make(layoutMain, R.string.please_retype_password_min, Snackbar.LENGTH_LONG).show();
        }
        else if(password != password2)
        {
            Snackbar.make(layoutMain, R.string.please_password_mismatch, Snackbar.LENGTH_LONG).show();
        }
        else if(!Server.isOnline(activity))
        {
            UI.alert(activity, Rewards.appName, activity.getString(R.string.err_no_connection));
        }
        else
        {
            mail(email, password);
        }
    }

    private void validateCode()
    {
        String code   = inputCode.getCode().trim();
        UI.hideKeyboard(activity, layoutMain);

        if(code.isEmpty() || code.length() != 4)
        {
            Snackbar.make(layoutMain, R.string.please_verification_code_email, Snackbar.LENGTH_LONG).show();
        }
        else if (!Server.isOnline(activity))
        {
            UI.alert(activity, Rewards.appName, activity.getString(R.string.err_no_connection));
        }
        else
        {
            verify(Prefs.getEmail(activity), code);
        }
    }

    private void validateBio()
    {
        String gender   = inputGender.getInput().toString().trim();
        String city     = inputCity.getInput().toString().trim();
        String state    = inputState.getInput().toString().trim();

        UI.hideKeyboard(activity, layoutMain);

        if(gender.isEmpty())
        {
            Snackbar.make(layoutMain, R.string.please_gender, Snackbar.LENGTH_LONG).show();
            //inputGender.getEditText().requestFocus();
        }
        else if(city.isEmpty())
        {
            Snackbar.make(layoutMain, R.string.please_city, Snackbar.LENGTH_LONG).show();
            //inputUsername.getEditText().requestFocus();
        }
        else if(city.length() > 20)
        {
            Snackbar.make(layoutMain, R.string.please_valid_city, Snackbar.LENGTH_LONG).show();
            //inputCity.getEditText().requestFocus();
        }
        else if(state.isEmpty())
        {
            Snackbar.make(layoutMain, R.string.please_state, Snackbar.LENGTH_LONG).show();
            //inputUsername.getEditText().requestFocus();
        }
        else if (!Server.isOnline(activity))
        {
            UI.alert(activity, Rewards.appName, activity.getString(R.string.err_no_connection));
        }
        else
        {
            set("", gender.equals("Male") ? 1 : 2 , city, state);
        }
    }

    private void unget()
    {
        if (taskGet != null)
        {
            taskGet.setCallback(null);
            taskGet = null;
        }
    }

    private void get()
    {
        unget();

        taskGet = new SocketProfileGet(activity, this);
        taskGet.start();
    }

    public void unmail()
    {
        if (taskEmail != null)
        {
            taskEmail.setCallback(null);
            taskEmail = null;
        }
    }

    public void mail(String email, String password)
    {
        unmail();

        taskEmail = new SocketProfileEmail(activity, this);
        taskEmail.start(email, password);
    }

    public void unverify()
    {
        if (taskVerify != null)
        {
            taskVerify.setCallback(null);
            taskVerify = null;
        }
    }

    public void verify(String email, String code)
    {
        unverify();

        taskVerify = new SocketProfileVerify(activity, this);
        taskVerify.start(email, code);
    }


    public void unset()
    {
        if (taskSet != null)
        {
            taskSet.setCallback(null);
            taskSet = null;
        }
    }

    public void set(String username, int gender, String city, String state)
    {
        unset();

        if(imageData == null) imageData = new byte[]{};

        taskSet = new SocketProfileSet(activity, this);
        taskSet.start(username, gender, city, state, imageData);
    }

    private void profileUI(boolean disable)
    {
        layoutOverlay.setVisibility(disable ? View.VISIBLE : View.GONE);

        if(menuReload != null) menuReload.setEnabled(!disable);

        buttonUpdate.setEnabled(!disable);

        progressBar.setVisibility(disable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void profileEmailStarted()
    {
        profileUI(true);
    }

    @Override
    public void profileEmailSuccess(String message)
    {
        profileUI(false);

        UI.alert(activity, Rewards.appName, message, new Runnable()
        {
            @Override
            public void run()
            {

                showSection(layoutVerify);
            }
        });
    }

    @Override
    public void profileEmailError(String error)
    {
        profileUI(false);

        if(TextUtils.isEmpty(error))
        {
            UI.toast(activity, "An error occurred!");
        }
        else
        {
            UI.alert(activity, Rewards.appName, error);
        }
    }

    @Override
    public void profileVerifyStarted()
    {
        profileUI(true);
    }

    @Override
    public void profileVerifySuccess(String message)
    {
        profileUI(false);

        UI.alert(activity, Rewards.appName, message, new Runnable()
        {
            @Override
            public void run()
            {
                showSection(layoutBio);
            }
        });
    }

    @Override
    public void profileVerifyError(String error)
    {
        profileUI(false);

        if(TextUtils.isEmpty(error))
        {
            UI.toast(activity, "An error occurred!");
        }
        else
        {
            UI.alert(activity, Rewards.appName, error);
        }
    }

    @Override
    public void profileSetStarted()
    {
        profileUI(true);
    }

    @Override
    public void profileSetSuccess(String message)
    {
        profileUI(false);

        UI.alert(activity, Rewards.appName, message, new Runnable()
        {
            @Override
            public void run()
            {
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        });
    }

    @Override
    public void profileSetError(String error)
    {
        profileUI(false);

        if(TextUtils.isEmpty(error))
        {
            UI.toast(activity, "An error occurred!");
        }
        else
        {
            UI.alert(activity, Rewards.appName, error);
        }
    }

    @Override
    public void profileGetStarted()
    {
        profileUI(true);
    }

    @Override
    public void profileGetSuccess(ProfileItem profile)
    {
        profileUI(false);

        if(!TextUtils.isEmpty(profile.thumb))
        {
            Image.loadFull(activity, profile.thumb /*+ "?stamp=" + Prefs.getThumbStamp(activity)*/, 0, imageCover, true, null);
        }

        //validUsername = true;
        //searchUser.allowed = profile.username;

        inputEmail.setInput(profile.email);
        inputUsername.setInput(profile.username);
        inputPhone.setInput("+" + profile.phone);
        inputGender.setIndex(profile.gender - 1);

        inputCity.setInput(profile.city);
        inputState.setInput(profile.state);

        if(TextUtils.isEmpty(profile.email) || !profile.passed)
        {
            showSection(layoutEmail);
        }
        else
        {
            showSection(layoutBio);
        }

        updateState();
        //userSearchable = true;
    }

    @Override
    public void profileGetError(String error)
    {
        profileUI(false);

        UI.alert(activity, Rewards.appName, error, new Runnable()
        {
            @Override
            public void run()
            {
                activity.finish();
            }
        });
    }

    @Override
    public void imageStarted()
    {
        LoadingDialog.show(activity, "Please wait...");
    }

    @Override
    public void imageDone(Bitmap bitmap, byte[] data)
    {
        LoadingDialog.dismiss(activity);

        imageCover.setImageBitmap(null);
        imageCover.setImageBitmap(bitmap);
    }

    @Override
    public void imageFailed(String error)
    {
        LoadingDialog.dismiss(activity);

        imageCover.setImageBitmap(null);

        imageData = null;
    }
}
