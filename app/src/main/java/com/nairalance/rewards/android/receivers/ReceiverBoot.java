package com.nairalance.rewards.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nairalance.rewards.android.Rewards;

public class ReceiverBoot extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Rewards.serviceStart(context, Rewards.ACTION_BOOT);
    }
}
