package com.nairalance.rewards.android.modules.home.objects;

import android.content.Context;
import android.text.TextUtils;

import com.nairalance.rewards.android.Prefs;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.earnings.objects.EarningItem;
import com.nairalance.rewards.android.modules.rewards.Reward;
import com.nairalance.rewards.android.modules.rewards.objects.RewardTypeItem;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Miciniti on 08/05/2018.
 */

public class HomeItem
{
    public String username;
    public String thumb;
    public int rewards;
    public long ranking;

    public List<RewardTypeItem> items;

    public HomeItem()
    {
        username    = "";
        thumb       = "";

        ranking     = 0;
        rewards     = 0;

        items       = new ArrayList<>();
    }


    public boolean copyJSON(Context context, JSONObject json)
    {
        try
        {
            this.username   = json.getString(ServerData.username);
            this.thumb      = json.getString(ServerData.thumb);

            this.ranking    = json.getLong(ServerData.ranking);
            this.rewards    = json.getInt(ServerData.rewards);

            this.items      = new ArrayList<>();

            JSONArray jsonList = json.getJSONArray(ServerData.list);
            for(int i = 0; i < jsonList.length(); i++)
            {
                RewardTypeItem item = new RewardTypeItem();
                if(item.copyJSON(jsonList.getJSONObject(i)))
                {
                    if(item.type.equals("admob"))
                    {
                        if(!TextUtils.isEmpty(item.data))
                        {
                            Rewards.rewardAdmobId = item.data;
                        }
                        item.available = 0;
                    }
                    else if(item.type.equals("youtube"))
                    {
                        if(!TextUtils.isEmpty(item.data))
                        {
                            //Rewards.youtubeApiKey = item.data;
                        }
                    }

                    items.add(item);
                }
            }

            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }
}
