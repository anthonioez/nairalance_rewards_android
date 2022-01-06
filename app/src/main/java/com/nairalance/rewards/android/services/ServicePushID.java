package com.nairalance.rewards.android.services;

import android.text.TextUtils;

// import com.google.firebase.iid.FirebaseInstanceId;
// import com.google.firebase.iid.FirebaseInstanceIdService;

import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Prefs;

public class ServicePushID // extends FirebaseInstanceIdService
{
    private static final String TAG = ServicePushID.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    // @Override
    public void onTokenRefresh()
    {
        // String token = FirebaseInstanceId.getInstance().getToken();
        //Logger.e(TAG, "Refreshed token: " + token);

        // sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token)
    {
        /*
        String old = Prefs.getPushToken(this);
        if(!TextUtils.isEmpty(old) && old == token) return;

        Prefs.setPushToken(this, token);
        Prefs.setPushTokenSent(this, false);

        Rewards.serviceStart(this, Rewards.ACTION_TOKEN);

        if(Prefs.getLastDev(this) > 0)
        {
        }*/
    }
}
