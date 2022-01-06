package com.nairalance.rewards.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Appirater
{
	private static final String PREF_LAUNCH_COUNT = "launch_count";
	private static final String PREF_EVENT_COUNT = "event_count";
	private static final String PREF_RATE_CLICKED = "rateclicked";
	private static final String PREF_DONT_SHOW = "dontshow";
	private static final String PREF_DATE_REMINDER_PRESSED = "date_reminder_pressed";
	private static final String PREF_DATE_FIRST_LAUNCHED = "date_firstlaunch";
	private static final String PREF_APP_VERSION_CODE = "versioncode";

    public static SharedPreferences getPrefs(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(context.getPackageName()+".appirater", 0);

        SharedPreferences.Editor editor = prefs.edit();

        return prefs;
    }

    public static void appLaunched(Context mContext)
    {
    	boolean testMode = mContext.getResources().getBoolean(R.bool.appirator_test_mode);

        SharedPreferences prefs = getPrefs(mContext);

        if(!testMode && (prefs.getBoolean(PREF_DONT_SHOW, false) || prefs.getBoolean(PREF_RATE_CLICKED, false)))
        {
            return;
        }

        if(testMode)
        {
            showRateDialog(mContext);
        	return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        // Increment launch counter
        long launch_count = prefs.getLong(PREF_LAUNCH_COUNT, 0);

        // Get events counter
        long event_count = prefs.getLong(PREF_EVENT_COUNT, 0);

        // Get date of first launch
        long date_firstLaunch = prefs.getLong(PREF_DATE_FIRST_LAUNCHED, 0);
        
		// Get reminder date pressed
		long date_reminder_pressed = prefs.getLong(PREF_DATE_REMINDER_PRESSED, 0);
        
        try
        {
	        int appVersionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
	        if(prefs.getInt(PREF_APP_VERSION_CODE, 0)  != appVersionCode)
            {
		        //Reset the launch and event counters to help assure users are rating based onEvent the latest version.
		        launch_count = 0;
		        event_count = 0;
		        editor.putLong(PREF_EVENT_COUNT, event_count);
	        }
	        editor.putInt(PREF_APP_VERSION_CODE, appVersionCode);
        }
        catch(Exception e)
        {
        	//do nothing
        }
        
        launch_count++;
        editor.putLong(PREF_LAUNCH_COUNT, launch_count);

        if (date_firstLaunch == 0)
        {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(PREF_DATE_FIRST_LAUNCHED, date_firstLaunch);
        }
        
        // Wait at least n days or m events before opening
        if (launch_count >= mContext.getResources().getInteger(R.integer.appirator_launches_until_prompt))
        {
			long millisecondsToWait = mContext.getResources().getInteger(R.integer.appirator_days_until_prompt) * 24 * 60 * 60 * 1000L;			
			if (System.currentTimeMillis() >= (date_firstLaunch + millisecondsToWait) || event_count >= mContext.getResources().getInteger(R.integer.appirator_events_until_prompt))
            {
				if(date_reminder_pressed == 0)
                {
                    showRateDialog(mContext);
				}
                else
                {
					long remindMillisecondsToWait = mContext.getResources().getInteger(R.integer.appirator_days_before_reminding) * 24 * 60 * 60 * 1000L;
					if(System.currentTimeMillis() >= (remindMillisecondsToWait + date_reminder_pressed))
                    {
                        showRateDialog(mContext);
					}
				}
			}
        }
        
        editor.commit();
    }   


	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void significantEvent(Context mContext)
    {
        boolean testMode = mContext.getResources().getBoolean(R.bool.appirator_test_mode);
        SharedPreferences prefs = mContext.getSharedPreferences(mContext.getPackageName()+".appirater", 0);
        if(!testMode && (prefs.getBoolean(PREF_DONT_SHOW, false) || prefs.getBoolean(PREF_RATE_CLICKED, false))) {return;}

        long event_count = prefs.getLong(PREF_EVENT_COUNT, 0);
        event_count++;
        prefs.edit().putLong(PREF_EVENT_COUNT, event_count).apply();
    }

    public static void rateApp(Context mContext)
    {
        SharedPreferences.Editor editor = getPrefs(mContext).edit();

        String url = String.format(mContext.getString(R.string.appirator_market_url), mContext.getPackageName());
        try
        {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
        catch (Exception e)
        {
            url = String.format(mContext.getString(R.string.appirator_playstore_url), mContext.getPackageName());
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }

        if (editor != null)
        {
            editor.putBoolean(PREF_RATE_CLICKED, true);
            editor.commit();
        }
    }

	@SuppressLint("NewApi")
	public static void showRateDialog(final Context mContext)
    {
        final Dialog dialog = new Dialog(mContext);

        final SharedPreferences.Editor editor = getPrefs(mContext).edit();

        String appName = mContext.getString(R.string.app_name);
        if(mContext.getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_LOW || mContext.getResources().getDisplayMetrics().densityDpi == DisplayMetrics.DENSITY_MEDIUM)
        {
        	Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        	int rotation = display.getRotation();
        	if(rotation == 90 || rotation == 270)
            {
        		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        	}
            else
            {
        		dialog.setTitle(String.format(mContext.getString(R.string.rate_title), appName));
        	}
        }
        else
        {
        	dialog.setTitle(String.format(mContext.getString(R.string.rate_title), appName));
        }

        LinearLayout layout = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.appirater, null);
        
        TextView tv = (TextView) layout.findViewById(R.id.message);
        tv.setText(String.format(mContext.getString(R.string.rate_message), appName));

        Button rateButton = (Button) layout.findViewById(R.id.rate);
        rateButton.setText(String.format(mContext.getString(R.string.rate_yes), appName));
        rateButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                rateApp(mContext);
                dialog.dismiss();
            }
        });

        Button rateLaterButton = (Button) layout.findViewById(R.id.rateLater);
        rateLaterButton.setText(mContext.getString(R.string.rate_later));
        rateLaterButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
            	if (editor != null)
                {
    				editor.putLong(PREF_DATE_REMINDER_PRESSED, System.currentTimeMillis());
    				editor.commit();
				}
                dialog.dismiss();
            }
        });

        Button tellButton = (Button) layout.findViewById(R.id.tellFriend);
        tellButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                if (editor != null)
                {
                    editor.putLong(PREF_DATE_REMINDER_PRESSED, System.currentTimeMillis());
                    editor.commit();
                }
                Rewards.shareApp(mContext);
                dialog.dismiss();
            }
        });

        Button cancelButton = (Button) layout.findViewById(R.id.cancel);
        cancelButton.setText(mContext.getString(R.string.rate_cancel));
        cancelButton.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                if (editor != null)
                {
                    editor.putBoolean(PREF_DONT_SHOW, true);
                    editor.commit();
                }
                dialog.dismiss();
            }
        });

        dialog.setContentView(layout);
        dialog.show();        
    }
}