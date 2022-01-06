package com.miciniti.library.io;

import com.miciniti.library.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ServerRequest
{
    public String endpoint = "";
    public String version = "";
    public String path = "";
    public String method = "GET";

    //public HashMap<String, FileItem> files;
    public HashMap<String, String> params;
    public HashMap<String, String> headers;

    public ServerRequestListener listener;

    public ServerRequest()
    {
        init();
    }

    public ServerRequest(String endpoint, String version)
    {
        this.endpoint = endpoint;
        this.version = version;

        init();
    }

    public ServerRequest(String endpoint, String version, String path)
    {
        this.endpoint = endpoint;
        this.version = version;
        this.path = path;

        init();
    }

    public void init()
    {
        method = "GET";
        params = new HashMap<>();
        headers = new HashMap<>();
        //files = new HashMap<>();
    }

    public String url()
    {
        return endpoint + version + path;
    }

    public String route()
    {
        return version + path;
    }

    public interface ServerRequestListener
    {
        void requestSize(long length);
        void requestWrite(long written);
    }

    public String sign(String hash)
    {
        String sign = "";

        long stamp = (int)(System.currentTimeMillis()  / (3600*1000)) * 3600;

        sign = Utils.getSHA1(sign + method);
        sign = Utils.getSHA1(sign + route());

        //sign = Utils.getSHA1(sign + String.valueOf(stamp));

        //sort parameters
        List<String> sortedKeys = new ArrayList(params.keySet());
        Collections.sort(sortedKeys);

        //hash loop thru
        for (String key : sortedKeys)
        {
            if(key.equals("apisign")) continue;    //

            String data = params.get(key);

            sign = Utils.getSHA1(sign + data);
        }

        if(hash.length() > 0)
        {
            sign = Utils.getSHA1(hash + sign);
        }

        return sign;
    }
}
