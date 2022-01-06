package com.nairalance.rewards.android.modules.start.viewers;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.FragmentBase;
import com.miciniti.library.objects.ViewerFragment;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;
import com.nairalance.rewards.android.Strings;
import com.nairalance.rewards.android.modules.start.controllers.ControllerStart;

/**
 * Created by Miciniti on 10/05/2018.
 */

public class ViewerSlide extends ViewerFragment implements View.OnClickListener
{
    public static final String TAG = ViewerSlide.class.getSimpleName();

    private ImageView imageIcon;
    private TextView textTitle;
    private TextView textDesc;
    private Button buttonAction;

    private Handler handler = new Handler();

    public ViewerSlide(FragmentBase frag)
    {
        super(frag);
    }

    public View init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.viewer_slide, container, false);

        UI.overrideFonts(getContext(), rootView, Rewards.appFont);

        imageIcon       = rootView.findViewById(R.id.imageIcon);

        textTitle       = rootView.findViewById(R.id.textTitle);
        textDesc        = rootView.findViewById(R.id.textDesc);
        buttonAction    = rootView.findViewById(R.id.buttonAction);
        buttonAction.setVisibility(View.GONE);

        Bundle bundle = fragment.getArguments();
        if (bundle != null)
        {
            textTitle.setText(bundle.getInt(Strings.title));
            textDesc.setText(bundle.getInt(Strings.desc));
            imageIcon.setImageResource(bundle.getInt(Strings.icon));

            int action = bundle.getInt(Strings.action);
            if(action > 0)
            {
                buttonAction.setText(action);
                buttonAction.setOnClickListener(this);
                buttonAction.setVisibility(View.VISIBLE);
            }
        }

        return rootView;
    }

    @Override
    public void onClick(View v)
    {
        ((ControllerStart)getController()).slideAction(0);
    }
}