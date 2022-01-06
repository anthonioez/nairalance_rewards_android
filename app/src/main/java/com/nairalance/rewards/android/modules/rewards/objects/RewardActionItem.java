package com.nairalance.rewards.android.modules.rewards.objects;

import android.text.TextUtils;

import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;

public class RewardActionItem
{
    public long     id;
    public long     reward;

    public String   title;
    public String   type;

    public String   action;
    public String   data;
    public int      points;

    public String   expiry;
    public boolean  claimed;

    public String   info;

    public RewardActionItem()
    {
        this.id = 0;
        this.reward = 0;

        this.title = "";
        this.type = "";

        this.action = "";
        this.data = "";
        this.points = 0;

        this.expiry = "";
        this.claimed = false;
    }

    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.id         = json.getLong(ServerData.id);

            this.data       = json.getString(ServerData.data);
            this.action     = json.getString(ServerData.action);
            this.points     = json.getInt(ServerData.points);

            this.reward     = json.optLong(ServerData.reward, 0);
            this.title      = json.optString(ServerData.title, "");
            this.type       = json.optString(ServerData.type, "");

            this.expiry     = json.optString(ServerData.expiry);
            this.claimed    = json.optBoolean(ServerData.claimed, false);
            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }
}

