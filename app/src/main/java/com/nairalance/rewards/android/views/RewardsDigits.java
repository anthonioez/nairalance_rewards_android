package com.nairalance.rewards.android.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.miciniti.library.helpers.UI;
import com.nairalance.rewards.android.R;

public class RewardsDigits extends FrameLayout implements View.OnFocusChangeListener, TextWatcher
{
    private final EditText editDigit;
    private final RewardsDigit inputDigit1;
    private final RewardsDigit inputDigit2;
    private final RewardsDigit inputDigit3;
    private final RewardsDigit inputDigit4;

    private int maxLength = 4;

    public RewardsDigits(Context context)
    {
        this(context, null);
    }

    public RewardsDigits(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public RewardsDigits(Context context, AttributeSet attrs, int defStyleAttr)
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

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RewardsDigits);
        maxLength = typedArray.getInt(R.styleable.RewardsDigits_digitMax, 5);
        typedArray.recycle();

        View layout = LayoutInflater.from(context).inflate(R.layout.content_rewards_digits, null);

        LayoutParams flp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

        addView(layout, flp);

        editDigit       = (EditText)layout.findViewById(R.id.editDigit);
        inputDigit1   = (RewardsDigit)layout.findViewById(R.id.inputDigit1);
        inputDigit2   = (RewardsDigit)layout.findViewById(R.id.inputDigit2);
        inputDigit3   = (RewardsDigit)layout.findViewById(R.id.inputDigit3);
        inputDigit4   = (RewardsDigit)layout.findViewById(R.id.inputDigit4);

        editDigit.setImeActionLabel("DONE", EditorInfo.IME_ACTION_DONE);
        editDigit.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        editDigit.setOnFocusChangeListener(this);
        editDigit.addTextChangedListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (hasFocus)
        {
            displayInput(editDigit.getText().toString().trim());
            inputDigit1.setDigitCursor(true);
        }
        else
        {
            inputDigit1.setDigitCursor(false);
            inputDigit2.setDigitCursor(false);
            inputDigit3.setDigitCursor(false);
            inputDigit4.setDigitCursor(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        String text = editDigit.getText().toString().trim();

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
    }

    @Override
    public void afterTextChanged(Editable s)
    {
        String text = editDigit.getText().toString().trim();
        displayInput(text);
        inputDigit1.setDigitCursor(true);

        if(text.length() >= 4)
        {
            UI.hideKeyboard(getContext(), editDigit);
        }
    }

    public void displayInput(String text)
    {
        for(int i = 0; i < maxLength; i++)
        {
            String digit = "";
            if(text.length() > 0 && i < text.length())
            {
                digit = text.substring(i, i+1);
            }

            if(i == 0)
            {
                inputDigit1.setDigit(digit);
            }
            else if(i == 1)
            {
                inputDigit2.setDigit(digit);
            }
            else if(i == 2)
            {
                inputDigit3.setDigit(digit);
            }
            else if(i == 3)
            {
                inputDigit4.setDigit(digit);
            }

        }
    }


    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        editDigit.setEnabled(enabled);

        if(!enabled)
        {
        }
    }

    public String getCode()
    {
        return editDigit.getText().toString().trim();
    }

    public void setCode(String text)
    {
        String input = text.trim();
        editDigit.setText(input);
        displayInput(input);
    }

    public EditText getEditText()
    {
        return editDigit;
    }
}