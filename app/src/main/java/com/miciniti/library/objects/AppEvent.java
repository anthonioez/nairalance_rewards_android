package com.miciniti.library.objects;

/**
 * Created by Miciniti on 26/12/2016.
 */

public class AppEvent
{
    public long id;
    public String name;
    public Object object;

    public AppEvent()
    {

    }

    public AppEvent(String name)
    {
        this.id = 0;
        this.name = name;
    }

    public AppEvent(String name, long id, Object obj)
    {
        this.name = name;
        this.id = id;
        this.object = obj;
    }

    public AppEvent(String name, Object obj)
    {
        this.id = 0;
        this.name = name;
        this.object = obj;
    }
}
