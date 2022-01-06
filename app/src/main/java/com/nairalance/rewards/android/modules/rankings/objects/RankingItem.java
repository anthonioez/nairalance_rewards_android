package com.nairalance.rewards.android.modules.rankings.objects;

import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;

public class RankingItem
{
    public long     id;
    public String   username;
    public String   image;
    public long     ranking;
    public int      rewards;

    public RankingItem()
    {
        this.id = 0;
        this.username   = "";
        this.image      = "";
        this.ranking    = 0;
        this.rewards    = 0;
    }

    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.id         = json.getLong(ServerData.id);

            this.username   = json.getString(ServerData.username);
            this.rewards    = json.getInt(ServerData.rewards);
            this.image      = json.getString(ServerData.image);
            this.ranking    = json.getLong(ServerData.ranking);

            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }
}

