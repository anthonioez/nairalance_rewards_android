package com.nairalance.rewards.android.modules.payouts.objects;

import com.miciniti.library.helpers.DateTime;
import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;

public class PayoutTypeItem
{
    public long     id;
    public String   provider;
    public String   type;

    public String   name;
    public String   account;

    public PayoutTypeItem()
    {
        this.id         = 0;

        this.provider   = "";
        this.type       = "";

        this.name       = "";
        this.account    = "";
    }

    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.id         = json.getLong(ServerData.id);

            this.provider   = json.getString(ServerData.provider);
            this.type       = json.getString(ServerData.type);

            this.name       = json.getString(ServerData.name);
            this.account    = json.getString(ServerData.account);

            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }

    @Override
    public String toString()
    {
        return provider;
    }
}

