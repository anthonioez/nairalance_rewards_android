package com.nairalance.rewards.android.modules.help.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.modules.help.objects.HowItem;

import java.util.List;

public class AdapterHow extends RecyclerView.Adapter<AdapterHow.AdapterRewardsHolder>
{
    private Context context;
    private List<HowItem> items;

    public AdapterHow(Context context, List<HowItem> items)
    {
        super();

        this.context = context;
        this.items = items;
    }

    @Override
    public AdapterRewardsHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_how, parent, false);

        AdapterRewardsHolder viewHolder = new AdapterRewardsHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterRewardsHolder holder, int position)
    {
        final HowItem item = (HowItem) getItem(position);
        if (item == null)
        {
            holder.textTitle.setText("");
            holder.textDesc.setText("");
        }
        else
        {
            holder.textTitle.setText(item.title);
            holder.textDesc.setText(item.desc);
            holder.imageThumb.setImageResource(item.icon);
        }
    }

    public HowItem getItem(int position)
    {
        if (position >= 0 && position < items.size())
            return items.get(position);
        else
            return null;
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    @Override
    public long getItemId(int position)
    {
        if (position >= 0 && position < items.size())
            return items.get(position).icon;
        else
            return -1;
    }

    public void remove(int index)
    {
        if(index < 0 || index >= items.size()) return;

        items.remove(index);
        notifyItemRemoved(index);
    }

    public void clear(boolean notify)
    {
        items.clear();

        if(notify) notifyDataSetChanged();
    }

    public void update()
    {
        notifyDataSetChanged();
    }

    public class AdapterRewardsHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Context context;

        ImageView imageThumb;
        TextView textTitle;
        TextView textDesc;


        public AdapterRewardsHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            imageThumb      = view.findViewById(R.id.imageThumb);

            textTitle       = view.findViewById(R.id.textTitle);
            textDesc        = view.findViewById(R.id.textDesc);

            textTitle.setTypeface(Rewards.appFontBold);
            textDesc.setTypeface(Rewards.appFont);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
        }
    }
}