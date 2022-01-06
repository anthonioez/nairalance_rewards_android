package com.miciniti.library;

//LOL

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;

import com.miciniti.library.helpers.UI;
import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.modules.rewards.objects.RewardTypeItem;

public class Links
{
    public static void openUrl(Context context, String url, boolean chooser)
    {
        //BuildConfig.DEBUG
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET | Intent.FLAG_ACTIVITY_NEW_TASK);
        if(chooser)
            context.startActivity(Intent.createChooser(intent, "Open with..."));
        else
            context.startActivity(intent);
    }

    public static void openExternalUrl(Context context, String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "Open with Browser..."));
    }

    public static void openStoreUrl(Context context)
    {
        String url = "http://play.google.com/store/apps/details?id=" + context.getPackageName();
        openUrl(context, url, false);
    }


    public static void openMail(Context context, String email, String subject, String body)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String uriText = String.format("mailto:%s?subject=%s&body=%s", Uri.encode(email), Uri.encode(subject),  Uri.encode(body));
        intent.setData(Uri.parse(uriText));
        context.startActivity(Intent.createChooser(intent, "Send mail..."));
    }


    public static void openContact(Context context, String email)
    {
        /*
        String body = "";
        body += "\r\nVersion: " + mAppVersion;
        body += "\r\nOS: " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")";
        body += "\r\nPhone: " + Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL;

        openMail(context, email, "Dayly for Android", body);
        */
    }

    public static void rateApp(Context context)
    {
        String packageName = context.getPackageName();
        openUrl(context, "market://details?id=" + packageName, false);
    }

    public static void moreApps(Context context)
    {
        openUrl(context, "market://search?q=pub:Miciniti%20Ventures", false);
    }



    public static void shareText(Context context, String title, String text, String chooser)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if(!TextUtils.isEmpty(title)) intent.putExtra(Intent.EXTRA_SUBJECT, title);
        Intent openin = Intent.createChooser(intent, chooser == null ? "Share..." : chooser);
        context.startActivity(openin);
    }



    public static void shareImage(Context context, String path)
    {
        if (path == null) {
            UI.toast(context, "Image is missing or not specified!");
            return;
        }

        Uri mediaUri = Uri.parse(path);

        Intent intent = new Intent();
        //intent.setAction(Intent.ACTION_VIEW);
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        intent.setData(mediaUri);
        intent.putExtra(Intent.EXTRA_STREAM, mediaUri);
        intent.setType("image/*");

        context.startActivity(Intent.createChooser(intent, "Share via"));

    }

    public static void openFacebook(Context context, String handle, String id)
    {
        try
        {
            // get the Facebook app if possible
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + id));
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            // no Facebook app, revert to browser
            String url = "https://www.facebook.com/" + handle;
            openUrl(context, url, true);
        }
    }

    public static void openTwitter(Context context, String handle)
    {
        try
        {
            // get the Twitter app if possible
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + handle));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            // no Twitter app, revert to browser
            String url = "https://twitter.com/" + handle;
            openUrl(context, url, true);
        }

    }
}
