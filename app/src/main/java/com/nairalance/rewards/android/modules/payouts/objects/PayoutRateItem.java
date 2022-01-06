package com.nairalance.rewards.android.modules.payouts.objects;

import com.miciniti.library.helpers.DateTime;
import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;

public class PayoutRateItem
{
    public long     id;

    public String   info;
    public String   type;

    public int      points;
    public double   amount;

    public PayoutRateItem()
    {
        this.id         = 0;

        this.info       = "";
        this.type       = "";

        this.points     = 0;
        this.amount     = 0;
    }

    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.id         = json.getLong(ServerData.id);

            this.points     = json.getInt(ServerData.points);
            this.amount     = json.getDouble(ServerData.amount);

            this.info       = json.getString(ServerData.info);
            this.type       = json.getString(ServerData.type);

            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }
}

