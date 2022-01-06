package com.miciniti.library.io;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;

import com.miciniti.library.Utils;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.objects.AppEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import com.nairalance.rewards.android.R;

public class ServerSocket
{
    private static final String TAG = ServerSocket.class.getSimpleName();

    public static final String EVENT            = ServerData.event;
    public static final String EVENT_REGISTER   = "devices_register";
    public static final String EVENT_CONNECT    = "devices_connect";

    private static Socket socket;
    private static ServerSocket serverSocket = null;

    private static List<SocketPayload> payloadList = new ArrayList<>();

    private static boolean busy = false;
    private static boolean registered = false;
    public static String socketUrl = "";
    public static SocketInterface socketListener;

    public ServerSocket()
    {
        busy = false;
        registered = false;

        try
        {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.reconnection = false;
            //opts.reconnectionAttempts = 3;
            //opts.reconnectionDelay = 5000;
            opts.timeout = 15000;
            opts.transports = new String[] {ServerData.websocket};

            socket = IO.socket(socketUrl, opts);
            socket.on(Socket.EVENT_CONNECT, listenerConnect);
            socket.on(Socket.EVENT_DISCONNECT, listenerDisconnect);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            serverSocket = null;
        }
    }

    private Emitter.Listener listenerConnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            //Logger.d(TAG, "listenerConnect: ");

            registered = false;

            EventBus.getDefault().post(new AppEvent(ServerData.socket_connected, args));
        }
    };

    private Emitter.Listener listenerDisconnect = new Emitter.Listener()
    {
        @Override
        public void call(Object... args)
        {
            //Logger.d(TAG, "listenerDisconnect: ");

            socket.off(Socket.EVENT_CONNECT, listenerConnect);
            socket.off(Socket.EVENT_DISCONNECT, listenerDisconnect);

            registered = false;
            serverSocket = null;

            EventBus.getDefault().post(new AppEvent(ServerData.socket_disconnected, args));
        }
    };

    public static void setSocketListener(SocketInterface sl)
    {
        socketListener = sl;
    }

    public static void setSocketUrl(String url)
    {
        socketUrl = url;
    }

    public synchronized static ServerSocket getInstance()
    {
        if (serverSocket == null)
        {
            serverSocket = new ServerSocket();
        }
        return serverSocket;
    }

    public Socket getSocket()
    {
        return socket;
    }

    public static boolean isConnected()
    {
        return getInstance().getSocket().connected();
    }

    public static void setBusy(boolean r)
    {
        busy = r;
    }

    public static boolean isBusy()
    {
        return busy;
    }

    public static void setRegistered(boolean r)
    {
        registered = r;
    }

    public static boolean isRegistered()
    {
        return registered;
    }

    public static void onEvent(String event, Emitter.Listener callBack)
    {
        if(callBack == null) return;

        ServerSocket.getInstance().getSocket().on(event, callBack);
    }

    public static void offEvent(String event, Emitter.Listener callBack)
    {
        if(callBack == null) return;

        ServerSocket.getInstance().getSocket().off(event, callBack);
    }

    public static void onError(Emitter.Listener callBack)
    {
        ServerSocket.getInstance().getSocket()
                .on(Socket.EVENT_ERROR, callBack)
                .on(Socket.EVENT_CONNECT_ERROR, callBack)
                .on(Socket.EVENT_CONNECT_TIMEOUT, callBack)
                .on(Socket.EVENT_RECONNECT_FAILED, callBack)
                .on(Socket.EVENT_DISCONNECT, callBack);
    }

    public static void offError(Emitter.Listener callBack)
    {
        if(callBack == null) return;

        ServerSocket.getInstance().getSocket()
                .off(Socket.EVENT_ERROR, callBack)
                .off(Socket.EVENT_CONNECT_ERROR, callBack)
                .off(Socket.EVENT_CONNECT_TIMEOUT, callBack)
                .off(Socket.EVENT_RECONNECT_FAILED, callBack)
                .off(Socket.EVENT_DISCONNECT, callBack);
    }


    public static void disconnect()
    {
        busy = false;
        if(serverSocket == null || socket == null) return;

        socket.disconnect();
    }

    public static void emit(String event, Object param)
    {
        ServerSocket.getInstance().getSocket().emit(event, param);
    }

    public static boolean output(Context context, String event, JSONObject json, Emitter.Listener payloadSentListener, Emitter.Listener payloadErrorListener)
    {
        try
        {
            if(busy)
            {
                SocketPayload pld = new SocketPayload(event, json, payloadSentListener, payloadErrorListener);

                queueAdd(pld);

                //if(payloadErrorListener != null) payloadErrorListener.call(context.getString(R.string.connection_busy));
                return false;
            }

            //Logger.d(TAG, "output: " + event + " " + json.toString());

            SocketPayload payload = new SocketPayload(event, json, payloadSentListener, payloadErrorListener);

            if(ServerSocket.getInstance().getSocket().connected())
            {
                if(isRegistered())
                {
                    queueDrain(context);

                    if(!TextUtils.isEmpty(event) && json != null)
                    {
                        json.put(ServerData.event, event);
                        ServerSocket.getInstance().getSocket().emit(EVENT, json);
                    }

                    if(payloadSentListener != null) payloadSentListener.call();
                }
                else
                {
                    authWithPayload(context, payload);
                }
            }
            else
            {
                if(!Server.isOnline(context))
                {
                    if(payloadErrorListener != null) payloadErrorListener.call(context.getString(R.string.err_no_connection));

                    return false;
                }
                setBusy(true);

                PayloadConnectDoneListener connectDone      = new PayloadConnectDoneListener(context, payload);
                PayloadConnectErrorListener connectError    = new PayloadConnectErrorListener(context, payload, connectDone);

                connectDone.setConnectError(connectError);

                ServerSocket.getInstance().onEvent(Socket.EVENT_CONNECT, connectDone);
                ServerSocket.getInstance().onError(connectError);

                ServerSocket.getInstance().getSocket().connect();
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            if(payloadErrorListener != null) payloadErrorListener.call(context.getString(R.string.err_occurred));
        }
        return false;
    }

    private static void authWithPayload(Context context, SocketPayload payload)
    {
        ServerSocket.setBusy(true);

        //register or connect
        PayloadAuthDoneListener authDone = new PayloadAuthDoneListener(context, payload);
        PayloadAuthErrorListener authError = new PayloadAuthErrorListener(context, payload, authDone);

        authDone.setAuthError(authError);

        try
        {
            JSONObject json = new JSONObject();

            if (socketListener.shouldHome(context))
            {
                Rect screenSize = UI.getScreenSize(context);


                json.put(ServerData.tag,        Utils.getID(context));

                json.put(ServerData.make,       Build.MANUFACTURER.toUpperCase(Locale.US));
                json.put(ServerData.name,       Build.MODEL);
                json.put(ServerData.model,      Build.PRODUCT);

                json.put(ServerData.os,         ServerData.android);
                json.put(ServerData.osver,      String.valueOf(Build.VERSION.RELEASE));

                json.put(ServerData.width,      String.valueOf(screenSize.width()));
                json.put(ServerData.height,     String.valueOf(screenSize.height()));

                json.put(ServerData.lon,        String.valueOf(socketListener.getLon(context)));
                json.put(ServerData.lat,        String.valueOf(socketListener.getLat(context)));

                json.put(ServerData.network,    Utils.getNetwork(context));
                json.put(ServerData.country,    Utils.getCountry(context));

                json.put(ServerData.app,        socketListener.getApp(context));
                json.put(ServerData.appver,     socketListener.getAppVer(context));
                json.put(ServerData.stamp,      String.valueOf(System.currentTimeMillis()));

                json.put(ServerData.token,      socketListener.getPush(context));
                json.put(ServerData.apihash,    socketListener.getHash(context));
                json.put(ServerData.apitoken,   socketListener.getToken(context));

                json.put(ServerSocket.EVENT,    EVENT_REGISTER);
                json.put(ServerData.apisign,    signJSON(EVENT_REGISTER, json));

                ServerSocket.getInstance().onEvent(EVENT_REGISTER, authDone);
            }
            else
            {
                json.put(ServerData.apihash,     socketListener.getHash(context));
                json.put(ServerData.apitoken,    socketListener.getToken(context));
                json.put(ServerSocket.EVENT,     EVENT_CONNECT);
                json.put(ServerData.apisign,     signJSON(EVENT_CONNECT, json));

                ServerSocket.getInstance().onEvent(EVENT_CONNECT, authDone);
            }

            ServerSocket.getInstance().onError(authError);

            ServerSocket.getInstance().emit(EVENT, json.toString());
        }
        catch (Exception e)
        {
            ServerSocket.getInstance().offEvent(EVENT_REGISTER, authDone);
            ServerSocket.getInstance().offEvent(EVENT_CONNECT, authDone);

            ServerSocket.getInstance().offError(authError);

            ServerSocket.setBusy(false);

            if(payload.errorListener != null) payload.errorListener.call(context.getString(R.string.err_unable_to_register_device));
        }
    }

    public static String signJSON(String event, JSONObject json)
    {
        String hash = "";

        hash = Utils.getSHA1(event);

        try
        {
            List<String> source = new ArrayList<>();
            Iterator<?> keys = json.keys();
            while( keys.hasNext() )
            {
                String key = (String)keys.next();
                source.add(key);
            }

            Collections.sort(source);

            for(String key : source)
            {
                if(key.equals(ServerSocket.EVENT)) continue;
                if(key.equals(ServerData.apisign)) continue;

                Object obj = json.get(key);
                if ( obj instanceof String )
                {
                    String data = (String)obj;
                    hash = Utils.getSHA1(hash + data);
                }
            }
        }
        catch (Exception e)
        {
            hash = "";
        }

        return hash;
    }

    public static String getError(Context mContext, Object[] args)
    {
        String msg = mContext.getString(R.string.err_server_unable);

        if(args != null && args.length > 0 && args[0] != null && args[0] instanceof String)
        {
            msg = args[0].toString();
        }

        return msg;
    }


    public static void queueAdd(SocketPayload pd)
    {
        if (payloadList == null) payloadList = new ArrayList<>();

        synchronized (payloadList)
        {
            payloadList.add(pd);
        }
    }


    public static void queuePurge()
    {
        if(payloadList == null || payloadList.size() == 0) return;

        synchronized (payloadList)
        {
            for (SocketPayload pd : payloadList)
            {
                if (pd.errorListener != null) {
                    pd.errorListener.call("An error occurred!");
                }
            }

            payloadList.clear();
        }
    }

    public static void queueDrain(Context context)
    {
        if(payloadList == null || payloadList.size() == 0) return;

        synchronized (payloadList)
        {
            for(SocketPayload pd : payloadList)
            {
                try
                {
                    pd.data.put(ServerData.event, pd.event);

                    ServerSocket.onError(pd.errorListener);
                    ServerSocket.onEvent(pd.event, pd.doneListener);
                    ServerSocket.getInstance().getSocket().emit(EVENT, pd.data);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();

                    if(pd.errorListener != null) pd.errorListener.call(context.getString(R.string.err_occurred));
                }
            }

            payloadList.clear();
        }
    }

    public static class PayloadAuthDoneListener implements Emitter.Listener
    {
        private Context context;
        private SocketPayload payload;
        private Emitter.Listener errorListener;


        public PayloadAuthDoneListener(Context context, SocketPayload payload)
        {
            this.context = context;
            this.payload = payload;
        }

        public void setAuthError(Emitter.Listener listener)
        {
            errorListener = listener;
        }

        @Override
        public void call(Object... args)
        {
            ServerSocket.getInstance().offEvent(EVENT_REGISTER, this);
            ServerSocket.getInstance().offEvent(EVENT_CONNECT, this);
            ServerSocket.getInstance().offError(errorListener);

            String error = "";
            try
            {
                final String response = args[0].toString();
                //Logger.e(TAG, "listenerConnect:" + response);

                JSONObject json = new JSONObject(new JSONTokener(response));
                if (json.has(ServerData.status))
                {
                    boolean status = json.getBoolean(ServerData.status);
                    String msg = json.optString(ServerData.message);

                    if (status)
                    {
                        if (json.has(ServerData.data))
                        {
                            socketListener.payloadDone(context, payload, json);

                            if(!TextUtils.isEmpty(payload.event) && payload.data != null)
                            {
                                ServerSocket.onError(payload.errorListener);
                                ServerSocket.onEvent(payload.event, payload.doneListener);

                                payload.data.put(ServerData.event, payload.event);
                                ServerSocket.getInstance().getSocket().emit(EVENT, payload.data);
                            }

                            EventBus.getDefault().post(new AppEvent(ServerData.socket_registered, 0));

                            queueDrain(context);

                            ServerSocket.setBusy(false);
                            ServerSocket.setRegistered(true);

                            return;
                        }
                        else
                        {
                            error = context.getString(R.string.err_server_invalid_data);
                        }
                    }
                    else
                    {
                        error = msg;
                    }
                }
                else
                {
                    error = context.getString(R.string.err_server_incorrect_response);
                }
            }
            catch (Exception e)
            {
                error = context.getString(R.string.err_occurred);
            }

            ServerSocket.setBusy(false);

            if(payload.errorListener != null) payload.errorListener.call(error);

            queuePurge();
        }
    }

    public static class PayloadAuthErrorListener implements Emitter.Listener
    {
        public Context context;
        public Emitter.Listener authDone;
        public Emitter.Listener errorListener;

        public PayloadAuthErrorListener(Context context, SocketPayload payload, Emitter.Listener authDone)
        {
            this.context = context;
            this.authDone = authDone;
            this.errorListener = payload.errorListener;
        }

        @Override
        public void call(Object... args)
        {
            ServerSocket.getInstance().offEvent(EVENT_REGISTER, authDone);
            ServerSocket.getInstance().offEvent(EVENT_CONNECT, authDone);

            ServerSocket.getInstance().offError(this);

            ServerSocket.setBusy(false);
            if(errorListener != null) errorListener.call(context.getString(R.string.err_authentication));

            queuePurge();
        }
    }

    public static class PayloadConnectDoneListener implements Emitter.Listener
    {
        private Context context;
        private SocketPayload payload;

        public Emitter.Listener connectError = null;

        public PayloadConnectDoneListener(Context context, SocketPayload payload)
        {
            this.context = context;
            this.payload = payload;
        }

        public void setConnectError(Emitter.Listener listener)
        {
            connectError = listener;
        }

        @Override
        public void call(Object... args)
        {
            ServerSocket.getInstance().offEvent(Socket.EVENT_CONNECT, this);
            ServerSocket.getInstance().offError(connectError);

            authWithPayload(context, payload);
        }
    }

    public static class PayloadConnectErrorListener implements Emitter.Listener
    {
        private Context context;
        private Emitter.Listener payloadListener;
        private Emitter.Listener connectListener;

        public PayloadConnectErrorListener(Context context, SocketPayload payload, Emitter.Listener connectListener)
        {
            this.context = context;
            this.payloadListener = payload.errorListener;
            this.connectListener = connectListener;
        }

        @Override
        public void call(Object... args)
        {
            String msg = context.getString(R.string.err_server_unable);
            //if(args[0] instanceof EngineIOException)

            ServerSocket.getInstance().offEvent(Socket.EVENT_CONNECT, connectListener);

            ServerSocket.getInstance().offError(this);

            ServerSocket.setBusy(false);
            if(payloadListener != null) payloadListener.call(msg);

            EventBus.getDefault().post(new AppEvent(ServerData.socket_disconnected, msg));

            queuePurge();
        }
    }

    public static class SocketPayload
    {
        public String event;
        public JSONObject data;
        public Emitter.Listener doneListener;
        public Emitter.Listener errorListener;

        public SocketPayload(String event, JSONObject payload, Emitter.Listener doneListener, Emitter.Listener errorListener)
        {
            this.event = event;
            this.data = payload;
            this.doneListener = doneListener;
            this.errorListener = errorListener;
        }
    }

    public interface SocketInterface
    {
        Object[] payloadDone(Context context, SocketPayload payload, JSONObject json);

        boolean shouldHome(Context context);

        String getApp(Context context);

        String getAppVer(Context context);

        String getHash(Context context);

        String getToken(Context context);

        String getPush(Context context);

        float getLon(Context context);

        float getLat(Context context);
    }
}

