package com.nairalance.rewards.android.modules.phone.viewers;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.io.Server;
import com.miciniti.library.objects.ViewerFragment;
import com.nairalance.rewards.android.BuildConfig;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.miciniti.library.objects.FragmentBase;
import com.nairalance.rewards.android.views.RewardsInput;
import com.nairalance.rewards.android.modules.phone.controllers.ControllerPhone;
import com.nairalance.rewards.android.modules.phone.socket.SocketPhoneSend;

public class ViewerPhoneSend extends ViewerFragment implements View.OnClickListener, SocketPhoneSend.SocketPhoneSendCallback
{
    public static final String TAG = ViewerPhoneSend.class.getSimpleName();

    private LinearLayout    layoutMain;
    private LinearLayout    layoutOverlay;
    private TextView        textCode;

    private RewardsInput    inputPhone;
    private Button          buttonNext;
    private ProgressBar     progressBar;

    private SocketPhoneSend task;

    private ControllerPhone controller;

    public ViewerPhoneSend(FragmentBase fragment)
    {
        super(fragment);
    }

    public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        controller = (ControllerPhone)getController();

        View view = inflater.inflate(R.layout.viewer_phone_send, container, false);

        fragment.setHasOptionsMenu(true);

        layoutMain = view.findViewById(R.id.layoutMain);
        UI.overrideFonts(getContext(), layoutMain, Rewards.appFont);

        layoutOverlay = view.findViewById(R.id.layoutOverlay);
        layoutOverlay.setVisibility(View.GONE);

        textCode = view.findViewById(R.id.textCode);
        textCode.setOnClickListener(this);
        textCode.setVisibility(TextUtils.isEmpty(Prefs.getPhone(view.getContext())) ? View.GONE : View.VISIBLE);

        inputPhone = view.findViewById(R.id.inputPhone);
        inputPhone.getPrefixText().setTypeface(Rewards.appFontBold);
        inputPhone.getPrefixText().setTextColor(view.getResources().getColor(R.color.disabled));

        inputPhone.getEditText().addTextChangedListener(new TextWatcher()
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
                String phone = inputPhone.getInput().trim();
                boolean active = (phone.startsWith("0") && phone.length() == 11) || (!phone.startsWith("0") && phone.length() == 10);
                buttonNext.setEnabled(active);
                buttonNext.setAlpha(active ? 1.0f : 0.5f);
            }
        });

        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(this);
        buttonNext.setEnabled(false);
        buttonNext.setAlpha(0.5f);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        if(BuildConfig.DEBUG)
        {
            inputPhone.setInput("09057153158");
        }

        return view;
    }

    public void quit()
    {
        super.quit();

        unsend();
    }

    @Override
    public void onClick(View v)
    {
        if(v == buttonNext)
        {
            next();
        }
        else if(v == textCode)
        {
            controller.phone = Prefs.getPhone(getContext());

            controller.verify(true);
        }
    }

    private void next()
    {
        String phone = inputPhone.getInput();
        if(phone.startsWith("0")) phone = phone.substring(1);

        if(phone.length() == 0 )
        {
            Snackbar.make(fragment.getView(), R.string.please_phone, Snackbar.LENGTH_LONG).show();
            inputPhone.getEditText().requestFocus();
        }
        else if(phone.length() != 10 )
        {
            Snackbar.make(fragment.getView(), R.string.please_valid_phone, Snackbar.LENGTH_LONG).show();
            inputPhone.getEditText().requestFocus();
        }
        else if (!Server.isOnline(getContext()))
        {
            UI.alert(getContext(), Rewards.appName, fragment.getString(R.string.err_no_connection));
        }
        else
        {
            send("234" + phone);
        }
    }

    public void unsend()
    {
        if (task != null)
        {
            task.setCallback(null);
            task = null;
        }
    }

    public void send(String phone)
    {
        unsend();

        controller.phone = phone;

        UI.hideKeyboard(getContext(), inputPhone.getEditText());

        task = new SocketPhoneSend(getContext(), this);
        task.start(phone, false);
    }

    private void sendUI(boolean disable)
    {
        layoutOverlay.setVisibility(disable ? View.VISIBLE : View.GONE);

        buttonNext.setEnabled(!disable);
        inputPhone.setEnabled(!disable);

        progressBar.setVisibility(disable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void phoneSendStarted()
    {
        sendUI(true);
    }

    @Override
    public void phoneSendSuccess(String message)
    {
        UI.toast(getContext(), message);

        Prefs.setPhone(getContext(), controller.phone);

        controller.verify(false);
    }

    @Override
    public void phoneSendError(String error)
    {
        sendUI(false);

        UI.alert(getContext(), Rewards.appName, error);
    }
}