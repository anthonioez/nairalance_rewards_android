package com.nairalance.rewards.android;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs
{
    private static final String KEY_ID              = "unique_id";
    private static final String KEY_RANKING         = "ranking";
    private static final String KEY_REWARDS         = "rewards";

    private static final String KEY_THUMB_URL       = "thumb_url";
    private static final String KEY_THUMB_STAMP     = "thumb_stamp";
    private static final String KEY_USERNAME        = "username";

    private static final String KEY_PHONE           = "phone";
    private static final String KEY_EMAIL           = "email";
    private static final String KEY_HASH            = "hash";
    private static final String KEY_LASTDEV         = "last_dev";
    private static final String KEY_LAST_RUN        = "last_run";
    private static final String KEY_LAST_FEEDBACK   = "last_feedback";

    private static final String KEY_LASTLAT         = "lat";
    private static final String KEY_LASTLON         = "lon";

    private static final String KEY_TOKEN           = "token";

    private static final String KEY_PUSH_COUNT      = "push_count";
    private static final String KEY_PUSH_SOUND      = "push_sound";
    private static final String KEY_PUSH_STATUS     = "push_status";
    private static final String KEY_PUSH_LED        = "push_led";
    private static final String KEY_PUSH_VIBRATE    = "push_vibrate";
    private static final String KEY_PUSH_TOKEN      = "push_token";
    private static final String KEY_PUSH_TOKEN_SENT = "push_token_sent";

    private static final String KEY_RUN_COUNT       = "run_count";

    private static final String KEY_AD_INTERS_TYPE  = "ad_inters_type";
    private static final String KEY_AD_INTERS_COUNT = "ad_inters_count";
    private static final String KEY_AD_INTERS_LIMIT = "ad_inters_thresh";
    private static final String KEY_AD_INTERS_INDEX = "ad_inters_index";

    private static final String KEY_AD_BANNER_SLOTS = "ad_banner_slots";

    private static final String KEY_IMAGE_FEED      = "image_feed";
    private static final String KEY_IMAGE_PUSH      = "image_push";



    public static void setString(Context context, String mKey, String mValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(mKey, mValue);
        editor.commit();
    }

    public static String getString(Context context, String mKey, String mDefValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return mSharedPreferences.getString(mKey, mDefValue);
    }

    public static void setBoolean(Context context, String mKey, boolean mValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(mKey, mValue);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String mKey, boolean mDefValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return mSharedPreferences.getBoolean(mKey, mDefValue);
    }

    public static void setInt(Context context, String mKey, int mValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mKey, mValue);
        editor.commit();
    }

    public static int getInt(Context context, String mKey, int mDefValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(mKey, mDefValue);
    }

    public static void setLong(Context context, String mKey, long mValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(mKey, mValue);
        editor.commit();
    }

    public static long getLong(Context context, String mKey, long mDefValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return mSharedPreferences.getLong(mKey, mDefValue);
    }

    public static void setFloat(Context context, String mKey, float mValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putFloat(mKey, mValue);
        editor.commit();
    }

    public static float getFloat(Context context, String mKey, float mDefValue)
    {
        SharedPreferences mSharedPreferences = context.getSharedPreferences(context.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return mSharedPreferences.getFloat(mKey, mDefValue);
    }

    public static void Dummy()
    {

    }

    public static long getRunCount(Context context)
    {
        return getLong(context, KEY_RUN_COUNT, 0);
    }
    public static void setRunCount(Context context, long value)
    {
        setLong(context, KEY_RUN_COUNT, value);
    }

    public static int getAdIntersCount(Context context)
    {
        return getInt(context, KEY_AD_INTERS_COUNT, 0);
    }
    public static void setAdIntersCount(Context context, int value)
    {
        setInt(context, KEY_AD_INTERS_COUNT, value);
    }

    public static int getAdIntersType(Context context)
    {
        return getInt(context, KEY_AD_INTERS_TYPE, 0);
    }
    public static void setAdIntersType(Context context, int value)
    {
        setInt(context, KEY_AD_INTERS_TYPE, value);
    }

    public static int getAdIntersIndex(Context context)
    {
        return getInt(context, KEY_AD_INTERS_INDEX, 0);
    }
    public static void setAdIntersIndex(Context context, int value)
    {
        setInt(context, KEY_AD_INTERS_INDEX, value);
    }

    public static int getAdIntersLimit(Context context)
    {
        return getInt(context, KEY_AD_INTERS_LIMIT, 0);
    }
    public static void setAdIntersLimit(Context context, int value)
    {
        setInt(context, KEY_AD_INTERS_LIMIT, value);
    }

    public static int getAdBannerSlots(Context context)
    {
        return getInt(context, KEY_AD_BANNER_SLOTS, 0);
    }
    public static void setAdBannerSlots(Context context, int value)
    {
        setInt(context, KEY_AD_BANNER_SLOTS, value);
    }

    public static void setId(Context ctx, long id)
    {
        setLong(ctx, KEY_ID, id);
    }
    public static long getId(Context ctx)
    {
        return getLong(ctx, KEY_ID, 0);
    }

    public static void setRanking(Context ctx, long id)
    {
        setLong(ctx, KEY_RANKING, id);
    }
    public static long getRanking(Context ctx)
    {
        return getLong(ctx, KEY_RANKING, 0);
    }

    public static void setRewards(Context ctx, int id)
    {
        setInt(ctx, KEY_REWARDS, id);
    }
    public static int getRewards(Context ctx)
    {
        return getInt(ctx, KEY_REWARDS, 0);
    }

    public static void setUsername(Context ctx, String name)
    {
        setString(ctx, KEY_USERNAME, name);
    }
    public static String getUsername(Context ctx)
    {
        return getString(ctx, KEY_USERNAME, "");
    }

    public static void setPhone(Context ctx, String email)
    {
        setString(ctx, KEY_PHONE, email);
    }
    public static String getPhone(Context ctx)
    {
        return getString(ctx, KEY_PHONE, "");
    }

    public static void setEmail(Context ctx, String email)
    {
        setString(ctx, KEY_EMAIL, email);
    }
    public static String getEmail(Context ctx)
    {
        return getString(ctx, KEY_EMAIL, "");
    }

    public static void setThumbUrl(Context ctx, String thumb)
    {
        setString(ctx, KEY_THUMB_URL, thumb);
    }
    public static String getThumbUrl(Context ctx)
    {
        return getString(ctx, KEY_THUMB_URL, "");
    }

    public static void setThumbStamp(Context ctx, long thumb)
    {
        setLong(ctx, KEY_THUMB_STAMP, thumb);
    }
    public static long getThumbStamp(Context ctx)
    {
        return getLong(ctx, KEY_THUMB_STAMP, 0);
    }

    public static void setApiToken(Context ctx, String email)
    {
        setString(ctx, KEY_TOKEN, email);
    }
    public static String getApiToken(Context ctx)
    {
        return getString(ctx, KEY_TOKEN, "");
    }

    public static void setApiHash(Context ctx, String email)
    {
        setString(ctx, KEY_HASH, email);
    }
    public static String getApiHash(Context ctx)
    {
        return getString(ctx, KEY_HASH, "");
    }

    public static void setLastRun(Context ctx, long stamp)
    {
        setLong(ctx, KEY_LAST_RUN, stamp);
    }
    public static long getLastRun(Context ctx)
    {
        return getLong(ctx, KEY_LAST_RUN, 0);
    }

    public static void setLastDev(Context ctx, long stamp)
    {
        setLong(ctx, KEY_LASTDEV, stamp);
    }
    public static long getLastDev(Context ctx)
    {
        return getLong(ctx, KEY_LASTDEV, 0);
    }

    public static void setLastFeedback(Context ctx, int val)
    {
        setInt(ctx, KEY_LAST_FEEDBACK, val);
    }
    public static long getLastFeedback(Context ctx)
    {
        return getInt(ctx, KEY_LAST_FEEDBACK, 0);
    }


    public static void setLastLat(Context ctx, float lat)
    {
        setFloat(ctx, KEY_LASTLAT, lat);
    }
    public static float getLastLat(Context ctx)
    {
        return getFloat(ctx, KEY_LASTLAT, 0);
    }

    public static void setLastLon(Context ctx, float lon)
    {
        setFloat(ctx, KEY_LASTLON, lon);
    }
    public static float getLastLon(Context ctx)
    {
        return getFloat(ctx, KEY_LASTLON, 0);
    }

    public static String getPushToken(Context context)
    {
        return getString(context, KEY_PUSH_TOKEN, "");
    }

    public static void setPushToken(Context context, String value)
    {
        setString(context, KEY_PUSH_TOKEN, value);
    }

    public static boolean getPushTokenSent(Context context)
    {
        return getBoolean(context, KEY_PUSH_TOKEN_SENT, false);
    }
    public static void setPushTokenSent(Context context, boolean value)
    {
        setBoolean(context, KEY_PUSH_TOKEN_SENT, value);
    }

    public static int getPushCount(Context context)
    {
        return getInt(context, KEY_PUSH_COUNT, 1);
    }
    public static void setPushCount(Context context, int value)
    {
        setInt(context, KEY_PUSH_COUNT, value);
    }

    public static String getPushSound(Context context)
    {
        return getString(context, KEY_PUSH_SOUND, "");
    }
    public static void setPushSound(Context context, String path)
    {
        setString(context, KEY_PUSH_SOUND, path);
    }

    public static boolean getPushStatus(Context context)
    {
        return getBoolean(context, KEY_PUSH_STATUS, true);
    }
    public static void setPushStatus(Context context, boolean value)
    {
        setBoolean(context, KEY_PUSH_STATUS, value);
    }

    /*
    public static boolean getPushVibrate(Context context)
    {
        return getBoolean(context, KEY_PUSH_VIBRATE, false);
    }
    public static void setPushVibrate(Context context, boolean value)
    {
        setBoolean(context, KEY_PUSH_VIBRATE, value);
    }

    public static boolean getPushLED(Context context)
    {
        return getBoolean(context, KEY_PUSH_LED, true);
    }
    public static void setPushLED(Context context, boolean value)
    {
        setBoolean(context, KEY_PUSH_LED, value);
    }
    */

    public static boolean getImagePush(Context context)
    {
        return getBoolean(context, KEY_IMAGE_PUSH, true);
    }
    public static void setImagePush(Context context, boolean value)
    {
        setBoolean(context, KEY_IMAGE_PUSH, value);
    }

    public static boolean getImageFeed(Context context)
    {
        return getBoolean(context, KEY_IMAGE_FEED, true);
    }
    public static void setImageFeed(Context context, boolean value)
    {
        setBoolean(context, KEY_IMAGE_FEED, value);
    }
}
