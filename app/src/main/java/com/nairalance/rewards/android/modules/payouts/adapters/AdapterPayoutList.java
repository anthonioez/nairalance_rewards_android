package com.nairalance.rewards.android.modules.payouts.adapters;

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
import com.nairalance.rewards.android.modules.payouts.objects.PayoutItem;
import com.nairalance.rewards.android.utils.Utils;

import java.util.List;

public class AdapterPayoutList extends RecyclerView.Adapter<AdapterPayoutList.AdapterPayoutsHolder>
{
    private RecyclerOnItemListener listener;
    private Context context;
    private List<PayoutItem> items;

    public AdapterPayoutList(Context context, List<PayoutItem> items, RecyclerOnItemListener listener)
    {
        super();

        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public AdapterPayoutsHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_payout_list, parent, false);

        AdapterPayoutsHolder viewHolder = new AdapterPayoutsHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterPayoutsHolder holder, int position)
    {
        final PayoutItem item = (PayoutItem) getItem(position);
        if (item == null)
        {
            holder.textTitle.setText("");
            holder.textDesc.setText("");
            holder.textValue.setText("");
            holder.textStamp.setText("");
            holder.textStatus.setText("");
        }
        else
        {
            holder.textTitle.setText(item.provider);
            holder.textDesc.setText(String.format("%s%s", item.account, TextUtils.isEmpty(item.name) ? "" : String.format(" - %s", item.name)));
            holder.textValue.setText(Utils.formatMoney(item.amount, 2));
            holder.textStatus.setText(item.status().toUpperCase());
            holder.textStamp.setText(item.datetime);

            holder.imageThumb.setImageResource(Rewards.providerImage(item.type));
        }
    }

    public PayoutItem getItem(int position)
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

    public class AdapterPayoutsHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Context context;

        LinearLayout layoutRow;
        TextView textTitle;
        TextView textDesc;
        TextView textValue;
        TextView textStatus;
        TextView textStamp;

        ImageView imageThumb;

        public AdapterPayoutsHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            layoutRow       = view.findViewById(R.id.layoutRow);
            imageThumb      = view.findViewById(R.id.imageThumb);

            textTitle       = view.findViewById(R.id.textTitle);
            textDesc        = view.findViewById(R.id.textDesc);
            textValue       = view.findViewById(R.id.textValue);
            textStatus      = view.findViewById(R.id.textStatus);
            textStamp       = view.findViewById(R.id.textStamp);

            textTitle.setTypeface(Rewards.appFont);
            textDesc.setTypeface(Rewards.appFontLight);
            textValue.setTypeface(Rewards.appFont);
            textStatus.setTypeface(Rewards.appFontLight);
            textStamp.setTypeface(Rewards.appFontLight);

            layoutRow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            int pos = getAdapterPosition();
            if (listener != null) listener.onItemClick(AdapterPayoutList.this, v, pos);
        }
    }
}