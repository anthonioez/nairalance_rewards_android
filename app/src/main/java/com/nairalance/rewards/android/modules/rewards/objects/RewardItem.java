package com.nairalance.rewards.android.modules.rewards.objects;

import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;
import org.json.JSONTokener;

public class RewardItem
{
    public long     id;

    public String   title;
    public String   desc;
    public String   type;

    public String   image;
    public String   link;
    public String   data;

    public String   expiry;

    public RewardItem()
    {
        this.id = 0;

        this.title = "";
        this.desc = "";
        this.type = "";

        this.image = "";
        this.link = "";
        this.data = "";

        this.expiry = "";
    }

    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.id         = json.getLong(ServerData.id);

            this.title      = json.getString(ServerData.title);
            this.desc       = json.getString(ServerData.desc);
            this.type       = json.getString(ServerData.type);

            this.image      = json.getString(ServerData.image);
            this.link       = json.getString(ServerData.link);
            this.data       = json.getString(ServerData.data);

            this.expiry     = json.getString(ServerData.expiry);
            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }


    public JSONObject toJSON()
    {
        try
        {
            JSONObject json = new JSONObject();

            json.put(ServerData.id, this.id);

            json.put(ServerData.title, this.title);
            json.put(ServerData.desc, this.desc);
            json.put(ServerData.type, this.type);

            json.put(ServerData.image, this.image);
            json.put(ServerData.link, this.link);
            json.put(ServerData.data, this.data);

            json.put(ServerData.expiry, this.expiry);

            return json;
        }
        catch (Exception e)
        {

        }
        return null;
    }

    public boolean fromJSON(String string)
    {
        try
        {
            JSONObject json = new JSONObject(new JSONTokener(string));

            if(copyJSON(json))
            {
                return true;
            }
        }
        catch (Exception e)
        {

        }

        return false;
    }
}

