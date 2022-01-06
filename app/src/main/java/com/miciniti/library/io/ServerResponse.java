package com.miciniti.library.io;


import android.content.Context;

import org.json.JSONException;

import com.nairalance.rewards.android.R;

public class ServerResponse {

    public int code;
    public byte[] data;
    public String error;

    public ServerResponse()
    {
        code = 0;
        data = null;
        error = null;
    }

    public String getError(Exception e, Context mContext)
    {
        if(e instanceof JSONException)
        {
            return mContext.getString(R.string.err_server_json);
        }
        else
        {
            return (error != null ? error : mContext.getString(R.string.err_occurred));
        }
    }
}
