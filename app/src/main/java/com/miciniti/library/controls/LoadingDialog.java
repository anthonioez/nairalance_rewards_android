package com.miciniti.library.controls;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Window;


public class LoadingDialog
{
    private static ProgressDialog dialog = null;

    public static void unshow(Activity activity)
    {
        if(activity.isFinishing()) return;

        try
        {
            if(dialog != null && dialog.isShowing())
            {
                //TODO            dialog.getWindow()
                dialog.dismiss();
                dialog = null;
            }
        }
        catch (Exception e)
        {

        }
    }

    public static void show(Activity activity, String message)
    {
        unshow(activity);

        if(activity.isFinishing()) return;

        dialog = new ProgressDialog(activity); //, R.layout.progressdialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void show(Activity activity, String title, String message)
    {
        unshow(activity);

        if(activity.isFinishing()) return;

        dialog = new ProgressDialog(activity); //, R.layout.progressdialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void show(Activity activity, String message, DialogInterface.OnCancelListener listener)
    {
        unshow(activity);

        if(activity.isFinishing()) return;

        dialog = new ProgressDialog(activity); //, R.layout.progressdialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(listener);
        dialog.show();
    }

    public static void dismiss(Activity activity)
    {
        unshow(activity);
    }
}
