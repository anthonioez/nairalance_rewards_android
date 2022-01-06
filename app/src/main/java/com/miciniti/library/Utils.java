package com.miciniti.library;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

public class Utils
{
    private static final String TAG = Utils.class.getSimpleName();

    public static final int     KB           = 1024;
    public static final int     MB           = KB * KB;
    public static final int     GB           = KB * MB;

    private static final int    LOAD_RETRIES = 3;
    private static final int    HTTP_TIMEOUT = 30000;

    public static boolean searchString(String needle, String haystack)
    {
        if (haystack.toLowerCase().startsWith(needle.toLowerCase()) || haystack.toLowerCase().contains(needle.toLowerCase()))
            return true;
        else return false;
    }


    public static void sleeper(int i)
    {
        try
        {
            Thread.sleep(i);
        }
        catch (InterruptedException e)
        {
        }
    }

    public static String getID(Context context)
    {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }

    public static String getNetwork(Context context)
    {
        try
        {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getNetworkOperatorName();
        }
        catch (Exception e)
        {

        }
        return "";
    }

    public static String getCountry(Context context)
    {
        try
        {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getNetworkCountryIso();
        }
        catch (Exception e)
        {

        }
        return "";
    }

    public static byte[] streamBytes(InputStream is)
    {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;

        try
        {
            bos = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = is.read(data, 0, data.length)) != -1)
            {
                bos.write(data, 0, nRead);
            }

            bytes = bos.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (bos != null)
                    bos.close();
            }
            catch (Exception e)
            {
            }
        }

        return bytes;
    }

    public static int getPixels(Context context, int dipValue)
    {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
        return px;
    }

    public static int getInt(String input)
    {
        int value = 0;
        try
        {
            value = Integer.parseInt(input);
        }
        catch (Exception e)
        {

        }
        return value;
    }

    public static long getLong(String input)
    {
        long value = 0;
        try
        {
            value = Long.parseLong(input);
        }
        catch (Exception e)
        {

        }
        return value;
    }

    public static double getDouble(String input)
    {
        double value = -1;
        try
        {
            value = Double.parseDouble(input);
        }
        catch (Exception e)
        {

        }
        return value;
    }

    public static float getFloat(String input)
    {
        float value = -1;
        try
        {
            value = Float.parseFloat(input);
        }
        catch (Exception e)
        {

        }
        return value;
    }

    public static byte[] getAsset(Context ctx, String file)
    {
        byte[] res = null;
        InputStream is = null;
        try
        {
            is = ctx.getAssets().open(file);
            res = streamBytes(is);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (is != null)
                    is.close();
            }
            catch (Exception e)
            {
            }
        }
        return res;
    }

    public static void setBitmapImage(Context context, ImageView ivImage, Bitmap bmp)
    {
        if(bmp == null) return;

        int w = Utils.getPixels(context, 48);
        int h = Utils.getPixels(context, 48);

        Bitmap dst = Bitmap.createScaledBitmap(bmp, w, h, true);

        ivImage.setImageBitmap(dst);
    }

    public static Bitmap scaleImage(Bitmap source, int quality, int screenWidth, int screenHeight)
    {
        float ratioX = (float) screenWidth / (float) source.getWidth();
        float ratioY = (float) screenHeight / (float) source.getHeight();
        float ratio = Math.min(ratioX, ratioY);
        float dwidth = ratio * source.getWidth();
        float dheight = ratio * source.getHeight();

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        source.compress(Bitmap.CompressFormat.JPEG, quality, os);
        source.recycle();

        byte[] array = os.toByteArray();

        source = BitmapFactory.decodeByteArray(array, 0, array.length);

        Bitmap bmp = Bitmap.createScaledBitmap(source, (int) dwidth, (int) dheight, true);

        return bmp;
    }

    public static Bitmap loadImage(String file, int targetW, int targetH)
    {
        if (file == null)
            return null;

        File ff = new File(file);
        if(!ff.exists())
            return null;

        //Logger.i(TAG, "loadImage: " + file + " : " + ff.length());

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(file, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        if(photoW <= 0 || photoH <= 0) return null;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(file, bmOptions);

        return bitmap;
    }

    public static String getVersion(Context context)
    {
        String versionCode = "";
        try
        {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return versionCode;
    }

    public static boolean isValidEmail(String target)
    {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static int getDp(Context context, int pixValue)
    {
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, pixValue, r.getDisplayMetrics());
        return px;
    }

    public static float convertPixelsToDp(float px)
    {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static float convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static byte[] readFile(File file)
    {
        byte[] data = null;
        RandomAccessFile rad = null;

        // Open file
        try
        {
            rad = new RandomAccessFile(file, "r");

            // Get and check length
            long longlength = rad.length();
            int length = (int) longlength;
            if (length != longlength) throw new IOException("File size >= 2 GB");

            // Read file and return data
            data = new byte[length];
            rad.readFully(data);
        }
        catch (Exception e)
        {

        }
        finally
        {
            try{if(rad != null) rad.close();}catch (Exception e){}
        }

        return data;
    }

    public static String getUrlPath(Context context, Uri uri)
    {
        try
        {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = { "_data" };
                Cursor cursor = null;

                try {
                    cursor = context.getContentResolver().query(uri, projection, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    // Eat it
                }
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

        }
        catch (Exception e)
        {

        }
        return null;
    }

    public static String getUriToPath(Context context, Uri uri)
    {
        try
        {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = { "_data" };
                Cursor cursor = null;

                try {
                    cursor = context.getContentResolver().query(uri, projection, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    // Eat it
                }
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

        }
        catch (Exception e)
        {

        }
        return null;
    }


    public static void startTask(AsyncTask task)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

    public static void stopTimer(Timer timer)
    {
        if (timer != null)
        {
            try{ timer.cancel(); timer.purge(); }
            catch (Exception e){}
            //timer = null;
        }
    }

    public static String stripHtml(String desc)
    {
        String html = desc;
        html = html.replace("<br>", "\n").replace("<br/>", "\n");

        html = html.replace("<a", "<b").replace("</a>", "</b>");
        html = html.replace("<b><b", "<b").replace("</b></b>", "</b>");
        html = html.replace("target=\"_blank\" ", "");
        html = html.replace("target=\"_blank\"", "");

        return html;
    }


    public static String fixUrl(String url)
    {
        String html = url;

        html = html.replace("http://www.", "");
        html = html.replace("https://www.", "");
        html = html.replace("http://", "");
        html = html.replace("https://", "");

        if(html.endsWith("/")) html = html.substring(0, html.length()-1);

        return html;
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
}
