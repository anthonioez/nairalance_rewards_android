package com.nairalance.rewards.android.modules.phone.viewers;

import android.os.Bundle;
import android.os.CountDownTimer;
import com.google.android.material.snackbar.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miciniti.library.helpers.DateTime;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.io.Server;
import com.miciniti.library.objects.ViewerFragment;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.miciniti.library.objects.FragmentBase;
import com.nairalance.rewards.android.views.RewardsDigits;
import com.nairalance.rewards.android.modules.phone.controllers.ControllerPhone;
import com.nairalance.rewards.android.modules.phone.socket.SocketPhoneSend;
import com.nairalance.rewards.android.modules.phone.socket.SocketPhoneVerify;

public class ViewerPhoneVerify extends ViewerFragment implements View.OnClickListener, SocketPhoneVerify.SocketPhoneVerifyCallback, SocketPhoneSend.SocketPhoneSendCallback
{
    public static final String TAG = ViewerPhoneSend.class.getSimpleName();

    private final int STAGE_TEXT    = 0;
    private final int STAGE_CALL    = 1;
    private final int STAGE_MANUAL  = 2;

    private LinearLayout layoutMain;
    private LinearLayout layoutAction;
    private LinearLayout layoutOverlay;

    private Button buttonNext;

    private TextView textTitle;
    private TextView textTimer;
    private TextView textAction;
    private RewardsDigits inputCode;

    private ProgressBar progressBar;

    private ControllerPhone controller;
    private SocketPhoneSend taskSend;
    private SocketPhoneVerify taskVerify;
    private CountDownTimer countDown;

    private int countTotal = 30000;
    private int countStep = 1000;
    private int stage = STAGE_TEXT;

    public ViewerPhoneVerify(FragmentBase fragment)
    {
        super(fragment);
    }

    public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        controller = (ControllerPhone)getController();

        View view = inflater.inflate(R.layout.viewer_phone_verify, container, false);

        fragment.setHasOptionsMenu(true);

        layoutMain = view.findViewById(R.id.layoutMain);
        UI.overrideFonts(getContext(), layoutMain, Rewards.appFont);

        layoutAction = view.findViewById(R.id.layoutAction);
        layoutAction.setVisibility(View.GONE);

        layoutOverlay = view.findViewById(R.id.layoutOverlay);
        layoutOverlay.setVisibility(View.GONE);

        textTitle = view.findViewById(R.id.textTitle);
        textTitle.setText(String.format("Please enter the verification code sent to +%s", controller.phone));

        textTimer = view.findViewById(R.id.textTimer);
        textTimer.setText("");

        textAction = view.findViewById(R.id.textAction);
        textAction.setOnClickListener(this);

        inputCode = view.findViewById(R.id.inputCode);
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
                buttonNext.setEnabled(active);
                buttonNext.setAlpha(active ? 1.0f : 0.5f);
            }
        });

        buttonNext = view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(this);
        buttonNext.setEnabled(false);
        buttonNext.setAlpha(0.5f);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        inputCode.setCode("");
        inputCode.getEditText().requestFocus();

        if(controller.hasCode)
        {
            layoutAction.setVisibility(View.GONE);
        }
        else
        {
            countDown();
        }

        return view;
    }

    public void quit()
    {
        super.quit();

        unsend();
        unverify();
    }

    @Override
    public void onClick(View v) {

        if(v == buttonNext)
        {
            validate();
        }
        else if(v == textAction)
        {
            action();
        }
    }

    private void countDown()
    {
        countTotal = 30000;
        textTimer.setVisibility(View.VISIBLE);
        layoutAction.setVisibility(View.GONE);

        countDown = new CountDownTimer(countTotal, countStep)
        {

            @Override
            public void onTick(long millisUntilFinished)
            {
                countTotal -= countStep;

                if(stage == STAGE_TEXT)
                    textTimer.setText(String.format("You will receive a text in %s", DateTime.formatDuration((countTotal / countStep) * 1000)));
                else if(stage == STAGE_CALL)
                    textTimer.setText(String.format("You will receive a call in %s", DateTime.formatDuration((countTotal / countStep) * 1000)));
                else
                    textTimer.setText("");
            }

            @Override
            public void onFinish()
            {
                textTimer.setVisibility(View.GONE);
                layoutAction.setVisibility(View.VISIBLE);

                changeAction();
            }
        };
        countDown.start();
    }

    public void unsend()
    {
        if (taskSend != null)
        {
            taskSend.setCallback(null);
            taskSend = null;
        }
    }

    public void send()
    {
        unsend();

        UI.hideKeyboard(getContext(), inputCode.getEditText());

        taskSend = new SocketPhoneSend(getContext(), this);
        taskSend.start(controller.phone, true);
    }

    @Override
    public void phoneSendStarted()
    {
        verifyUI(true);
    }

    @Override
    public void phoneSendSuccess(String message)
    {
        verifyUI(false);

        UI.toast(getContext(), message);

        countDown();
    }

    @Override
    public void phoneSendError(String error)
    {
        verifyUI(false);

        UI.alert(getContext(), Rewards.appName, error);
    }

    private void changeAction()
    {
        if(stage == STAGE_TEXT)
        {
            stage = STAGE_CALL;
            textAction.setText("Call me instead");
        }
        else if(stage == STAGE_CALL)
        {
            stage = STAGE_MANUAL;
            textAction.setText("Try manual");
        }
    }

    private void action()
    {
        if(stage == STAGE_CALL)
        {
            send();
        }
        else if(stage == STAGE_MANUAL)
        {
            UI.confirm(getContext(), "Text Verification", "Text 'VERIFY' to " + Rewards.appPhone + " to get a verification code.", new Runnable()
            {
                @Override
                public void run()
                {
                    Rewards.sendVerifySMS(getActivity());
                }
            }, null);
        }
    }

    private void validate()
    {
        String code = inputCode.getCode().trim();

        if(code.length() == 0 )
        {
            Snackbar.make(fragment.getView(), R.string.please_verification_code, Snackbar.LENGTH_LONG).show();
            inputCode.getEditText().requestFocus();
        }
        else if(code.length() != 4 )
        {
            Snackbar.make(fragment.getView(), R.string.please_valid_verification_code, Snackbar.LENGTH_LONG).show();
            inputCode.getEditText().requestFocus();
        }

        else if (!Server.isOnline(getContext()))
        {
            UI.alert(getContext(), Rewards.appName, fragment.getString(R.string.err_no_connection));
        }
        else
        {
            verify(code);
        }
    }

    public void unverify()
    {
        if (taskVerify != null)
        {
            taskVerify.setCallback(null);
            taskVerify = null;
        }
    }

    public void verify(String code)
    {
        unverify();

        if(countDown != null) countDown.cancel();

        textTimer.setVisibility(View.GONE);
        layoutAction.setVisibility(View.GONE);

        UI.hideKeyboard(getContext(), inputCode.getEditText());

        taskVerify = new SocketPhoneVerify(getContext(), this);
        taskVerify.start(controller.phone, code);
    }

    private void verifyUI(boolean disable)
    {
        layoutOverlay.setVisibility(disable ? View.VISIBLE : View.GONE);

        buttonNext.setEnabled(!disable);
        inputCode.setEnabled(!disable);

        progressBar.setVisibility(disable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void phoneVerifyStarted()
    {
        verifyUI(true);
    }

    @Override
    public void phoneVerifySuccess(String msg)
    {
        verifyUI(false);

        controller.main();
    }

    @Override
    public void phoneVerifyError(String error)
    {
        verifyUI(false);

        UI.alert(getContext(), Rewards.appName, error);
    }
}