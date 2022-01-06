package com.nairalance.rewards.android.modules.rewards.adapters;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.nairalance.rewards.android.modules.rewards.objects.RewardActionItem;
import com.nairalance.rewards.android.utils.Utils;

import java.util.List;

public class AdapterActions extends RecyclerView.Adapter<AdapterActions.AdapterActionsHolder>
{
    private RecyclerOnItemListener listener;
    private Context context;
    private List<RewardActionItem> items;
    public int selectedPosition = -1;

    public AdapterActions(Context context, List<RewardActionItem> items, RecyclerOnItemListener listener)
    {
        super();

        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public AdapterActionsHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_actions, parent, false);

        AdapterActionsHolder viewHolder = new AdapterActionsHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterActionsHolder holder, int position)
    {
        final RewardActionItem item = (RewardActionItem) getItem(position);
        if (item == null)
        {
            holder.textAction.setText("");
            holder.textValue.setText("");
        }
        else
        {
            String title = TextUtils.isEmpty(item.type) ? item.action.toUpperCase() : item.title;
            String points = Utils.formatPoints(item.points);


            if(item.claimed)
            {
                SpannableString value = new SpannableString(points);
                value.setSpan(new StrikethroughSpan(), 0, value.length(), 0);

                SpannableString content = new SpannableString(title);
                content.setSpan(new StrikethroughSpan(), 0, content.length(), 0);

                holder.textAction.setText(content);
                holder.textAction.setTextColor(ContextCompat.getColor(context, R.color.claimed));

                holder.textValue.setText(value);
                holder.textValue.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
            else
            {
                holder.textAction.setText(title);
                holder.textAction.setTextColor(ContextCompat.getColor(context, R.color.white));

                holder.textValue.setText(points);
                holder.textValue.setTextColor(ContextCompat.getColor(context, R.color.white));
            }

            //holder.layoutRow.setBackgroundColor(ContextCompat.getColor(context, selectedPosition == position ? R.color.theme_active  : R.color.transparent));
        }
    }

    public RewardActionItem getItem(int position)
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

    public class AdapterActionsHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Context context;

        LinearLayout layoutRow;
        TextView textAction;
        TextView textValue;

        public AdapterActionsHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            layoutRow       = view.findViewById(R.id.layoutRow);
            textAction      = view.findViewById(R.id.textAction);
            textValue       = view.findViewById(R.id.textValue);

            textAction.setTypeface(Rewards.appFont);
            textValue.setTypeface(Rewards.appFont);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if (listener != null) listener.onItemClick(AdapterActions.this, v, getAdapterPosition());
        }
    }
}