package com.nairalance.rewards.android.modules.payouts.viewers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.FragmentBase;
import com.miciniti.library.objects.ViewerFragment;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.activities.ActivityPayouts;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.nairalance.rewards.android.ads.AdmobBanner;
import com.nairalance.rewards.android.modules.payouts.adapters.AdapterPayoutData;
import com.nairalance.rewards.android.modules.payouts.controllers.ControllerPayout;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutRateItem;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutTypeItem;
import com.nairalance.rewards.android.modules.payouts.socket.SocketPayoutData;
import com.nairalance.rewards.android.utils.Utils;

import java.util.List;

public class ViewerPayoutData extends ViewerFragment implements RecyclerOnItemListener, SocketPayoutData.SocketPayoutDataCallback
{
    public static final String TAG = ViewerPayoutData.class.getSimpleName();

    private FrameLayout layoutMain;
    private LinearLayout layoutFooter;

    private ProgressBar     progressBar;

    private SocketPayoutData task;

    private ControllerPayout controller;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;

    private AdapterPayoutData adapter;

    private boolean isLoaded = false;
    private boolean isLoading = false;
    private MenuItem menuItemReload = null;
    private TextView textEarnings;

    private AdmobBanner adBanner = null;

    public ViewerPayoutData(FragmentBase fragment)
    {
        super(fragment);
    }

    public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        controller = (ControllerPayout)getController();

        View view = inflater.inflate(R.layout.viewer_payout_data, container, false);

        fragment.setHasOptionsMenu(true);

        layoutMain = view.findViewById(R.id.layoutMain);
        UI.overrideFonts(getContext(), layoutMain, Rewards.appFont);

        layoutFooter = view.findViewById(R.id.layoutFooter);
        layoutFooter.setVisibility(View.GONE);

        ImageView imageThumb = layoutFooter.findViewById(R.id.imageThumb);
        imageThumb.setImageResource(R.drawable.logo_white);

        TextView textTitle = layoutFooter.findViewById(R.id.textTitle);
        textTitle.setTypeface(Rewards.appFont);
        textTitle.setText("Total Earnings (points)");

        TextView textDesc = layoutFooter.findViewById(R.id.textDesc);
        textDesc.setVisibility(View.GONE);

        textEarnings = layoutFooter.findViewById(R.id.textValue);

        adapter = new AdapterPayoutData(view.getContext(), controller.rateList, this);

        layoutManager = new LinearLayoutManager(view.getContext());

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        recyclerView = view.findViewById(R.id.itemList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        registerBus();

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

    public void pause()
    {
        super.pause();

        adBanner.pause();
    }

    public void resume()
    {
        super.resume();

        if(controller.rateList.size() == 0 || controller.typeList.size() == 0)
        {
            data();
        }
        else
        {
            adapter.notifyDataSetChanged();
        }

        adBanner.resume(true);
        updateEarnings();
    }

    public void quit()
    {
        super.quit();

        adBanner.close();
        undata();
    }

    public void createMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        inflater.inflate(R.menu.viewer_payout_data, menu);
    }

    public void prepareMenu(Menu menu)
    {
        menuItemReload = menu.findItem(R.id.action_reload);
    }

    public boolean selectMenu(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_reload:
                data();
                return true;

            case R.id.action_payouts:
                payouts();
                return true;
        }

        return false;
    }

    private void updateEarnings()
    {
        textEarnings.setText(Utils.formatPoints(controller.earnings));

        layoutFooter.setVisibility(isLoaded ? View.VISIBLE : View.GONE);
    }

    private void payouts()
    {
        Intent intent = new Intent(getActivity(), ActivityPayouts.class);
        getActivity().startActivity(intent);

    }

    public void undata()
    {
        if (task != null)
        {
            task.setCallback(null);
            task = null;
        }
    }

    public void data()
    {
        undata();

        task = new SocketPayoutData(getContext(), this);
        task.start();
    }

    private void dataUI(boolean disable)
    {
        progressBar.setVisibility(disable ? View.VISIBLE : View.GONE);
        if(menuItemReload != null) menuItemReload.setVisible(!disable);
    }

    @Override
    public void payoutDataStarted()
    {
        isLoading = true;

        dataUI(true);
    }

    @Override
    public void payoutDataSuccess(int earnings, String message, List<PayoutRateItem> rates, List<PayoutTypeItem> types)
    {
        dataUI(false);

        controller.message = message;
        controller.earnings = earnings;

        controller.rateList.clear();
        controller.rateList.addAll(rates);

        controller.typeList.clear();
        controller.typeList.addAll(types);

        adapter.notifyDataSetChanged();

        layoutFooter.setVisibility(View.VISIBLE);

        isLoaded = true;
        isLoading = false;

        updateEarnings();
    }

    @Override
    public void payoutDataError(String error)
    {
        isLoading = false;

        dataUI(false);

        //UI.alert(getContext(), Rewards.appName, error);
        UI.toast(getContext(), error);
    }

    @Override
    public void onItemClick(RecyclerView.Adapter adp, View view, int position)
    {
        PayoutRateItem rate = adapter.getItem(position);
        if(rate == null) return;

        if(controller.earnings < rate.points)
        {
            UI.alert(getContext(), Rewards.appName, "You don't have enough points to cash out!");
            return;
        }

        controller.rate = rate;
        controller.input();
    }

    @Override
    public boolean onItemLongClick(RecyclerView.Adapter adp, View view, int position)
    {
        return false;
    }

    @Override
    public void onMenuClick(RecyclerView.Adapter adp, View view, int position)
    {

    }
}