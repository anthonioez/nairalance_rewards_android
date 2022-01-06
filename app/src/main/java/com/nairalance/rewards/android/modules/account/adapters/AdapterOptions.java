package com.nairalance.rewards.android.modules.account.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.modules.account.objects.OptionItem;
import com.nairalance.rewards.android.modules.account.objects.OptionListener;

import java.util.List;

public class AdapterOptions extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private boolean hasArrow = false;
    private Context context;
    private List<OptionItem> items;

    private OptionListener listener;

    public AdapterOptions(Context context, List<OptionItem> items, boolean hasArrow, OptionListener listener)
    {
        super();

        this.context = context;
        this.items = items;
        this.listener = listener;
        this.hasArrow = hasArrow;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case OptionItem.Header:
                View viewHeader = LayoutInflater.from(context).inflate(R.layout.adapter_option_header, parent, false);
                AdapterOptionsHeaderHolder viewHeaderHolder = new AdapterOptionsHeaderHolder(context, viewHeader);
                return viewHeaderHolder;

            default:
                View view = LayoutInflater.from(context).inflate(R.layout.adapter_option_item, parent, false);
                AdapterOptionsItemHolder viewHolder = new AdapterOptionsItemHolder(context, view);
                return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        final OptionItem item = (OptionItem) getItem(position);
        if (item == null)
        {
            //holder.textTitle.setText("");
            //holder.textStatus.setText("");
            //holder.layoutRight.setVisibility(View.GONE);
        }
        else
        {
            if (item.type == OptionItem.Header)
            {
                AdapterOptionsHeaderHolder headerHolder = (AdapterOptionsHeaderHolder) holder;
                headerHolder.textTitle.setText(item.title);
            }
            else
            {

                AdapterOptionsItemHolder itemHolder = (AdapterOptionsItemHolder) holder;
                itemHolder.textTitle.setText(item.title);

                boolean active = listener.activeValue(item.id);

                itemHolder.viewMain.setEnabled(active);

                itemHolder.imageIcon.setVisibility(item.icon == 0 ? View.GONE : View.VISIBLE);
                itemHolder.imageIcon.setImageResource(item.icon == 0 ? R.mipmap.ic_launcher : item.icon);

                itemHolder.textTitle.setAlpha(active ? 1.0f : 0.5f);
                itemHolder.textStatus.setAlpha(active ? 1.0f : 0.5f);
                //itemHolder.imageArrow.setAlpha(active ? 1.0f : 0.5f);
                itemHolder.switchState.setAlpha(active ? 1.0f : 0.5f);

                switch (item.type) {
                    case OptionItem.Switch:
                        boolean checked = listener.switchValue(item.id);

                        itemHolder.switchState.setEnabled(active);
                        itemHolder.switchState.setChecked(checked);
                        itemHolder.switchState.setVisibility(View.VISIBLE);
                        itemHolder.imageArrow.setVisibility(View.GONE);
                        itemHolder.layoutRight.setVisibility(View.VISIBLE);
                        itemHolder.textStatus.setText(checked ? item.descOn : item.descOff);
                        break;

                    case OptionItem.Link:
                        itemHolder.switchState.setVisibility(View.GONE);
                        itemHolder.imageArrow.setVisibility(View.VISIBLE);
                        itemHolder.layoutRight.setVisibility(View.VISIBLE);
                        itemHolder.textStatus.setText(item.descOn);
                        itemHolder.textStatus.setVisibility(TextUtils.isEmpty(item.descOn) ? View.GONE : View.VISIBLE);
                        break;

                    case OptionItem.Text:
                        itemHolder.switchState.setVisibility(View.GONE);
                        itemHolder.imageArrow.setVisibility(View.GONE);
                        itemHolder.layoutRight.setVisibility(View.VISIBLE);
                        itemHolder.textStatus.setText(item.descOn);
                        itemHolder.textStatus.setVisibility(TextUtils.isEmpty(item.descOn) ? View.GONE : View.VISIBLE);
                        break;

                    case OptionItem.Header:
                        itemHolder.layoutRight.setVisibility(View.GONE);
                        itemHolder.textStatus.setVisibility(View.GONE);
                        itemHolder.textTitle.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                        break;
                }


                if(!hasArrow) itemHolder.imageArrow.setVisibility(View.GONE);

            }
        }
    }
    public OptionItem getItem(int position)
    {
        if (position >= 0 && position < items.size())
            return items.get(position);
        else
            return null;
    }

    @Override
    public int getItemViewType(int position)
    {
        final OptionItem item = getItem(position);
        if (item == null)
        {
            return OptionItem.Header;
        }

        return item.type;
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

    public class AdapterOptionsHeaderHolder extends RecyclerView.ViewHolder
    {
        private Context context;

        TextView        textTitle;

        public AdapterOptionsHeaderHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            textTitle   = (TextView) view.findViewById(R.id.textTitle);
            textTitle.setTypeface(Rewards.appFontBold);
        }
    }

    public class AdapterOptionsItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private Context context;

        View            viewMain;
        TextView        textTitle;
        TextView        textStatus;

        FrameLayout     layoutRight;
        SwitchCompat    switchState;
        ImageView       imageIcon;
        ImageView       imageArrow;

        public AdapterOptionsItemHolder(Context context, View view)
        {
            super(view);

            this.context = context;

            viewMain = view;

            imageIcon  = view.findViewById(R.id.imageIcon);
            textTitle   = view.findViewById(R.id.textTitle);
            textStatus  = view.findViewById(R.id.textStatus);

            layoutRight = view.findViewById(R.id.layoutRight);
            imageArrow  = view.findViewById(R.id.imageArrow);
            switchState = view.findViewById(R.id.switchState);

            textTitle.setTypeface(Rewards.appFont);
            textStatus.setTypeface(Rewards.appFont);

            switchState.setOnClickListener(this);
            viewMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            OptionItem item = getItem(getAdapterPosition());
            if(item == null) return;

            if (listener != null && listener.activeValue(item.id))
            {
                if(v == viewMain)
                {
                    listener.itemClicked(AdapterOptions.this, v, getAdapterPosition());
                }
                else if(v == switchState)
                {
                    listener.switchChanged(item.id, switchState.isChecked());

                    notifyDataSetChanged();
                }
            }
        }
    }
}