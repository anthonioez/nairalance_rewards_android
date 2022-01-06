package com.nairalance.rewards.android.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miciniti.library.helpers.UI;
import com.nairalance.rewards.android.R;
import com.nairalance.rewards.android.Rewards;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class RewardsInput extends FrameLayout implements View.OnFocusChangeListener, View.OnClickListener, TextWatcher, TextView.OnEditorActionListener, DatePickerDialog.OnDateSetListener
{
    private String prefix;
    private Drawable icon;

    private String placeHolder;
    private String returnKey;
    private String keyboardType;

    private View layoutMain;
    private TextView textPrefix;
    private EditText editText;
    private ProgressBar progressSuffix;
    private ImageView imageSuffix;

    private AlertDialog pickerDialog = null;
    private DatePickerDialog dateDialog = null;

    private List<Object> pickerList = null;
    private int selectedIndex = -1;

    private OnInputChangedListener inputListener = null;
    private OnPickerChangedListener pickerListener = null;

    private Calendar calendar;

    public RewardsInput(Context context)
    {
        this(context, null);
    }

    public RewardsInput(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public RewardsInput(Context context, AttributeSet attrs, int defStyleAttr)
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

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RewardsInput);

        prefix = typedArray.getString(R.styleable.RewardsInput_inputPrefix);
        placeHolder = typedArray.getString(R.styleable.RewardsInput_inputPlaceholder);
        returnKey = typedArray.getString(R.styleable.RewardsInput_inputReturnKey);
        keyboardType = typedArray.getString(R.styleable.RewardsInput_inputKeyboardType);
        icon = typedArray.getDrawable(R.styleable.RewardsInput_inputSuffixIcon);
        typedArray.recycle();

        calendar = Calendar.getInstance(TimeZone.getDefault());

        layoutMain = LayoutInflater.from(context).inflate(R.layout.content_rewards_input, null);

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

        addView(layoutMain, flp);

        textPrefix      = layoutMain.findViewById(R.id.textPrefix);
        editText        = layoutMain.findViewById(R.id.editText);
        imageSuffix     = layoutMain.findViewById(R.id.imageSuffix);
        progressSuffix  = layoutMain.findViewById(R.id.progressSuffix);

        textPrefix.setText(TextUtils.isEmpty(prefix) ? "" : prefix);
        textPrefix.setVisibility(TextUtils.isEmpty(prefix) ? GONE : VISIBLE);
        textPrefix.setTypeface(Rewards.appFontBold);
        textPrefix.setTextColor(getResources().getColor(R.color.disabled));

        if(!TextUtils.isEmpty(prefix))
        {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) editText.getLayoutParams();
            params.leftMargin = params.leftMargin / 2;
            editText.setLayoutParams(params);
        }

        if(icon != null)
        {
            imageSuffix.setImageDrawable(icon);
            imageSuffix.setVisibility(VISIBLE);
        }

        editText.setHint(placeHolder);
        editText.setText("");
        editText.setOnFocusChangeListener(this);
        editText.addTextChangedListener(this);
        editText.setOnEditorActionListener(this);
        editText.setTypeface(Rewards.appFont);

        if(returnKey == null)
        {
        }
        else if(returnKey.equals("next"))
        {
            editText.setImeActionLabel("NEXT", EditorInfo.IME_ACTION_NEXT);
            editText.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        }
        else if(returnKey.equals("done"))
        {
            editText.setImeActionLabel("DONE", EditorInfo.IME_ACTION_DONE);
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        setKeyboardType(keyboardType);

        if(!keyboardType.equals("password"))
        {
            /*
            InputFilter filter = new InputFilter()
            {
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    for (int i = start; i < end; i++)
                    {
                        char c = source.charAt(i);
                        if(c != '@' && c != '.' && c != '-' && c != '_' && !Character.isLetterOrDigit(c))
                        {
                            return "";
                        }
                    }
                    return null;
                }
            };
            editText.setFilters(new InputFilter[] { filter });
            */
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        if (hasFocus)
        {
        }
        else
        {
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        if (actionId == EditorInfo.IME_ACTION_DONE)
        {
            UI.hideKeyboard(getContext(), editText);
            return true;
        }
        return false;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day)
    {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        editText.setText(DateFormat.format("yyyy-MM-dd", calendar.getTimeInMillis()));

        dateDialog = null;
    }

    @Override
    public void onClick(View v)
    {
        if(keyboardType.equals("date"))
        {
            showDate();
        }
        else if(keyboardType.equals("dropdown"))
        {
            showPicker();
        }
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        editText.setEnabled(enabled);

        layoutMain.setAlpha(enabled ? 1.0f : 0.5f);
    }

    public void setPrefix(String text)
    {
        textPrefix.setText(text);
    }

    public void setPlaceholder(String text)
    {
        placeHolder = text;
        editText.setHint(text);
    }

    public void setKeyboardType(String type)
    {
        keyboardType = type;

        imageSuffix.setOnClickListener(null);

        if(keyboardType == null)
        {

        }
        else if(keyboardType.equals("name"))
        {
            editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS | EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
        }
        else if(keyboardType.equals("email"))
        {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        }
        else if(keyboardType.equals("number"))
        {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        else if(keyboardType.equals("phone"))
        {
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        else if(keyboardType.equals("password"))
        {
            //editText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        else if(keyboardType.equals("dropdown"))
        {
            imageSuffix.setImageResource(R.drawable.ic_arrow_drop_down_black);
            imageSuffix.setAlpha(0.5f);
            imageSuffix.setVisibility(VISIBLE);
            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME);
            imageSuffix.setOnClickListener(this);
        }
        else if(keyboardType.equals("date"))
        {
        }
    }

    public void setIcon(int icon)
    {
        imageSuffix.setImageResource(icon);
    }

    public String getInput()
    {
        return editText.getText().toString();
    }

    public void setInput(String text)
    {
        editText.setText(text);

        if(pickerList != null)
        {
            if(pickerListener != null)
            {
                int index = pickerList.indexOf(text);
                pickerListener.onPickerChanged(this, text, index);
            }
        }
    }

    public void setIndex(int index)
    {
        if(pickerList == null) return;

        if(index < 0 || index > pickerList.size()) return;

        selectedIndex = index;

        String text = pickerList.get(index).toString();
        editText.setText(text);

        if(pickerListener != null)
        {
            pickerListener.onPickerChanged(this, text, index);
        }
    }

    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    public Object getSelectedItem()
    {
        if(pickerList != null && selectedIndex >= 0 && selectedIndex < pickerList.size())
        {
            return pickerList.get(selectedIndex);
        }

        return null;
    }

    public List<Object> getPickerList()
    {
        return pickerList;
    }

    public void setList(CharSequence[] list)
    {
        List<Object> items = null;
        if(list != null)
        {
            items = new ArrayList<>();
            for (CharSequence item : list)
            {
                items.add((String)item);
            }
        }

        setPickerList(items);
    }


    public void setPickerList(List<Object> list)
    {
        pickerList = list;
        if(list == null)
        {
            //editText.setEnabled(true);
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setOnClickListener(null);
        }
        else
        {
            editText.setFocusable(false);
            editText.setFocusableInTouchMode(false);
            editText.setOnClickListener(this);
        }
    }

    public Calendar getDate()
    {
        return calendar;
    }

    public void setDate(long stamp)
    {
        calendar.setTimeInMillis(stamp);

        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setOnClickListener(this);
    }

    public void reload()
    {
    }

    public void showDate()
    {
        if(dateDialog != null) return;
        UI.hideKeyboard(getContext(), this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            dateDialog = new DatePickerDialog(getContext(),
                    android.R.style.Theme_Material_Light_Dialog_Alert,
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
        }
        else
        {
            dateDialog = new DatePickerDialog(getContext(),
                    this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
        }

        dateDialog.show();
    }

    private void showPicker()
    {
        if(pickerDialog != null) return;
        if(pickerList == null) return;

        UI.hideKeyboard(getContext(), this);

        CharSequence[] items = new CharSequence[pickerList.size()];
        for(int i = 0; i < pickerList.size(); i++)
        {
            items[i] = pickerList.get(i).toString();
        }

        android.app.AlertDialog.Builder dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            dialog = new android.app.AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        else
            dialog = new android.app.AlertDialog.Builder(getContext());

        dialog.setTitle(placeHolder);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                pickerDialog = null;
            }
        });
        dialog.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int pos)
            {
                selectedIndex = pos;

                String input = getInput();
                String text = pickerList.get(pos).toString();

                editText.setText(text);
                if(pickerListener != null && !input.equals(text))
                {
                    pickerListener.onPickerChanged(RewardsInput.this, text, pos);
                }

                pickerDialog = null;
            }
        });
        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                pickerDialog = null;
            }
        });
        pickerDialog = dialog.show();
    }

    public void setOnPickerChanged(OnPickerChangedListener listener)
    {
        pickerListener = listener;
    }

    public void setOnInputChanged(OnInputChangedListener listener)
    {
        inputListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {

    }

    @Override
    public void afterTextChanged(Editable s)
    {
        if(inputListener != null)
        {
            inputListener.onInputChanged(this, s.toString());
        }
    }

    public EditText getEditText()
    {
        return editText;
    }

    public TextView getPrefixText()
    {
        return textPrefix;
    }

    public ProgressBar getSuffixProgress()
    {
        return progressSuffix;
    }

    public ImageView getSuffixImage()
    {
        return imageSuffix;
    }

    public String getPlaceholder()
    {
        return placeHolder;
    }

    public interface OnPickerChangedListener
    {
        void onPickerChanged(RewardsInput input, String text, int pos);
    }

    public interface OnInputChangedListener
    {
        void onInputChanged(RewardsInput input, String text);
    }
}