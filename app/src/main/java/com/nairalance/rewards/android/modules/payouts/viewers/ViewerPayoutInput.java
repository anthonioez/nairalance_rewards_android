package com.nairalance.rewards.android.modules.payouts.viewers;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.io.Server;
import com.miciniti.library.objects.FragmentBase;
import com.miciniti.library.objects.ViewerFragment;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.ads.AdmobBanner;
import com.nairalance.rewards.android.views.RewardsInput;
import com.nairalance.rewards.android.modules.payouts.controllers.ControllerPayout;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutTypeItem;
import com.nairalance.rewards.android.modules.payouts.socket.SocketPayoutRequest;
import com.nairalance.rewards.android.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ViewerPayoutInput extends ViewerFragment implements View.OnClickListener, SocketPayoutRequest.SocketPayoutRequestCallback, RewardsInput.OnInputChangedListener
{
    public static final String TAG = ViewerPayoutData.class.getSimpleName();

    private FrameLayout layoutMain;
    private LinearLayout layoutOverlay;

    private Button buttonSubmit;

    private TextView textTitle;
    private TextView textDesc;

    private RewardsInput inputProvider;
    private RewardsInput inputName;
    private RewardsInput inputAccount;
    private RewardsInput inputPhone;

    private ProgressBar progressBar;

    private ControllerPayout controller;

    private SocketPayoutRequest taskRequest;
    private AdmobBanner adBanner = null;

    public ViewerPayoutInput(FragmentBase fragment)
    {
        super(fragment);
    }

    public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        controller = (ControllerPayout)getController();

        View view = inflater.inflate(R.layout.viewer_payout_input, container, false);

        fragment.setHasOptionsMenu(true);

        layoutMain = view.findViewById(R.id.layoutMain);
        UI.overrideFonts(getContext(), layoutMain, Rewards.appFont);

        layoutOverlay = view.findViewById(R.id.layoutOverlay);
        layoutOverlay.setVisibility(View.GONE);


        textTitle = view.findViewById(R.id.textTitle);
        textTitle.setText(String.format("%s (%s)", Rewards.payoutType(controller.rate.type), Utils.formatMoney(controller.rate.amount, 0)));

        textDesc = view.findViewById(R.id.textDesc);
        textDesc.setText(controller.message);

        inputProvider = view.findViewById(R.id.inputProvider);
        inputName = view.findViewById(R.id.inputName);
        inputAccount = view.findViewById(R.id.inputAccount);
        inputPhone = view.findViewById(R.id.inputPhone);

        inputProvider.setOnInputChanged(this);
        inputName.setOnInputChanged(this);
        inputAccount.setOnInputChanged(this);
        inputPhone.setOnInputChanged(this);


        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(this);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        List<Object> providers = new ArrayList<>();
        for (PayoutTypeItem type : controller.typeList)
        {
            if(type.type.equals(controller.rate.type))
            {
                providers.add(type);
            }
        }

        inputProvider.setPickerList(providers);

        if(controller.rate.type.equals("bank"))
        {
            inputProvider.setPlaceholder("Select bank");
            inputName.setPlaceholder("Your account name");
            inputAccount.setPlaceholder("Your account number");

            inputProvider.setVisibility(View.VISIBLE);
            inputName.setVisibility(View.VISIBLE);
            inputAccount.setVisibility(View.VISIBLE);

            inputPhone.setVisibility(View.GONE);
        }
        else if(controller.rate.type.equals("airtime"))
        {
            inputProvider.setPlaceholder("Select network");
            inputPhone.setPlaceholder("Your phone number");

            inputProvider.setVisibility(View.VISIBLE);
            inputName.setVisibility(View.GONE);
            inputAccount.setVisibility(View.GONE);

            inputPhone.setVisibility(View.VISIBLE);
        }
        else
        {
            inputProvider.setVisibility(View.GONE);
        }

        check();

        adBanner = new AdmobBanner(getActivity().getApplicationContext(), getString(R.string.admob_banner), (RelativeLayout)view.findViewById(R.id.layoutAd), 0, 0);
        if(AdmobBanner.hasSlot(getContext(), AdmobBanner.SLOT_CASHOUT))
        {
            adBanner.prepare();
            adBanner.load();
        }

        return view;
    }


    public void attach(Context context)
    {
        super.attach(context);

        //ControllerPayout controller = (ControllerPayout)getController();

        //if(controller != null) controller.setTitle("Cash Out");
    }

    public void resume()
    {
        super.resume();

        adBanner.resume(true);
    }

    public void pause()
    {
        super.pause();

        adBanner.pause();
    }

    public void quit()
    {
        super.quit();

        adBanner.close();

        unrequest();
    }

    @Override
    public void onClick(View v) {

        if(v == buttonSubmit)
        {
            validate();
        }
    }

    @Override
    public void onInputChanged(RewardsInput input, String text)
    {
        check();
    }

    private void check()
    {
        boolean active =
                inputProvider.getSelectedIndex() != -1 && (
                        (inputPhone.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(inputPhone.getInput().trim())) ||
                                (inputName.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(inputName.getInput().trim()) && !TextUtils.isEmpty(inputAccount.getInput().trim()))
                );

        buttonSubmit.setEnabled(active);
        buttonSubmit.setAlpha(active ? 1.0f : 0.5f);
    }

    private void validate()
    {
        int pindex = inputProvider.getSelectedIndex();
        if(pindex == -1)
        {
            Snackbar.make(fragment.getView(), inputProvider.getPlaceholder(), Snackbar.LENGTH_LONG).show();
        }
        else
        {
            PayoutTypeItem type = (PayoutTypeItem) inputProvider.getPickerList().get(pindex);
            if(type == null)
            {
                Snackbar.make(fragment.getView(), inputProvider.getPlaceholder(), Snackbar.LENGTH_LONG).show();
            }
            else if(inputPhone.getVisibility() == View.VISIBLE)
            {
                String phone = inputPhone.getInput().trim();
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
                else
                {
                    request(type.id, "", "234" + phone);
                }
            }
            else
            {
                String name     = inputName.getInput().trim();
                String account  = inputAccount.getInput().trim();

                if(name.length() == 0 )
                {
                    Snackbar.make(fragment.getView(), R.string.please_acc_name, Snackbar.LENGTH_LONG).show();
                    inputName.getEditText().requestFocus();
                }
                else if(account.length() == 0 )
                {
                    Snackbar.make(fragment.getView(), R.string.please_acc_number, Snackbar.LENGTH_LONG).show();
                    inputAccount.getEditText().requestFocus();
                }
                else
                {
                    request(type.id, name, account);
                }
            }
        }
    }

    public void unrequest()
    {
        if (taskRequest != null)
        {
            taskRequest.setCallback(null);
            taskRequest = null;
        }
    }

    public void request(long provider, String name, String account)
    {

        if (!Server.isOnline(getContext()))
        {
            UI.alert(getContext(), Rewards.appName, fragment.getString(R.string.err_no_connection));
            return;
        }

        unrequest();

        UI.hideKeyboard(getContext(), inputProvider.getEditText());

        taskRequest = new SocketPayoutRequest(getContext(), this);
        taskRequest.start(provider, controller.rate.points, name, account);
    }

    private void verifyUI(boolean disable)
    {
        layoutOverlay.setVisibility(disable ? View.VISIBLE : View.GONE);

        buttonSubmit.setEnabled(!disable);

        inputProvider.setEnabled(!disable);
        inputName.setEnabled(!disable);
        inputAccount.setEnabled(!disable);
        inputPhone.setEnabled(!disable);

        progressBar.setVisibility(disable ? View.VISIBLE : View.GONE);
    }

    @Override
    public void payoutRequestStarted()
    {
        verifyUI(true);
    }

    @Override
    public void payoutRequestSuccess(String msg)
    {
        verifyUI(false);

        UI.alert(getActivity(), "Cash Out", msg, new Runnable()
        {
            @Override
            public void run()
            {
                controller.done();
            }
        });
    }

    @Override
    public void payoutRequestError(String error)
    {
        verifyUI(false);

        UI.alert(getContext(), "Cash Out", error);
    }

}