package com.nairalance.rewards.android.modules.profile.objects;

import com.nairalance.rewards.android.io.ServerData;

import org.json.JSONObject;

/**
 * Created by Miciniti on 10/05/2018.
 */

public class ProfileItem
{
    public String   email;
    public String   username;
    public String   phone;
    public String   thumb;

    public int      gender;

    //public double   balance;
    //public double   rewards;
    //public long     ranking;

    public String   city;
    public String   state;
    public boolean  passed;
    public int      flags;

    public ProfileItem()
    {
        username = "";
        phone = "";
        thumb = "";
        email = "";

        gender = 0;

        //balance = 0;
        //rewards = 0;
        //ranking = 0;

        city = "";
        state = "";

        passed = false;
        flags = 0;
    }

    public boolean copyJSON(JSONObject json)
    {
        try
        {
            this.username   = json.getString(ServerData.username);
            this.phone      = json.getString(ServerData.phone);
            this.thumb      = json.getString(ServerData.thumb);
            this.email      = json.optString(ServerData.email);

            this.gender     = json.optInt(ServerData.gender, 0);

            //this.balance    = json.getDouble(ServerData.balance);
            //this.rewards    = json.getDouble(ServerData.rewards);
            //this.ranking    = json.getLong(ServerData.ranking);

            this.city       = json.getString(ServerData.city);
            this.state      = json.getString(ServerData.state);

            this.passed     = json.optBoolean(ServerData.passed, false);
            this.flags      = json.optInt(ServerData.flags, 0);


            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }
}
