package com.nairalance.rewards.android.helpers;

public class AdBlocker
{
    /*
    private static final Set<String> AD_HOSTS = new HashSet<>();

    public static boolean isAd(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        return isAdHost(httpUrl != null ? httpUrl.host() : "");
    }

    private static boolean isAdHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        int index = host.indexOf(".");
        return index >= 0 && (AD_HOSTS.contains(host) ||
                index + 1 < host.length() && isAdHost(host.substring(index + 1)));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static WebResourceResponse createEmptyResource()
    {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }

    public class AdBlocker {
        private static final String AD_HOSTS_FILE = "pgl.yoyo.org.txt";
        private static final Set<String> AD_HOSTS = new HashSet<>();

        public static void init(Context context) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        loadFromAssets(context);
                    } catch (IOException e) {
                        // noop
                    }
                    return null;
                }
            }.execute();
        }

        @WorkerThread
        private static void loadFromAssets(Context context) throws IOException {
            InputStream stream = context.getAssets().open(AD_HOSTS_FILE);
            BufferedSource buffer = Okio.buffer(Okio.source(stream));
            String line;
            while ((line = buffer.readUtf8Line()) != null) {
                AD_HOSTS.add(line);
            }
            buffer.close();
            stream.close();
        }
    }
*/
}
