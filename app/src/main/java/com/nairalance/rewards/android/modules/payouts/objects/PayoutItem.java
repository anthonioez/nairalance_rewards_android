package com.nairalance.rewards.android.modules.payouts.objects;

import com.miciniti.library.helpers.DateTime;
import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;

public class PayoutItem
{
    public static final int PENDING = 0;

    public long     id;
    public String   provider;
    public String   type;

    public String   name;
    public String   account;

    public int      points;
    public double   amount;

    public String   stamp;
    public int      status;

    public String   datetime;

    public PayoutItem()
    {
        this.id         = 0;
        this.provider   = "";
        this.type       = "";

        this.name       = "";
        this.account    = "";

        this.points     = 0;
        this.amount     = 0;

        this.stamp      = "";

        this.status     = 1;

        this.datetime   = "";
    }

    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.id         = json.getLong(ServerData.id);

            this.points     = json.getInt(ServerData.points);
            this.amount     = json.getDouble(ServerData.amount);

            this.provider   = json.getString(ServerData.title);
            this.type       = json.getString(ServerData.type);

            this.name       = json.getString(ServerData.name);
            this.account    = json.getString(ServerData.account);

            this.stamp      = json.getString(ServerData.stamp);
            this.status     = json.optInt(ServerData.status, 0);

            this.datetime   = DateTime.getDateTimeFormat(DateTime.getLocalTimestamp(DateTime.getTimestamp(stamp)));
            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }

    public String status()
    {
        switch (status)
        {
            case -2:
                return "Rejected";

            case -1:
                return "Cancelled";

            case 0:
                return "Pending";

            case 1:
                return "Paid";

            default:
                return "Unknown";
        }
    }
}

