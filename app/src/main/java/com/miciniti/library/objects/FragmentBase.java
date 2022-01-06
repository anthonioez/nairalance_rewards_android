package com.miciniti.library.objects;

import android.content.Context;
import androidx.fragment.app.Fragment;

import com.miciniti.library.controls.FragmentStack;

public class FragmentBase extends Fragment
{
    public FragmentStack stack = null;

    public boolean onBackPressed()
    {
        if(stack != null && !stack.isEmpty())
        {
            stack.pop(stack.getCurrent());
            return true;
        }
        return false;
    }

    public String getTitle(Context context)
    {
        return null;
    }
}
