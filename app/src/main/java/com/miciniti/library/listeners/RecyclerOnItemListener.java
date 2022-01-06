package com.miciniti.library.listeners;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by Miciniti onEvent 03/05/16.
 */
/**
 * A click errorListener for items.
 */
public interface RecyclerOnItemListener
{
    /**
     * Called when an item is clicked.
     *
     * @param adp Adapter was clicked.
     * @param view View of the item that was clicked.
     * @param position  Position of the item that was clicked.
     */
    public void onItemClick(RecyclerView.Adapter adp, View view, int position);

    /**
     * Called when an item is long clicked.
     *
     * @param adp Adapter was clicked.
     * @param view View of the item that was clicked.
     * @param position  Position of the item that was clicked.
     */
    public boolean onItemLongClick(RecyclerView.Adapter adp, View view, int position);


    /**
     * Called when an item's menu is clicked.
     *
     * @param adp Adapter was clicked.
     * @param view View of the item that was clicked.
     * @param position  Position of the item that was clicked.
     */
    public void onMenuClick(RecyclerView.Adapter adp, View view, int position);
}

