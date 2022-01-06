package com.miciniti.library.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.miciniti.library.io.Server;

import com.nairalance.rewards.android.R;

/**
 * Created by Miciniti on 28/12/2016.
 */

public class UI
{
    public static ProgressDialog progressDialogOpen(Context context, String title, String message, DialogInterface.OnCancelListener listener)
    {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        progressDialog.setOnCancelListener(listener);
        progressDialog.show();

        return progressDialog;
    }

    public static  void progressDialogClose(ProgressDialog progressDialog)
    {
        if(progressDialog != null && progressDialog.isShowing())
        {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    public static void alertDialogClose(androidx.appcompat.app.AlertDialog alertDialog)
    {
        if(alertDialog != null && alertDialog.isShowing())
        {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public static void clipboard(Context context, String text, String toast)
    {
        String msg = toast;
        if(!copyToClipboard(context, text))
        {
            msg = context.getString(R.string.unable_to_copy_to_clipboard);
        }

        if(!TextUtils.isEmpty(toast))
        {
            UI.toast(context, msg);
        }
    }

    public static boolean copyToClipboard(Context context, String text)
    {
        boolean ret = false;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", text);
                clipboard.setPrimaryClip(clip);
                ret = true;
            }
            else
            {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
                ret = true;
            }
        }
        catch (Exception e)
        {
        }

        return ret;
    }

    public static void alert(Context context, String title, String msg)
    {
        if( ((Activity) context).isFinishing() ) return;

        if(TextUtils.isEmpty(msg)) return;

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dlg, int id)
            {
                dlg.cancel();
            }
        });
        dialog.show();
    }

    public static void alertHtml(Context context, String title, String msg, final Runnable runnable)
    {
        if( ((Activity) context).isFinishing() ) return;

        if(TextUtils.isEmpty(msg)) return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(Html.fromHtml(msg));
        dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnable != null) {
                    runnable.run();
                }

            }
        });
        dialog.show();
    }

    public static void alert(Context context, String title, String msg, final Runnable runnable)
    {
        if( ((Activity) context).isFinishing() ) return;

        if(TextUtils.isEmpty(msg)) return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnable != null) {
                    runnable.run();
                }

            }
        });

        try {
            dialog.show();
        }
        catch (Exception e)
        {

        }
    }

    public static void confirm(Context context, String title, String msg, final Runnable runnableYes, final Runnable runnableNo)
    {
        if( ((Activity) context).isFinishing() ) return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnableYes != null) {
                    runnableYes.run();
                }
            }
        });
        dialog.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnableNo != null) {
                    runnableNo.run();
                }
            }
        });
        dialog.show();
    }

    public static void ask(Context context, String title, String msg, final Runnable runnableYes, final Runnable runnableNo)
    {
        if( ((Activity) context).isFinishing() ) return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnableYes != null) {
                    runnableYes.run();
                }
            }
        });
        dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnableNo != null) {
                    runnableNo.run();
                }
            }
        });
        dialog.show();
    }

    public static void ask(Context context, String title, String msg, String yes, String no, final Runnable runnableYes, final Runnable runnableNo)
    {
        if( ((Activity) context).isFinishing() ) return;

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnableYes != null) {
                    runnableYes.run();
                }
            }
        });
        dialog.setNegativeButton(no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
                if (runnableNo != null) {
                    runnableNo.run();
                }
            }
        });
        dialog.show();

        /*
        final CharSequence[] items = {yes, no};

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case 0:
                        if (runnableYes != null) {
                            runnableYes.run();
                        }
                        break;

                    case 1:
                        if (runnableNo != null) {
                            runnableNo.run();
                        }
                        break;
                }
            }
        });
        builder.show();
        */
    }

    public static void snack(View layout, CharSequence text)
    {

        if (layout == null || TextUtils.isEmpty(text))
            return;

        try{ Snackbar.make(layout, text, Snackbar.LENGTH_LONG).show(); } catch (Exception e) {}
    }


    public static void toast(Context context, int text)
    {
        toast(context, context.getString(text));
    }

    public static void toast(Context context, CharSequence text)
    {

        if (context == null || TextUtils.isEmpty(text))
            return;

        try{ Toast.makeText(context, text, Toast.LENGTH_LONG).show(); } catch (Exception e) {}
    }

    public static void toastShort(Context context, String text)
    {
        if (context == null || text == null || text.length() == 0)
            return;

        try{ Toast.makeText(context, text, Toast.LENGTH_SHORT).show();} catch (Exception e) {}
    }

    public static boolean toastConnection(Context context)
    {
        if(!Server.isOnline(context))
        {
            toast(context, context.getString(R.string.err_no_connection));
            return false;
        }

        return true;
    }


    public static void hideKeyboard(Context context, View vw)
    {
        if (context == null)
            return;

        if (vw == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(vw.getWindowToken(), 0);
    }

    public static void overrideFonts(final Context context, final View v, final Typeface font)
    {
        try
        {
            if (v instanceof ViewGroup)
            {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++)
                {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child, font);
                }
            }
            else if (v instanceof TextView)
            {
                ((TextView) v).setTypeface(font);
            }
            else if (v instanceof EditText)
            {
                ((EditText) v).setTypeface(font);
            }
            else if (v instanceof Button)
            {
                ((Button) v).setTypeface(font);
            }
            else if (v instanceof TextInputLayout)
            {
                ((TextInputLayout) v).setTypeface(font);
            }
        }
        catch (Exception e)
        {
        }
    }


    public static void expand(final View v, int duration)
    {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1 ? ViewGroup.LayoutParams.WRAP_CONTENT : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(duration != 0 ? duration : ((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density)));
        v.startAnimation(a);
    }

    public static void fadeIn(final View v, int duration)
    {
        v.setVisibility(View.VISIBLE);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(duration);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        v.setAnimation(animation);
    }

    public static void fadeOut(final View v, int duration)
    {
        v.setVisibility(View.VISIBLE);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(duration);
        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                v.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeOut);
        v.setAnimation(animation);

    }

    public static void collapse(final View v, int duration)
    {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1)
                {
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(duration != 0 ? duration : ((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density)));
        v.startAnimation(a);
    }

    public static Rect getScreenSize(Context context)
    {
        Rect size = new Rect();
        size.left = 0;
        size.top = 0;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        Display d = wm.getDefaultDisplay();
        size.right = metrics.widthPixels;
        size.bottom = metrics.heightPixels;
        try
        {
            // used when 17 > SDK_INT >= 14; includes window decorations (statusbar bar/menu bar)
            size.right = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
            size.bottom = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
        }
        catch (Exception ignored)
        {
            try
            {
                // used when SDK_INT >= 17; includes window decorations
                // (statusbar bar/menu bar)
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                size.right = realSize.x;
                size.bottom = realSize.y;
            }
            catch (Exception ignored2)
            {
            }
        }

        int width = Math.min(size.right, size.bottom);
        int height = Math.max(size.right, size.bottom);

        size.right = width;
        size.bottom = height;
        return size;
    }

    public static Point getDeviceSize(Activity activity)
    {
        WindowManager wm = activity.getWindowManager();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            wm.getDefaultDisplay().getSize(point);
            return point;
        }
        else
        {
            return new Point(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
        }
    }

    public static void removeFromParent(View layout)
    {
        if(layout == null) return;
        ViewParent pp = layout.getParent();
        if(pp == null) return;
        ((ViewGroup)pp).removeView(layout);
    }

    public static InputFilter[] usernameFilter()
    {
        return new InputFilter[] {
                new InputFilter()
                {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[a-zA-Z0-9_]+")){    //TODO
                            return src;
                        }
                        return "";
                    }
                }
        };
    }

    public static InputFilter[] alphanumericFilter()
    {
        return new InputFilter[] {
                new InputFilter()
                {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[a-zA-Z 0-9]+")){
                            return src;
                        }
                        return "";
                    }
                }
        };
    }

    public static InputFilter[] phoneFilter()
    {
        return new InputFilter[] {
                new InputFilter()
                {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if(src.equals("")){ // for backspace
                            return src;
                        }
                        if(src.toString().matches("[0-9]+")){
                            return src;
                        }
                        return "";
                    }
                }
        };
    }

}
