package com.nairalance.rewards.android.modules.earnings.objects;

import com.miciniti.library.helpers.DateTime;
import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;

public class EarningItem
{
    public long     id;
    public String   title;
    public String   action;
    public String   info;
    public String   type;

    public int      points;

    public String   stamp;
    public int      status;

    public String   datetime;

    public EarningItem()
    {
        this.id = 0;
        this.title      = "";
        this.action     = "";
        this.info       = "";
        this.type       = "";

        this.points     = 0;

        this.stamp      = "";

        this.status     = 1;

        this.datetime   = "";
    }

    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.id         = json.getLong(ServerData.id);

            this.title      = json.getString(ServerData.title);
            this.action     = json.getString(ServerData.action);
            this.info       = json.optString(ServerData.info, "");
            this.type       = json.getString(ServerData.type);

            this.points     = json.getInt(ServerData.points);

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
}

