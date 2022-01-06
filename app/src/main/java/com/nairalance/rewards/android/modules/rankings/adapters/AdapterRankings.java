package com.nairalance.rewards.android.modules.rankings.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.nairalance.rewards.android.helpers.Image;
import com.nairalance.rewards.android.modules.rankings.objects.RankingItem;
import com.nairalance.rewards.android.utils.Utils;
import com.squareup.picasso.Callback;

import java.util.List;

public class AdapterRankings extends RecyclerView.Adapter<AdapterRankings.AdapterRewardsHolder>
{
    private RecyclerOnItemListener listener;
    private Context context;
    private List<RankingItem> items;

    public AdapterRankings(Context context, List<RankingItem> items, RecyclerOnItemListener listener)
    {
        super();

        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public AdapterRewardsHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rankings, parent, false);

        AdapterRewardsHolder viewHolder = new AdapterRewardsHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterRewardsHolder holder, int position)
    {
        final RankingItem item = (RankingItem) getItem(position);
        if (item == null)
        {
            holder.textUsername.setText("");
            holder.textPosition.setText("");
            holder.textValue.setText("");
            holder.imageThumb.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.textUsername.setText(item.username);
            holder.textPosition.setText(String.valueOf(item.ranking));
            holder.textValue.setText(Utils.formatPoints(item.rewards));

            if(!TextUtils.isEmpty(item.image))
            {
                holder.imageThumb.setVisibility(View.GONE);
                holder.imageHolder.setVisibility(View.VISIBLE);

                Image.loadFull(context, item.image, 0, holder.imageThumb, false, new Callback()
                {
                    @Override
                    public void onSuccess()
                    {
                        holder.imageThumb.setVisibility(View.VISIBLE);
                        holder.imageHolder.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e)
                    {
                        holder.imageThumb.setVisibility(View.GONE);
                        holder.imageHolder.setVisibility(View.VISIBLE);
                    }
                });
            }
            else
            {
                holder.imageThumb.setVisibility(View.GONE);
                holder.imageHolder.setVisibility(View.VISIBLE);
            }

            holder.layoutPad.setVisibility(position == getItemCount() - 1 ? View.VISIBLE : View.GONE);
            holder.layoutRow.setVisibility(View.VISIBLE);
        }
    }

    public RankingItem getItem(int position)
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
        for(RankingItem item : items)
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
        LinearLayout layoutPad;
        TextView textPosition;
        TextView textUsername;
        TextView textValue;

        ImageView imageHolder;
        ImageView imageThumb;

        public AdapterRewardsHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            layoutRow       = view.findViewById(R.id.layoutRow);
            layoutPad       = view.findViewById(R.id.layoutPad);
            textPosition    = view.findViewById(R.id.textPosition);
            imageHolder     = view.findViewById(R.id.imageHolder);
            imageThumb      = view.findViewById(R.id.imageThumb);
            textUsername    = view.findViewById(R.id.textUsername);
            textValue       = view.findViewById(R.id.textValue);

            textUsername.setTypeface(Rewards.appFont);
            textPosition.setTypeface(Rewards.appFontLight);
            textValue.setTypeface(Rewards.appFont);

            view.setOnClickListener(this);
            layoutRow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(v == layoutRow)
            {
                if (listener != null) listener.onMenuClick(AdapterRankings.this, v, getAdapterPosition());
            }
            else
            {
                if (listener != null) listener.onItemClick(AdapterRankings.this, v, getAdapterPosition());
            }
        }
    }
}