package com.miciniti.library.controls;

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import android.view.View;

import com.miciniti.library.objects.FragmentBase;

import java.util.Vector;

public class FragmentStack
{
    private Context context;
    private FragmentManager manager;
    private int id;

    private Vector<FragmentBase> backStack = new Vector<>();
    private FragmentBase current = null;
    private View view;

    public FragmentStack(Context context, FragmentManager manager, View view, int id)
    {
        this.context = context;
        this.manager = manager;
        this.id = id;
        this.view = view.findViewById(id);
    }

    public void pop(FragmentBase self)
    {
        if (backStack.size() > 0) {
            FragmentBase frag = backStack.lastElement();
            if (frag != null) {
                manager.beginTransaction().replace(id, frag).commit();
                backStack.removeElement(frag);
            }

            if (current != null) {
                manager.beginTransaction().remove(current).commit();
            }

            current = frag;
        }
        else {
            if (self != null) {
                manager.beginTransaction().remove(self).commit();
            }
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            current = null;

        }

    }

    public void push(FragmentBase frag)
    {
        if (current != null) {
            backStack.add(current);
        }

        frag.stack = this;

        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }

        current = frag;
        try {
            manager.beginTransaction().replace(id, frag).commit();
        }
        catch (Exception e) {

        }
    }

    public boolean isEmpty()
    {
        return backStack.isEmpty() && current == null;
    }

    public FragmentBase getCurrent()
    {
        return current;
    }
}
