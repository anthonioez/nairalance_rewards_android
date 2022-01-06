package com.nairalance.rewards.android;

import com.miciniti.library.objects.AppEvent;

/**
 * Created by Miciniti on 26/12/2016.
 */

public class RewardsEvent extends AppEvent
{
    public static String GOOGLE_AD_LOADED       = "rewarded_ad_loaded";
    public static String GOOGLE_AD_CLEARED      = "rewarded_ad_cleared";

    public static String FACEBOOK_AD_LOADED     = "facebook_ad_loaded";
    public static String FACEBOOK_AD_CLEARED    = "facebook_ad_cleared";

    public static String REWARDED_POINTS        = "rewarded_points";

    public static String PROFILE_CHANGED        = Strings.profile_changed;
}
