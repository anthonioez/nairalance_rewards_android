package com.miciniti.library.adapters.SeparatedList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SeparatedListAdapter extends BaseAdapter
{
    public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
    public final HeaderListAdapter headers;
    public final static int TYPE_SECTION_HEADER = 0;

    private final int mHeadLayoutID;
    private final int mHeadLayoutTextID;

    private Context mContext;

    public SeparatedListAdapter(Context context, int headLayoutID, int headLayoutTextID)
    {
        mContext = context;
        headers = new HeaderListAdapter(context);
        mHeadLayoutID = headLayoutID;
        mHeadLayoutTextID = headLayoutTextID;
    }

    public void addSection(SectionItem section)
    {
        this.headers.add(section);
        this.sections.put(section.key, section.adapter);
    }

    public void clear()
    {
        this.headers.clear();
        this.sections.clear();

        notifyDataSetChanged();
    }

    public Object getItem(int position)
    {
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return section;
            }
            if (position < size) {
                return adapter.getItem(position - 1);
            }

            // otherwise jump into next section
            position -= size;
        }
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return 0;
            }
            if (position < size) {
                return adapter.getItemId(position - 1);
            }

            // otherwise jump into next section
            position -= size;
        }


        return position;
    }

    public Adapter getAdapter(int position)
    {
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return null;
            }
            if (position < size) {
                return adapter;
            }

            // otherwise jump into next section
            position -= size;
        }


        return null;
    }

    public int getCount()
    {
        // total together all sections, plus one for each section header
        int total = 0;
        for (Adapter adapter : this.sections.values()) {
            total += adapter.getCount() + 1;
        }
        return total;
    }

    @Override
    public int getViewTypeCount()
    {
        // assume that headers count as one, then total all sections
        int total = 1;
        for (Adapter adapter : this.sections.values()) {
            total += adapter.getViewTypeCount();
        }
        return total;
    }

    @Override
    public int getItemViewType(int position)
    {
        int type = 1;
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return TYPE_SECTION_HEADER;
            }
            if (position < size) {
                return type + adapter.getItemViewType(position - 1);
            }

            // otherwise jump into next section
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }

    public boolean areAllItemsSelectable()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int position)
    {
        return (getItemViewType(position) != TYPE_SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int sectionnum = 0;
        for (Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if (position == 0) {
                return headers.getView(sectionnum, convertView, parent);
            }
            if (position < size) {
                return adapter.getView(position - 1, convertView, parent);
            }

            // otherwise jump into next section
            position -= size;
            sectionnum++;
        }
        return null;
    }

    public class HeaderListAdapter extends BaseAdapter
    {
        private Context mContext;

        private List<SectionItem> items;

        public HeaderListAdapter(Context act)
        {
            mContext = act;
            items = new ArrayList<SectionItem>();
        }

        public void add(SectionItem item)
        {
            items.add(item);
            reload();
        }

        public void clear()
        {
            items.clear();
            notifyDataSetChanged();
        }

        public void reload()
        {
            notifyDataSetChanged();
        }

        public int getCount()
        {
            return items.size();
        }

        public Object getItem(int position)
        {
            return items.get(position);
        }

        public long getItemId(int position)
        {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent)
        {
            View rowView = convertView;

            if (rowView == null)
            {
                rowView = LayoutInflater.from(mContext).inflate(mHeadLayoutID, parent, false);
            }

            SectionItem item = (SectionItem) getItem(position);
            if (item != null && rowView != null)
            {
                TextView titleText = (TextView) rowView.findViewById(mHeadLayoutTextID);
                if(titleText != null) titleText.setText(item.title);
            }

            return rowView;
        }

    }
}
