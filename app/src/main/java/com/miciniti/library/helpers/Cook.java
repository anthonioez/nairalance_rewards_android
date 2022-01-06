package com.miciniti.library.helpers;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import ng.streetwize.android.Rewards;
//import ng.streetwize.android.Prefs;
//import okhttp3.Cookie;

/**
 * Created by Miciniti onEvent 23/07/16.
 */
public class Cook
{
    /*
    private static final String TAG = Cook.class.getSimpleName();

    public static final HashMap<String, Cookie> cookieStore = new HashMap<>();

    public static boolean cookieChanged = false;

    public static void load(Context context)
    {
        String data = Prefs.getCookies(context);

        cookieStore.clear();

        try {
            JSONArray jsonData = new JSONArray(new JSONTokener(data));

            for(int i = 0; i < jsonData.length(); i++)
            {
                JSONObject jsonObject = jsonData.getJSONObject(i);
                Cookie cookie = json2cookie(jsonObject);
                if(cookie == null) continue;
                //log(cookie, "loading**************");
                cookieStore.put(cookie.name(), cookie);
            }
        }
        catch (Exception e)
        {

        }
    }

    public static void save()
    {
        if(!cookieChanged) return;

        JSONArray jsonData = new JSONArray();

        for (Map.Entry<String, Cookie> entry : cookieStore.entrySet())
        {
            Cookie cookie = entry.getValue();

            JSONObject json = cookie2json(cookie);
            if(json == null) continue;
            //log(cookie, "saving**************");
            jsonData.put(json);
        }

        String data = jsonData.toString();

        Prefs.setCookies(Rewards.appContext, data);

        cookieChanged = false;
    }


    private static Cookie json2cookie(JSONObject json)
    {
        try
        {
            Cookie.Builder builder = new Cookie.Builder();
            builder.name(json.getString("name"));
            builder.value(json.getString("value"));
            builder.domain(json.getString("domain"));
            builder.path(json.getString("path"));

            builder.expiresAt(json.getLong("expiry"));

            return builder.build();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private static JSONObject cookie2json(Cookie cookie)
    {
        try {
            JSONObject json = new JSONObject();
            json.put("name", cookie.name());
            json.put("value", cookie.value());
            json.put("domain", cookie.domain());
            json.put("path", cookie.path());

            json.put("expiry", cookie.expiresAt());

            return json;
        }
        catch (Exception e)
        {
            return null;
        }
    }


    public static Collection<Cookie> list()
    {
        return cookieStore.values();
    }

    public static void add(List<Cookie> cookies)
    {
        for (Cookie cookie : cookies)
        {
            //log(cookie, "incoming**************");

            cookieStore.put(cookie.name(), cookie);
        }

        cookieChanged = true;
    }

    public static void remove(List<Cookie> cookies)
    {
        if(cookies.size() == 0) return;

        for (Cookie cookie : cookies)
        {
            //log(cookie, "expired**************");
            cookieStore.remove(cookie.name());
        }


        cookieChanged = true;
    }

    //Print the values of cookies - Useful for testing
    public static void log(Cookie cookie, String header)
    {
        //Logger.e(TAG, header);
        //Logger.e(TAG, "String: " + cookie.toString());
        //Log.e(TAG, "Expires: " + cookie.expiresAt());
        //Log.e(TAG, "Hash: " + cookie.hashCode());
        //Log.e(TAG, "Path: " + cookie.path());
        //Log.e(TAG, "Domain: " + cookie.domain());
        //Log.e(TAG, "Name: " + cookie.name());
        //Log.e(TAG, "Value: " + cookie.value());
    }*/
}
