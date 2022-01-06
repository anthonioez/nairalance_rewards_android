package com.nairalance.rewards.android.modules.account.objects;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

public interface OptionListener
{
    boolean switchValue(int id);
    boolean activeValue(int id);

    void itemClicked(RecyclerView.Adapter adp, View view, int pos);
    void switchChanged(int id, boolean state);
}
