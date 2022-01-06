package com.nairalance.rewards.android.modules.payouts.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.miciniti.library.listeners.RecyclerOnItemListener;
import com.nairalance.rewards.android.modules.payouts.objects.PayoutRateItem;
import com.nairalance.rewards.android.utils.Utils;

import java.util.List;

public class AdapterPayoutData extends RecyclerView.Adapter<AdapterPayoutData.AdapterPayoutsHolder>
{
    private RecyclerOnItemListener listener;
    private Context context;
    private List<PayoutRateItem> items;

    private int selectedPosition = -1;

    public AdapterPayoutData(Context context, List<PayoutRateItem> items, RecyclerOnItemListener listener)
    {
        super();

        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public AdapterPayoutsHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_payout_data, parent, false);

        AdapterPayoutsHolder viewHolder = new AdapterPayoutsHolder(context, view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterPayoutsHolder holder, int position)
    {
        final PayoutRateItem item = getItem(position);
        if (item == null)
        {
            holder.textTitle.setText("");
            holder.textDesc.setText("");
            holder.textValue.setText("");
        }
        else
        {
            holder.textTitle.setText(Rewards.payoutType(item.type));
            holder.textValue.setText(Utils.formatMoney(item.amount, 0));
            holder.textDesc.setText(String.format("%s points", Utils.formatPoints(item.points)));

            holder.imageThumb.setImageResource(Rewards.providerImage(item.type));

            holder.layoutPad.setVisibility(position == getItemCount() - 1 ? View.VISIBLE : View.GONE);
        }
    }

    public PayoutRateItem getItem(int position)
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
        LinearLayout layoutPad;
        TextView textTitle;
        TextView textDesc;
        TextView textValue;

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
            layoutPad       = view.findViewById(R.id.layoutPad);

            textTitle.setTypeface(Rewards.appFont);
            textDesc.setTypeface(Rewards.appFontLight);
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

            if (listener != null) listener.onItemClick(AdapterPayoutData.this, v, pos);
        }
    }
}