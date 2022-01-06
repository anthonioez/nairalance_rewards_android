package com.nairalance.rewards.android.modules.rewards.objects;

import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;

public class RewardTypeItem
{
    public long     id;
    public long     reward;

    public String   title;
    public String   desc;
    public String   type;

    public String   data;
    public String   actions;
    public int      available;

    public int      order;

    public RewardTypeItem()
    {
        this.id = 0;
        this.reward = 0;

        this.title = "";
        this.desc = "";
        this.type = "";

        this.data = "";
        this.actions = "";
        this.available = 0;

        this.order = 0;
    }


    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.id         = json.getLong(ServerData.id);
            this.reward     = json.getLong(ServerData.reward);

            this.title      = json.getString(ServerData.title);
            this.desc       = json.getString(ServerData.desc);
            this.type       = json.getString(ServerData.type);

            this.data       = json.optString(ServerData.data);
            this.actions    = json.getString(ServerData.actions);

            this.available  = json.getInt(ServerData.available);
            this.order      = json.getInt(ServerData.order);

            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }
}

