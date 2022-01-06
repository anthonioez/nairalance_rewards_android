package com.nairalance.rewards.android.modules.rewards.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.nairalance.rewards.android.helpers.Image;
import com.nairalance.rewards.android.modules.rewards.objects.RewardItem;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

public class AdapterRewards extends RecyclerView.Adapter<AdapterRewards.AdapterRewardsHolder>
{
    private RecyclerOnItemListener listener;
    private Context context;
    private ArrayList<RewardItem> items;

    public AdapterRewards(Context context, ArrayList<RewardItem> items, RecyclerOnItemListener listener)
    {
        super();

        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public AdapterRewardsHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rewards, parent, false);

        AdapterRewardsHolder viewHolder = new AdapterRewardsHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterRewardsHolder holder, int position)
    {
        final RewardItem item = (RewardItem) getItem(position);
        if (item == null)
        {
            holder.textTitle.setText("");
            holder.textDesc.setText("");
            holder.imageLogo.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.layoutFrame.setVisibility(View.GONE);
        }
        else
        {
            holder.textTitle.setText(item.title);
            holder.textDesc.setText(item.desc);

            holder.imageHolder.setImageResource(Rewards.typeImage(item.type));
            if(!TextUtils.isEmpty(item.image))
            {
                Image.loadSmall(context, item.image, holder.imageLogo, false, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        holder.imageHolder.setVisibility(View.GONE);
                        holder.imageLogo.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e)
                    {
                        holder.imageHolder.setVisibility(View.VISIBLE);
                        holder.imageLogo.setVisibility(View.GONE);
                    }
                });
            }
            else
            {
                holder.imageHolder.setVisibility(View.VISIBLE);
                holder.imageLogo.setVisibility(View.GONE);
            }

            holder.layoutFrame.setVisibility(View.VISIBLE);
        }
    }

    public RewardItem getItem(int position)
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
            return items.get(position).id;
        else
            return -1;
    }

    public int indexByID(long id)
    {
        int index = 0;
        for(RewardItem item : items)
        {
            if(item.id == id)
            {
                return index;
            }

            index++;
        }

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

        LinearLayout layoutRow;
        TextView textTitle;
        TextView textDesc;

        FrameLayout layoutFrame;
        ImageView imageHolder;
        ImageView imageLogo;
        ProgressBar progressBar;

        public AdapterRewardsHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            layoutRow       = (LinearLayout) view.findViewById(R.id.layoutRow);
            textTitle       = (TextView) view.findViewById(R.id.textTitle);
            textDesc        = (TextView) view.findViewById(R.id.textDesc);

            layoutFrame     = (FrameLayout) view.findViewById(R.id.layoutFrame);
            imageHolder     = (ImageView) view.findViewById(R.id.imageHolder);
            imageLogo       = (ImageView) view.findViewById(R.id.imageLogo);
            progressBar     = (ProgressBar) view.findViewById(R.id.progressBar);

            textTitle.setTypeface(Rewards.appFont);
            textDesc.setTypeface(Rewards.appFont);

            view.setOnClickListener(this);
            layoutFrame.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(v == layoutFrame)
            {
                if (listener != null) listener.onMenuClick(AdapterRewards.this, v, getAdapterPosition());
            }
            else
            {
                if (listener != null) listener.onItemClick(AdapterRewards.this, v, getAdapterPosition());
            }
        }
    }
}