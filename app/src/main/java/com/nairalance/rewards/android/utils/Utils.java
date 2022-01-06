package com.nairalance.rewards.android.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;

public class Utils
{
    public static void startTask(AsyncTask task)
    {
        if (Build.VERSION.SDK_INT >= 11)
        {
            //--post GB use serial executor by default --
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //task.execute();
        }
        else
        {
            //--GB uses ThreadPoolExecutor by default--
            task.execute();
        }
    }

    public static boolean isOnline(Context mContext)
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    public static boolean isValidEmail(String target)
    {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void toast(Context context, String message)
    {
        if(TextUtils.isEmpty(message)) return;

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void alert(Context context, String title, String msg)
    {
        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(msg)) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });
        try {
            builder.show();
        }
        catch (Exception e)
        {

        }
    }

    public static void alert(Context context, String title, String msg, final Runnable runnable)
    {
        if(TextUtils.isEmpty(msg)) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();

                if (runnable != null) {
                    runnable.run();
                }
            }
        });

        try {
            builder.show();
        }
        catch (Exception e)
        {

        }
    }


    public static void confirm(Context context, String title, String msg, final Runnable runnableYes, final Runnable runnableNo)
    {
        AlertDialog.Builder dialog;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);//Theme_Material_Dialog_Alert);
        else
            dialog = new AlertDialog.Builder(context);

        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnableYes != null) {
                    runnableYes.run();
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnableNo != null) {
                    runnableNo.run();
                }
            }
        });
        dialog.show();
    }

    public static String responseString(ResponseBody result)
    {
        String str = null;

        try
        {
            str = result.string();
        }
        catch (Exception e)
        {

        }

        return str;
    }

    public static String formatByte(long data)
    {
        float value = data;

        if (value >= 1024 * 1024 * 1024)
            return String.format("%.2f ", value / (1024 * 1024 * 1024)) + "GB";
        else if (value >= 1024 * 1024)
            return String.format("%.2f ", value / (1024 * 1024)) + "MB";
        else if (value >= 1024)
            return String.format("%.2f ", value / 1024) + "KB";
        else
            return String.format("%d ", data) + "B";
    }

    public static String formatDate(long date)
    {
        Date dt = new Date(date);
        return (String) DateFormat.format("dd/MM/yyyy", dt);
    }

    public static String formatDateTime(long date)
    {
        Date dt = new Date(date);
        return (String) DateFormat.format("dd/MM/yyyy hh:mm a", dt);
    }

    public static String getMimeType(String url)
    {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null)
        {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        return type;
    }

    public static int getInt(String input)
    {
        int value = -1;
        try
        {
            value = Integer.parseInt(input);
        }
        catch (Exception e)
        {

        }
        return value;
    }

    public static String joinList(List<String> list, String sep)
    {
        String emails = "";
        for (String email : list)
        {
            emails += (emails.trim().length() == 0 ? "" : sep);
            emails += email;
        }

        return emails;
    }

    public static long getStamp(String stamp)
    {
        long time = 0;
        try
        {
            if(stamp == null || stamp.length() == 0) return 0;

            //stamp = stamp.replace("T", " ").replace("Z", "");

            time = Timestamp.valueOf(stamp).getTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return time;
    }

    public static String getSHA1(final String s)
    {
        try
        {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA1");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
            {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatMoney(double amount, int prec)
    {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        Currency currency = Currency.getInstance("NGN");

        format.setCurrency(currency);
        format.setMinimumFractionDigits(prec);
        format.setMaximumFractionDigits(prec);

        String str = format.format(amount);
        str = str.replace("NGN", "₦");
        return str;
    }

    public static String formatPoints(int points)
    {
        return String.valueOf(points);  //TODO format points
    }

    public static String formatRanking(long ranking)
    {
        return ranking == -1 ? "-" : String.valueOf(ranking);
    }
}
