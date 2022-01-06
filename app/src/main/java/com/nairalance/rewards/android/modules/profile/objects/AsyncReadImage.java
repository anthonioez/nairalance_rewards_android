package com.nairalance.rewards.android.modules.profile.objects;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import com.miciniti.library.controls.LoadingDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AsyncReadImage extends AsyncTask<Object, Object, Boolean>
{
    private AsyncReadImageListener listener;

    private Activity context;
    private Object obj;
    private Bitmap imageBitmap = null;
    private byte[] imageData = null;

    public AsyncReadImage(Activity context, Object obj, AsyncReadImageListener listener)
    {
        this.context = context;
        this.obj = obj;
        this.listener = listener;
    }


    @Override
    public void onPreExecute()
    {
        super.onPreExecute();

        if(listener != null) listener.imageStarted();
    }

    @Override
    public Boolean doInBackground(Object... params)
    {
        if(obj instanceof Bitmap)
            return readBitmap((Bitmap) obj);
        else if(obj instanceof Uri)
            return readUrl((Uri) obj);
        else if(obj instanceof File)
            return readFile((File) obj);
        return false;
    }

    private boolean readBitmap(Bitmap bmp)
    {
        boolean ret = false;

        imageBitmap = bmp;

        ByteArrayOutputStream baos = null;
        FileOutputStream os = null;
        try
        {
            baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);

            imageData = baos.toByteArray();

            ret = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try{ if(os != null) os.close(); } catch (Exception e){}
            try{ if(baos != null) baos.close(); } catch (Exception e){}

        }
        return ret;
    }

    private boolean readUrl(Uri uri)
    {
        boolean ret = false;

        ByteArrayOutputStream baos = null;
        try
        {
            imageBitmap = resizedBitmap(uri);
            if(imageBitmap != null)
            {
                baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG,75, baos);

                imageData = baos.toByteArray();

                ret = true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret = false;
        }
        finally
        {
            try{ if(baos != null) baos.close(); } catch (Exception e){}
        }

        return ret;
    }

    private boolean readFile(File file)
    {
        boolean ret = false;
        ByteArrayOutputStream baos = null;
        try
        {
            imageBitmap = resizedBitmap(file.getAbsolutePath());
            if(imageBitmap != null)
            {
                baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG,75, baos);

                imageData = baos.toByteArray();

                ret = true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            ret = false;
        }
        finally
        {
            try{ if(baos != null) baos.close(); } catch (Exception e){}
        }

        return ret;
    }

    private Bitmap resizedBitmap(Object input)
    {
        int targetW = 640;
        int targetH = 640;

        InputStream is = null;
        Bitmap bitmap = null;
        try
        {
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            if(input instanceof String)
            {
                BitmapFactory.decodeFile((String)input, bmOptions);
            }
            else if(input instanceof Uri)
            {
                is = context.getContentResolver().openInputStream((Uri)input);
                BitmapFactory.decodeStream(is, null, bmOptions);
            }
            else
            {
                return null;
            }

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            if(input instanceof String)
            {
                bitmap = BitmapFactory.decodeFile((String)input, bmOptions);
            }
            else if(input instanceof Uri)
            {
                is = context.getContentResolver().openInputStream((Uri)input);
                bitmap = BitmapFactory.decodeStream(is, null, bmOptions);
            }
        }
        catch (Exception e)
        {

        }
        finally
        {
            try{ if(is != null) is.close(); } catch (Exception e){}
        }

        return bitmap;
    }

    @Override
    public void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);

        if(result != null && result.booleanValue())
        {
            if(listener != null) listener.imageDone(imageBitmap, imageData);
        }
        else
        {
            if(listener != null) listener.imageFailed("");
        }
    }

    public interface AsyncReadImageListener
    {
        void imageStarted();
        void imageDone(Bitmap bmp, byte[] data);
        void imageFailed(String error);
    }
}



