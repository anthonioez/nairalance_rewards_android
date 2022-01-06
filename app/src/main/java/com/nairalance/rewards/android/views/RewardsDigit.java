package com.nairalance.rewards.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;

public class RewardsDigit extends FrameLayout implements View.OnFocusChangeListener
{
    private final TextView textDigit;
    private final ImageView imageDigit;
    //private EditText editText;
    private View viewActive;

    public RewardsDigit(Context context)
    {
        this(context, null);
    }

    public RewardsDigit(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public RewardsDigit(Context context, AttributeSet attrs, int defStyleAttr)
    {
        // Can't call through to super(Context, AttributeSet, int) since it doesn't exist on API 10
        super(context, attrs);

        if (isInEditMode())
        {
//            return;
        }

        if (null == attrs)
        {
            throw new IllegalArgumentException("Attributes should be provided to this view,");
        }

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RewardsDigit);
        String number = typedArray.getString(R.styleable.RewardsDigit_digitNumber);
        typedArray.recycle();

        View layout = LayoutInflater.from(context).inflate(R.layout.content_rewards_digit, null);

        LayoutParams flp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

        addView(layout, flp);

        imageDigit  = (ImageView)layout.findViewById(R.id.imageDigit);
        textDigit   = (TextView)layout.findViewById(R.id.textDigit);
        viewActive  = layout.findViewById(R.id.viewActive);

        textDigit.setText(number != null ? number : "");
        textDigit.setTypeface(Rewards.appFont);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (hasFocus)
        {
            viewActive.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
        else
        {
            viewActive.setBackgroundColor(getResources().getColor(R.color.transparent));
        }
    }

    public void setDigit(String digit)
    {
        textDigit.setText(digit);
        imageDigit.setVisibility(textDigit.length() > 0 ? GONE : VISIBLE);
        viewActive.setBackgroundColor(getResources().getColor(textDigit.length() > 0 ? R.color.colorAccent : R.color.transparent));
    }

    public void setDigitCursor(boolean show)
    {
        viewActive.setBackgroundColor(getResources().getColor(show ? R.color.colorAccent : R.color.transparent));
    }
}