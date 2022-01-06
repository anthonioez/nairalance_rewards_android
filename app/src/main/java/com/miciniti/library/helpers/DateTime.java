package com.miciniti.library.helpers;

import android.text.format.DateFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TimeZone;

public class DateTime
{
    private static final String TAG = DateTime.class.getSimpleName();

    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;
    public final static long ONE_MONTH = ONE_DAY * 30;

    public final static long SECONDS = 60;
    public final static long HOURS = 24;
    public final static long MINUTES = 60;

    private DateTime()
    {
    }

    public static long getTimestamp(String stamp)
    {
        long time = 0;
        try
        {
            if(stamp == null || stamp.length() == 0) return 0;

            stamp = stamp.replace("T", " ").replace("Z", "");

            time = Timestamp.valueOf(stamp).getTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return time;
    }

    public static String getRelativeTime(long mill, long now, String suffix)
    {
        long diff = (now - mill)/1000;
        if(diff < 0)
            return "";
        else
            return timeDifference(diff, suffix);
    }

    public static String timeDifference(long seconds, String suffix)
    {
        long value = 0;
        String prefix = "";

        if(seconds < 60)
        {
            value = seconds;
            prefix = "second";
        }
        else if(seconds < 60*60)
        {
            value = (seconds / 60);
            prefix = "minute";
        }
        else if(seconds < 24*60*60)
        {
            value = Math.round(seconds / (60*60));
            prefix = "hour";
        }
        else if(seconds < 30*24*60*60)
        {
            value = Math.round(seconds / (24*60*60));
            prefix = "day";
        }
        else if(seconds < (365*24*60*60))
        {
            value = Math.round(seconds / (30*24*60*60));
            prefix = "month";
        }
        else if(seconds > (365*24*60*60))
        {
            value = Math.round(seconds / (365*24*60*60));
            prefix = "year";
        }

        String s = value + " " + prefix + (value > 1 ? "s" : "") +  " "  + suffix;
        return s.trim();
    }

    public static String formatDuration(long input)
    {
        long totalSecs = input / 1000;
        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        if(hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * converts time (in milliseconds) to human-readable format
     * "<w> days, <x> hours, <y> minutes and (z) seconds"
     */
    public static String millisToLongDHMS(long duration)
    {
        long day = 0;
        long hour = 0;
        long minute = 0;
        long second = 0;

        StringBuffer res = new StringBuffer();

        if (duration >= ONE_SECOND)
        {
            day = duration / ONE_DAY;
            if (day > 0) {
                duration -= day * ONE_DAY;
                res.append(day).append(" day").append(day > 1 ? "s " : " ");  //.append(duration >= ONE_MINUTE ? " " : "");
            }

            hour = duration / ONE_HOUR;
            if (hour > 0) {
                duration -= hour * ONE_HOUR;
                res.append(hour).append(" hr").append(hour > 1 ? "s " : " ");   //.append(duration >= ONE_MINUTE ? " " : "");
            }

            minute = duration / ONE_MINUTE;
            if (minute > 0 && day == 0) {
                duration -= minute * ONE_MINUTE;
                res.append(minute).append(" min").append(minute > 1 ? "s " : " ");
            }

            second = duration / ONE_SECOND;
            if (second > 0 && day == 0 && hour == 0 && minute == 0) {
                res.append(second).append(" sec").append(second > 1 ? "s " : " ");
            }

            if(!res.toString().equals(""))
            {
                res.append("ago");
            }

            return res.toString();
        } else {
            return "0 seconds ago";
        }
    }

    public static String getDaysFormat(int days)
    {
        return String.format("%d day%s", days, days != 1 ? "s" : "");
    }

    public static String getDateTimeFormat(long date)
    {
        return (String) DateFormat.format("EEE, MMM d, yyyy, hh:mm aa", new Date(date));
    }

    public static String getDateFormat(long date)
    {
        return (String) DateFormat.format("EEEE, MMMM d, yyyy", new Date(date));
    }

    public static String getDateFormatShort(long date)
    {
        return (String) DateFormat.format("d MMM yyyy", new Date(date));
    }

    public static String getTimeFormat(long stamp)
    {
        return (String) DateFormat.format("hh:mm aa", new Date(stamp));
    }

    public static String getDayFormat(long stamp)
    {
        return (String) DateFormat.format("EEE", new Date(stamp));
    }

    public static String getDoWFormat(long stamp)
    {
        return (String) DateFormat.format("dd", new Date(stamp));
    }

    public static String getMonthFormat(long stamp)
    {
        return (String) DateFormat.format("MMMMM", new Date(stamp));
    }

    public static String getYearFormat(long stamp)
    {
        return (String) DateFormat.format("yyyy", new Date(stamp));
    }


    public static long getLocalTimestamp(long stamp)
    {
        long timestamp  = TimeZone.getDefault().getOffset(stamp);
        timestamp += stamp;

        return timestamp;
    }

    public static long getUTCTimestamp(long stamp)
    {
        long timestamp  = stamp - TimeZone.getDefault().getOffset(stamp);

        return timestamp;
    }



}