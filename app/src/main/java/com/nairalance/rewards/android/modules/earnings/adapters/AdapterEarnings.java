package com.nairalance.rewards.android.modules.earnings.adapters;

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
import com.nairalance.rewards.android.modules.earnings.objects.EarningItem;
import com.nairalance.rewards.android.utils.Utils;

import java.util.List;

public class AdapterEarnings extends RecyclerView.Adapter<AdapterEarnings.AdapterRewardsHolder>
{
    private RecyclerOnItemListener listener;
    private Context context;
    private List<EarningItem> items;

    private int selectedPosition = -1;

    public AdapterEarnings(Context context, List<EarningItem> items, RecyclerOnItemListener listener)
    {
        super();

        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public AdapterRewardsHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_earnings, parent, false);

        AdapterRewardsHolder viewHolder = new AdapterRewardsHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterRewardsHolder holder, int position)
    {
        final EarningItem item = (EarningItem) getItem(position);
        if (item == null)
        {
            holder.textTitle.setText("");
            holder.textAction.setText("");
            holder.textValue.setText("");
            holder.imageThumb.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.textTitle.setText(item.title);
            holder.textAction.setText(position == selectedPosition ? item.datetime : String.format("%s%s%s", item.action.toUpperCase(), TextUtils.isEmpty(item.info) ? "" : " - ", item.info));
            holder.textValue.setText(Utils.formatPoints(item.points));

            holder.imageThumb.setImageResource(Rewards.typeImage(item.type));

            holder.layoutPad.setVisibility(position == getItemCount() - 1 ? View.VISIBLE : View.GONE);

            holder.textAction.setVisibility(View.VISIBLE);
            holder.imageThumb.setVisibility(View.VISIBLE);
            holder.layoutRow.setVisibility(View.VISIBLE);
        }
    }

    public EarningItem getItem(int position)
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
        TextView textTitle;
        TextView textAction;
        TextView textValue;

        ImageView imageThumb;

        public AdapterRewardsHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            layoutRow       = view.findViewById(R.id.layoutRow);
            layoutPad       = view.findViewById(R.id.layoutPad);
            imageThumb      = view.findViewById(R.id.imageThumb);

            textTitle       = view.findViewById(R.id.textTitle);
            textAction      = view.findViewById(R.id.textAction);
            textValue       = view.findViewById(R.id.textValue);

            textTitle.setTypeface(Rewards.appFont);
            textAction.setTypeface(Rewards.appFontLight);
            textValue.setTypeface(Rewards.appFont);

            layoutRow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int pos = getAdapterPosition();

            if(pos == selectedPosition)
                selectedPosition = -1;
            else
                selectedPosition = pos;

            notifyDataSetChanged();

            if (listener != null) listener.onItemClick(AdapterEarnings.this, v, pos);
        }
    }
}