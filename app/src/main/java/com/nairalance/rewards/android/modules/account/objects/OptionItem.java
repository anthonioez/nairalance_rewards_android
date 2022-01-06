package com.nairalance.rewards.android.modules.account.objects;

public class OptionItem
{
    public static final int Link = 0;
    public static final int Header = 1;
    public static final int Switch = 2;
    public static final int Text = 3;

    public int id;
    public int type;
    public String title;
    public String descOn;
    public String descOff;
    public int icon;

    public OptionItem()
    {
        this.id = 0;
        this.type = Header;
        this.title = "";
        this.descOn = "";
        this.descOff = "";
        this.icon = 0;
    }

    public OptionItem(int id, int type, String title, String descOn, String descOff)
    {
        this.id = id;
        this.type = type;
        this.title = title;
        this.descOn = descOn;
        this.descOff = descOff;
    }

    public static OptionItem newHeader(int id, String title)
    {
        OptionItem option = new OptionItem();
        option.id = id;
        option.title = title;
        option.type = Header;
        return option;
    }

    public static OptionItem newLink(int id, String title, String status)
    {
        OptionItem option = new OptionItem();
        option.id = id;
        option.title = title;
        option.type = Link;
        option.descOn = status;
        return option;
    }

    public static OptionItem newLink(int id, String title, String status, int icon)
    {
        OptionItem option = new OptionItem();
        option.id = id;
        option.title = title;
        option.type = Link;
        option.descOn = status;
        option.icon = icon;
        return option;
    }

    public static OptionItem newSwitch(int id, String title, String descOn, String descOff)
    {
        OptionItem option = new OptionItem();
        option.id = id;
        option.title = title;
        option.type = Switch;

        option.descOn = descOn;
        option.descOff = descOff;
        return option;
    }


}
