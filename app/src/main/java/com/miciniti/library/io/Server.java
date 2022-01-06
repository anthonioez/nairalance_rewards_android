package com.miciniti.library.io;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.miciniti.library.Logger;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.ConnectionSpec;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Miciniti onEvent 19/04/16.
 */
public class Server
{
    private static final String TAG = Server.class.getSimpleName();
    private static final String LINE_FEED = "\r\n";

    private static final int READ_TIMEOUT  = 15000;
    private static final int WRITE_TIMEOUT = 15000;
    private static final int CONN_TIMEOUT  = 15000;

    private static OkHttpClient client = defaultOkHttpClient();

    private static String userAgent = "Miciniti";

    public static boolean isOnline(Context mContext)
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    public static boolean isWifiOnline(Context mContext)
    {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi != null && mWifi.isConnected())
        {
            return true;
        }

        return false;
    }

    public static void setUserAgent(String agent)
    {
        userAgent = agent;
    }

    public static String getUserAgent()
    {
        return userAgent;
    }

    public static String getQueryString(HashMap<String, String> params)
    {
        String res = "";
        try {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) first = false;
                else result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            res = result.toString();
        } catch (Exception e) {

        }
        return res;
    }

    public static RequestBody getQueryBody(HashMap<String, String> params)
    {
        FormBody.Builder builder =  new FormBody.Builder();;

        for (Map.Entry<String, String> entry : params.entrySet())
        {
            builder.add(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    private static OkHttpClient defaultOkHttpClient()
    {
        //CookieManager cookieManager = new CookieManager();
        //cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(Server.READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(Server.WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(Server.CONN_TIMEOUT, TimeUnit.MILLISECONDS)
                .addInterceptor(new UserAgentInterceptor())
                .cookieJar(new CookieJarStore());

        /*
        if(Rewards.appUrl.startsWith("https"))
        {
            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    //.tlsVersions(TlsVersion.TLS_1_2)
                    //.supportsTlsExtensions(true)
                    //.allEnabledCipherSuites()
                    //.allEnabledTlsVersions()
                    .build();

            builder.connectionSpecs(Collections.singletonList(spec));
            builder.hostnameVerifier(new AllHostnameVerifier());
        }*/


        OkHttpClient client = builder.build();
        //client.interceptors().add(new AddCookiesInterceptor());
        //client.interceptors().add(new ReceivedCookiesInterceptor());

        return client;
    }

    public static ServerResponse send(Context context, ServerRequest request)
    {
        if (request.method.equals("POST"))
        {
            return post(context, request);
        }
        else
        {
            return get(context, request);
        }
    }

    private static ServerResponse get(Context context, ServerRequest request)
    {
        ServerResponse response = null;

        String link = request.url();
        String query = getQueryString(request.params);
        if (query.length() > 0) link += "?" + query;

        Logger.e(TAG, "get: " + link);

        //OkHttpClient client = defaultOkHttpClient();

        long start = System.currentTimeMillis();

        try
        {

            Request req = new Request.Builder()
                    .url(link)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();

            Response res = client.newCall(req)
                    .execute();

            int code = res.code();
            byte[] data =  res.body().bytes();

            response = new ServerResponse();
            response.code = code;
            response.data = data;
        }
        catch (SocketTimeoutException e)
        {
            response = new ServerResponse();
            response.error = "Connection timeout!";

            Logger.e(TAG, "get Exception: " + e.toString());
        }
        catch (InterruptedIOException e)
        {
            response = new ServerResponse();
            response.error = "Connection interrupted!";

            Logger.e(TAG, "get Exception: " + e.toString());
        }
        catch (IOException e)
        {
            response = new ServerResponse();
            response.error = "IO Error!";

            Logger.e(TAG, "get Exception: " + e.toString());
        }
        catch (Exception e)
        {
            Logger.e(TAG, "get Exception: " + e.toString());
        }

        Logger.e(TAG, "get: elapsed: " + (System.currentTimeMillis() - start) + "ms");

        return response;
    }

    private static ServerResponse post(Context context, ServerRequest request)
    {
        ServerResponse response = null;

        String link = request.url();

        Logger.e(TAG, "post: " + link);

        //OkHttpClient client = defaultOkHttpClient();

        long start = System.currentTimeMillis();
        try
        {
            RequestBody body = getQueryBody(request.params);

            Request req = new Request.Builder()
                    .url(link)
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .post(body)
                    .build();

            Response res = client.newCall(req)
                    .execute();

            int code = res.code();
            byte[] data =  res.body().bytes();

            response = new ServerResponse();
            response.code = code;
            response.data = data;
        }
        catch (SocketTimeoutException e)
        {
            response = new ServerResponse();
            response.error = "Connection timeout!";

            Logger.e(TAG, "post Exception: " + e.toString());
            e.printStackTrace();
        }
        catch (InterruptedIOException e)
        {
            response = new ServerResponse();
            response.error = "Connection interrupted!";

            Logger.e(TAG, "post Exception: " + e.toString());

            e.printStackTrace();
        }
        catch (IOException e)
        {
            response = new ServerResponse();
            response.error = "Connection IO Error!";

            Logger.e(TAG, "post Exception: " + e.toString());

            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            Logger.e(TAG, "post Exception: " + e.toString());
        }

        Logger.e(TAG, "post: elapsed: " + (System.currentTimeMillis() - start) + "ms");

        return response;
    }

    /*
    public static ServerResponse file(Context context, ServerRequest request)
    {
        request.method = ServerData.POST;

        if (request.endpoint.startsWith("https"))
            return uploads(context, request);
        else
            return upload(context, request);
    }

    public static ServerResponse upload(Context context, ServerRequest request)
    {
        ServerResponse response = null;
        HttpURLConnection conn = null;

        // creates a unique boundary based onEvent time stamp
        String boundary = "===" + System.currentTimeMillis() + "===";

        Logger.e(TAG, "upload: " + request.url());

        try {
            URL url = new URL(request.url());

            conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(Servers.READ_TIMEOUT * 2);
            conn.setConnectTimeout(Servers.CONN_TIMEOUT * 2);
            conn.setRequestMethod(request.method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("User-Agent", userAgent());

            OutputStream os = conn.getOutputStream();

            uploadData(context, request, boundary, os);

            response = new ServerResponse();
            response.code = conn.getResponseCode();

            if (response.code == HttpsURLConnection.HTTP_OK) {
                response.data = Utils.streamBytes(conn.getInputStream());
            }
            else
            {
                response.data = Utils.streamBytes(conn.getErrorStream());
            }
        }
        catch (FileNotFoundException e)
        {
            response = new ServerResponse();
            response.code = 0;
            response.error = "File not found or file permission denied!";

            Logger.e(TAG, "upload Exception: " + e.toString());
        }
        catch (Exception e)
        {
            response = new ServerResponse();
            response.code = 0;
            response.error = "An error occurred!";

            Logger.e(TAG, "upload Exception: " + e.toString());
        }
        finally {
            try {
                if (conn != null) conn.disconnect();
            } catch (Exception e) {
            }

        }

        return response;
    }

    public static ServerResponse uploads(Context context, ServerRequest request)
    {
        ServerResponse response = null;
        HttpsURLConnection conn = null;

        // creates a unique boundary based onEvent time stamp
        String boundary = "===" + System.currentTimeMillis() + "===";

        Logger.e(TAG, "uploads: " + request.url());

        try {
            URL url = new URL(request.url());

            conn = (HttpsURLConnection) url.openConnection();

            conn.setReadTimeout(Servers.READ_TIMEOUT * 2);
            conn.setConnectTimeout(Servers.CONN_TIMEOUT * 2);

            conn.setRequestMethod(request.method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.setRequestProperty("User-Agent", userAgent());

            // Create the SSL connection
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());

            OutputStream os = conn.getOutputStream();

            uploadData(context, request, boundary, os);

            response = new ServerResponse();
            response.code = conn.getResponseCode();

            if (response.code == HttpsURLConnection.HTTP_OK) {
                response.data = Utils.streamBytes(conn.getInputStream());
            } else {
                response.data = Utils.streamBytes(conn.getErrorStream());
            }

        }
        catch (FileNotFoundException e)
        {
            response = new ServerResponse();
            response.code = 0;
            response.error = "File not found or file permission denied!";

            Logger.e(TAG, "upload Exception: " + e.toString());
        }
        catch (Exception e)
        {
            response = new ServerResponse();
            response.code = 0;
            response.error = "An error occurred!";

            Logger.e(TAG, "uploads Exception: " + e.toString());
        } finally {
            try {
                if (conn != null) conn.disconnect();
            } catch (Exception e) {
            }

        }

        return response;
    }

    private static void uploadData(Context context, ServerRequest request, String boundary, OutputStream os) throws Exception
    {
        String data;
        StringBuilder result;

        BufferedOutputStream writer = new BufferedOutputStream(os);

        for (Map.Entry<String, FileItem> entry : request.files.entrySet())
        {
            String name = URLEncoder.encode(entry.getKey(), "UTF-8");

            FileItem item = entry.getValue();
            String filename = item.name;    //Utils.getEncodedQuery(context, item.name);

            result = new StringBuilder();
            result.append("--" + boundary).append(LINE_FEED);
            result.append("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + filename + "\"").append(LINE_FEED);
            result.append("Content-Type: " + URLConnection.guessContentTypeFromName(item.name)).append(LINE_FEED);
            result.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            result.append(LINE_FEED);

            data = result.toString();

            writer.write(data.getBytes("UTF-8"));
            writer.flush();

            Logger.e(TAG, "uploadData opening " + item.link);

            InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(item.link));
            if(item.size == 0)
            {
                item.size = inputStream.available();
            }

            if (request.errorListener != null) {
                request.errorListener.requestSize(item.size);
            }

            Logger.e(TAG, "uploadData file uploading");

            //FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[16384];
            int bytesRead = -1;
            long last = 0;
            long total = 0;
            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                writer.write(buffer, 0, bytesRead);

                total += bytesRead;

                if (request.errorListener != null) {
                    long now = System.currentTimeMillis();
                    if (last == 0 || (now - last) > 500) {
                        request.errorListener.requestWrite(total);
                        last = now;
                        total = 0;
                    }
                }

                //Utils.sleeper(50);
            }

            Logger.e(TAG, "uploadData file done");

            if (request.errorListener != null) {
                request.errorListener.requestWrite(item.size);
            }

            writer.flush();
            inputStream.close();

            writer.write(LINE_FEED.getBytes());
            writer.flush();
        }

        Logger.e(TAG, "uploadData data uploading ");

        result = new StringBuilder();
        for (Map.Entry<String, String> entry : request.params.entrySet()) {
            String name = URLEncoder.encode(entry.getKey(), "UTF-8");
            String value = URLEncoder.encode(entry.getValue(), "UTF-8");

            result.append("--" + boundary).append(LINE_FEED);
            result.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
            result.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
            result.append(LINE_FEED);
            result.append(value).append(LINE_FEED);
        }
        //            result.append(LINE_FEED);
        result.append("--" + boundary + "--").append(LINE_FEED);

        data = result.toString();
        writer.write(data.getBytes("UTF-8"));
        writer.flush();

        Logger.e(TAG, "uploadData data done ");

        writer.close();
        os.close();
    }
    */

    public static SSLContext getSslContext()
    {
        try
        {
            /*
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509","BC");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(derInputStream);
            String alias = cert.getSubjectX500Principal().getName();
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, cert);
            */


            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);

            // creating a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            //TrustManager[] trustAllCerts = new TrustManager[]{new AllX509TrustManager()};
            TrustManager[] trustAllCerts = tmf.getTrustManagers();

            // creating an SSLSocketFactory that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, null);

            return sslContext;
        }
        catch (Exception e)
        {

        }

        return null;
    }

    public static void reset()
    {
        client = defaultOkHttpClient();
    }


    public static class AllX509TrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
        {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException
        {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return new java.security.cert.X509Certificate[] {};
        }
    }

    public static class AllHostnameVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(String s, SSLSession sslSession)
        {
            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
            return hv.verify("nairalance.com", sslSession);
            //return true;
        }
    }

    public static class UserAgentInterceptor implements Interceptor
    {
        public UserAgentInterceptor()
        {

        }

        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException
        {
            Request originalRequest = chain.request();

            Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", userAgent)
                    .build();

            return chain.proceed(requestWithUserAgent);
        }
    }

    public static class CookieJarStore implements CookieJar
    {
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
        {
            //Saves cookies from HTTP response
            //If the response includes a trailer this method is called second time
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url)
        {
            //Load cookies from the jar for an HTTP request.
            //This method returns cookies that have not yet expired

            List<Cookie> validCookies = new ArrayList<>();

            /*
            List<Cookie> expiredCookies = new ArrayList<>();

            Collection<Cookie> store = Cook.list();
            for (Cookie cookie : store)
            {
                if (cookie.expiresAt() < System.currentTimeMillis())
                {
                    expiredCookies.add(cookie);
                }
                else
                {
                    //Cook.log(cookie, "outgoing**************");
                    validCookies.add(cookie);
                }
            }

            Cook.remove(expiredCookies);
            */

            return validCookies;
        }

    }

}
