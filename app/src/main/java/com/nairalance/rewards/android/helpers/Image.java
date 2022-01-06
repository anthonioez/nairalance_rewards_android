package com.nairalance.rewards.android.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.R;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.RequestCreator;

/**
 * Created by Miciniti onEvent 22/05/16.
 */
public class Image
{
    public static int IMAGE_SMALL_SIZE = 256;
    public static int IMAGE_MEDIUM_SIZE = 640;

    public static void cancel(Context context, ImageView imageView)
    {
        Rewards.picasso.cancelRequest(imageView);
    }

    public static void loadSmall(Context context, String uri, ImageView imageView, boolean fresh, Callback callback)
    {
        Rewards.picasso.cancelRequest(imageView);

        if(uri == null || uri.length() == 0) return;

        if(fresh)
        {
            Rewards.picasso.load(uri)
                    //.error(R.drawable.errorholder)
                    .placeholder(R.drawable.blank)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .resize(IMAGE_SMALL_SIZE, IMAGE_SMALL_SIZE)
                    .centerInside()
                    .into(imageView, callback);
        }
        else
        {
            Rewards.picasso.load(uri)
                    //.error(R.drawable.errorholder)
                    .placeholder(R.drawable.blank)
                    .resize(IMAGE_SMALL_SIZE, IMAGE_SMALL_SIZE)
                    .centerInside()
                    .into(imageView, callback);
        }
    }

    public static void loadMedium(Context context, String uri, ImageView imageView, boolean fresh, Callback callback)
    {
        Rewards.picasso.cancelRequest(imageView);
        if(uri == null || uri.length() == 0) return;

        if(fresh)
        {
            Rewards.picasso.load(uri)//.error(R.drawable.placeholder).placeholder(R.drawable.placeholder)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .resize(IMAGE_MEDIUM_SIZE, IMAGE_MEDIUM_SIZE)
                    .centerInside()
                    .into(imageView, callback);
        }
        else
        {
            Rewards.picasso.load(uri)//.error(R.drawable.placeholder).placeholder(R.drawable.placeholder)
                    .resize(IMAGE_MEDIUM_SIZE, IMAGE_MEDIUM_SIZE)
                    .centerInside()
                    .into(imageView, callback);
        }
    }

    public static void loadFull(Context context, String uri, int placeholder, ImageView imageView, boolean fresh, Callback callback)
    {
        Rewards.picasso.cancelRequest(imageView);

        if(uri == null || uri.length() == 0) return;

        RequestCreator request = Rewards.picasso.load(uri);

        if(placeholder != 0)
            request = request.placeholder(placeholder);
        else
            request = request.noPlaceholder();

        if(fresh)
        {
            //.error(R.drawable.placeholder).placeholder(R.drawable.placeholder)
            request = request
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE);
        }
        else
        {
            //.error(R.drawable.placeholder).placeholder(R.drawable.placeholder)
        }

        request.into(imageView, callback);

    }

    public static void fetch(Context context, String uri, boolean fresh)
    {
        if(uri == null || uri.length() == 0) return;

        if(fresh)
        {
            Rewards.picasso.load(uri)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .fetch();
        }
        else
        {
            Rewards.picasso.load(uri)
                    .fetch();
        }
    }


    public static void clear(Context context, String url)
    {
        if(url == null || url.length() == 0) return;

        Rewards.picasso.invalidate(url);
    }

    public static void clear(Context context, java.io.File file)
    {
        if(file == null) return;

        Rewards.picasso.invalidate(file);
    }

    public static void reset()
    {

    }

}
