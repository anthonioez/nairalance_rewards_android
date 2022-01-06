package com.nairalance.rewards.android.modules.home.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
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
import com.nairalance.rewards.android.modules.rewards.objects.RewardTypeItem;

import java.util.ArrayList;
import java.util.List;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.AdapterHomeHolder>
{
    private RecyclerOnItemListener listener;
    private Context context;
    private List<RewardTypeItem> items;

    public AdapterHome(Context context, RecyclerOnItemListener listener)
    {
        super();

        this.context = context;
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    @Override
    public AdapterHomeHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_home, parent, false);

        AdapterHomeHolder viewHolder = new AdapterHomeHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterHomeHolder holder, int position)
    {
        final RewardTypeItem item = (RewardTypeItem) getItem(position);
        if (item == null)
        {
            holder.textTitle.setText("");
            holder.textDesc.setText("");
            holder.textCount.setText("");
            holder.imageLogo.setVisibility(View.INVISIBLE);
            holder.progressBar.setVisibility(View.INVISIBLE);
            holder.layoutFrame.setVisibility(View.GONE);
            holder.layoutCount.setVisibility(View.GONE);
        }
        else
        {
            holder.textTitle.setText(Html.fromHtml(item.title));
            holder.textDesc.setText(Html.fromHtml(item.desc));
            holder.imageLogo.setImageResource(Rewards.typeImage(item.type));
            holder.textCount.setText(String.valueOf(item.available));
            holder.layoutCount.setVisibility(item.available > 0 ? View.VISIBLE : View.INVISIBLE);

            holder.layoutFrame.setVisibility(View.VISIBLE);
        }
    }

    public RewardTypeItem getItem(int position)
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
        for(RewardTypeItem item : items)
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

    public void updateList(List<RewardTypeItem> list)
    {
        this.items.clear();
        this.items.addAll(list);

        notifyDataSetChanged();
    }

    public void update()
    {
        notifyDataSetChanged();
    }

    public void setCount(String type, int count)
    {
        for(int pos = 0; pos < items.size(); pos++)
        {
            RewardTypeItem item = items.get(pos);
            if(item != null && item.type != null && item.type.equals(type))
            {
                item.available = count;
                notifyItemChanged(pos);
                break;
            }
        }
    }

    public class AdapterHomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Context context;

        LinearLayout layoutRow;
        TextView textTitle;
        TextView textDesc;
        TextView textCount;

        FrameLayout layoutFrame;
        FrameLayout layoutCount;
        ImageView imageLogo;
        ProgressBar progressBar;

        public AdapterHomeHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            layoutRow       = (LinearLayout) view.findViewById(R.id.layoutRow);
            textTitle       = (TextView) view.findViewById(R.id.textTitle);
            textDesc        = (TextView) view.findViewById(R.id.textDesc);
            textCount       = (TextView) view.findViewById(R.id.textBadge);

            layoutCount     = (FrameLayout) view.findViewById(R.id.layoutCount);
            layoutFrame     = (FrameLayout) view.findViewById(R.id.layoutFrame);
            imageLogo       = (ImageView) view.findViewById(R.id.imageLogo);
            progressBar     = (ProgressBar) view.findViewById(R.id.progressBar);

            textTitle.setTypeface(Rewards.appFont);
            textDesc.setTypeface(Rewards.appFont);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if (listener != null) listener.onItemClick(AdapterHome.this, v, getAdapterPosition());
        }
    }
}