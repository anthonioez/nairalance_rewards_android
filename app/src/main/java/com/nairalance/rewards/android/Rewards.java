package com.nairalance.rewards.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.miciniti.library.Files;
import com.miciniti.library.Links;
import com.miciniti.library.Utils;
import com.miciniti.library.helpers.UI;
import com.miciniti.library.io.Server;
import com.miciniti.library.io.ServerSocket;
import com.miciniti.library.objects.AppEvent;
import com.nairalance.rewards.android.activities.ActivityEarnings;
import com.nairalance.rewards.android.activities.ActivityFaq;
import com.nairalance.rewards.android.activities.ActivityHome;
import com.nairalance.rewards.android.activities.ActivityPayout;
import com.nairalance.rewards.android.activities.ActivityProfile;
import com.nairalance.rewards.android.activities.ActivityRankings;
import com.nairalance.rewards.android.activities.ActivityRewards;
import com.nairalance.rewards.android.activities.ActivityYoutube;
import com.nairalance.rewards.android.ads.AdmobReward;
import com.nairalance.rewards.android.io.ServerData;
import com.nairalance.rewards.android.modules.start.socket.SocketToken;
import com.nairalance.rewards.android.services.ServiceMain;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class Rewards
{
    private static final String TAG = Rewards.class.getSimpleName();

    public static final String DATABASE_NAME = "rewards.db";
    public static final int DATABASE_VERSION = 1;
    public static final int SCROLL_THRESHOLD = 5;
    public static final int SCROLL_DELAY = 1000;

    public static final long RESPONSE_DELAY = 500;

    public static final String ACTION_BOOT = "com.nairalance.rewards.android.boot";
    public static final String ACTION_ALARM = "com.nairalance.rewards.android.check";
    public static final String ACTION_CHECK = "com.nairalance.rewards.android.check";
    public static final String ACTION_TOKEN = "com.nairalance.rewards.android.token";

    public static final int REQUEST_CONTENT_VIEW = 2229;
    public static final int REQUEST_CONTENT_LIST = 2230;
    public static final int REQUEST_BROWSER = 333;
    public static final int PAGE_SIZE = 20;

    public static String appPhone = "+2348168838969";

    public static boolean loaded = false;
    public static String appUrl;
    public static String appUrlSocket;

    public static String app = "rw";
    public static String appVer;
    public static String appName;
    public static Rect screenSize;

    public static Picasso picasso;
    public static LruCache picassoLruCache;

    public static String facebookHandle = "Nairalance";
    public static String twitterHandle = "Nairalance";
    public static int feedback = 0;

    public static Typeface appFont;
    public static Typeface appFontLight;
    public static Typeface appFontBold;
    //public static Retrofit retrofit;
    public static String email = "";
    public static String password = "";

    public static AdmobReward rewardAdmob;
    //public static AudienceReward rewardAudience;

    public static String rewardAudienceId = "";
    public static String rewardAdmobId = "";
    public static String youtubeApiKey = "";

    public static void close(Context context)
    {
        Rewards.loaded = false;

        //Db.quit();

    }

    public static void quit(Context context)
    {
        close(context);

        System.exit(-1);
    }

    public static void load(Context context, ServerSocket.SocketInterface listener)
    {
        appName = context.getString(R.string.app_name);
        appVer = Utils.getVersion(context);
        appUrl = context.getString(R.string.app_url);


        youtubeApiKey = Strings.ykey1;
        youtubeApiKey += Strings.ykey2;
        youtubeApiKey += Strings.ykey3;


        appUrlSocket = appUrl + ":" + context.getString(R.string.app_socket);

        screenSize = UI.getScreenSize(context);

        appFont = Typeface.createFromAsset(context.getAssets(), "fonts/sourcesanspro-regular.ttf");
        appFontBold = Typeface.createFromAsset(context.getAssets(), "fonts/sourcesanspro-bold.ttf");
        appFontLight = Typeface.createFromAsset(context.getAssets(), "fonts/sourcesanspro-light.ttf");

        picassoLruCache = new LruCache(context);
        picasso = new Picasso.Builder(context).memoryCache(picassoLruCache).build();

        Server.setUserAgent(String.format("%s for Android %s", Rewards.appName, Rewards.appVer));

        ServerSocket.setSocketUrl(Rewards.appUrlSocket);
        ServerSocket.setSocketListener(listener);

        /*
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        OkHttpClient.Builder cbuilder = new OkHttpClient.Builder();
        cbuilder.connectTimeout(120, TimeUnit.SECONDS);
        cbuilder.connectTimeout(240, TimeUnit.SECONDS);

        retrofit = new Retrofit.Builder()
                .baseUrl(appUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(cbuilder.build())
                .build();
        */

        //rewardAudienceId = context.getString(R.string.audience_reward_main);
        rewardAdmobId = context.getString(R.string.admob_reward);

        Rewards.loaded = true;
    }

    public static void openSupport(Context context)
    {
        String body = "";
        body += "\r\nVersion: " + Rewards.appVer;
        body += "\r\nOS: " + Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")";
        body += "\r\nPhone: " + Build.MANUFACTURER.toUpperCase() + " " + Build.MODEL;

        Links.openMail(context, context.getString(R.string.app_email), "Rewards for Android", body);
    }

    public static void shareApp(Context context)
    {
        Links.shareText(context, "Rewards for Android", "Download the Rewards App from https://nairalance.com/rewards/apps", null);
    }

    public static void shareText(Context context, String text)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        Intent openin = Intent.createChooser(intent, "Share To...");
        context.startActivity(openin);
    }

    public static int notificationIcon()
    {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_status : R.mipmap.ic_launcher;
    }

    public static void restartMain(Activity activity)
    {
        Intent intent1 = new Intent(activity, ActivityHome.class);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(intent1.getComponent());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


        Intent mainIntent = intent; //IntentCompat.makeRestartActivityTask();
        activity.startActivity(mainIntent);
    }

    public static boolean isUser(Context context)
    {
        return Prefs.getId(context) > 0 && !TextUtils.isEmpty(Prefs.getApiToken(context));
    }

    public static void login(Context context)
    {
        EventBus.getDefault().post(new AppEvent("login"));

        RewardsAnalytics.logEvent(context, "login", Prefs.getUsername(context));
    }

    public static void signin(Context context)
    {
        EventBus.getDefault().post(new AppEvent("signin"));

        RewardsAnalytics.logEvent(context, "signin", Prefs.getUsername(context));
    }

    public static void logout(Context context)
    {
        ServerSocket.disconnect();

        String username = Prefs.getUsername(context);

        Prefs.setApiHash(context, "");
        Prefs.setApiToken(context, "");
        Prefs.setThumbUrl(context, "");
        Prefs.setEmail(context, "");
        Prefs.setPushTokenSent(context, false);

        Prefs.setId(context, 0);

        EventBus.getDefault().post(new AppEvent("logout"));
    }

    public static void sendVerifySMS(Context context)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("smsto:" + Rewards.appPhone));
        //intent.setType("text/plain");
        intent.putExtra("sms_body","VERIFY");

        Intent chooser = Intent.createChooser(intent, "Send with...");
        if (intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(chooser);
        }
        else
        {
            UI.toast(context, "No SMS app found!");
        }
    }

    public static void clearCache(Context context)
    {
        picassoLruCache.clear();

        Files.deleteFolder(context.getCacheDir());
    }

    public static void registerForPush(Context context)
    {
        //String  token   = Prefs.getPushToken(context);
        //boolean status  = Prefs.getPushTokenSent(context);

        /*
        String token = FirebaseInstanceId.getInstance().getToken();

        if (!TextUtils.isEmpty(token)) //check last attempt stamp
        {
            Prefs.setPushToken(context, token);

            SocketToken tkn = new SocketToken(context, null);
            tkn.start();
        }*/
    }

    public static void invite(Activity activity)
    {
        //String msg = getString(R.string.share_app_text) + getContext().getPackageName();
        String link = "https://nairalance.com/rewards/apps";
        String msg = "Earn rewards for performing simple tasks. Get the Rewards App available at " + link;

        /*
        Intent intent = new AppInviteInvitation.IntentBuilder("Invite friends")
                .setMessage(msg)
                .setCustomImage(Uri.parse("http://nairalance.com/images/reward.png"))
                //.setDeepLink(Uri.parse(link))
                //.setCallToActionText("Download")
                .build();

        activity.startActivityForResult(intent, 200);
        */

        Links.shareText(activity, "", msg, null);
    }

    public static void askFeedback(final Activity activity)
    {
        UI.ask(activity, activity.getString(R.string.ask_like), null, activity.getString(R.string.yes_), activity.getString(R.string.not_really), new Runnable()
        {
            @Override
            public void run()
            {
                Prefs.setLastFeedback(activity, Rewards.feedback);

                UI.ask(activity, activity.getString(R.string.ask_rating), null, activity.getString(R.string.ok_sure), activity.getString(R.string.no_thanks), new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Prefs.setLastFeedback(activity, Rewards.feedback + 1);
                        rateApp(activity);
                    }
                }, null);
            }
        }, new Runnable()
        {
            @Override
            public void run()
            {
                Prefs.setLastFeedback(activity, Rewards.feedback);
                UI.ask(activity, activity.getString(R.string.ask_feedback), null, activity.getString(R.string.ok_sure), activity.getString(R.string.no_thanks), new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Prefs.setLastFeedback(activity, Rewards.feedback + 1);
                        Rewards.openSupport(activity);
                    }
                }, null);
            }
        });
    }

    public static void rateApp(Context context)
    {
        String url = String.format(context.getString(R.string.appirator_market_url), context.getPackageName());
        try
        {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
        catch (Exception e)
        {
            url = String.format(context.getString(R.string.appirator_playstore_url), context.getPackageName());
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    public static void serviceStart(Context context, String action)
    {
        Intent intent = new Intent(context, ServiceMain.class);
        intent.setAction(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            context.startForegroundService(intent);
        }
        else
        {
            context.startService(intent);
        }
    }

    public static void serviceStop(Context context)
    {
        Intent intent = new Intent(context, ServiceMain.class);
        context.stopService(intent);
    }

    public static void startMain(Activity activity)
    {
        Intent intent = activity.getIntent();
        Uri data = activity.getIntent().getData();
        String path = intent.getStringExtra(Strings.path);
        if (data != null)
        {
            Intent newIntent = new Intent(activity, ActivityHome.class);
            newIntent.putExtra(Strings.sender, Strings.link);
            newIntent.putExtra(Strings.url, data.toString());
            newIntent.putExtra(Strings.path, data.getPath());
            activity.startActivity(newIntent);
        }
        else if (path != null)
        {
            intent.setClass(activity, ActivityHome.class);
            intent.putExtra(Strings.sender, Strings.push);
            activity.startActivity(intent);
        }
        else
        {
            Intent newIntent = new Intent(activity, ActivityHome.class);
            activity.startActivity(newIntent);
        }
    }

    public static File getCameraFile(Context context)
    {
        File imagePath = new File(context.getFilesDir(), "images");
        imagePath.mkdirs();
        return new File(imagePath, "camera.jpg");
    }

    public static int typeImage(String type)
    {
        switch (type)
        {
            case "invite":
                return R.drawable.type_invite;

            case "admob":
                return R.drawable.type_admob;

            case "youtube":
                return R.drawable.type_youtube;

            case "audience":
                return R.drawable.type_facebook;

            case "facebook":
                return R.drawable.type_facebook;

            case "instagram":
                return R.drawable.type_instagram;

            case "twitter":
                return R.drawable.type_twitter;

            case "android":
                return R.drawable.type_android;

        }
        return R.mipmap.ic_launcher;
    }

    public static String payoutType(String type)
    {
        switch (type)
        {
            case "airtime":
                return "Airtime";

            case "bank":
                return "Bank Transfer";

            default:
                return "Unknown";
        }
    }

    public static int providerImage(String type)
    {
        switch (type)
        {
            case "airtime":
                return R.drawable.ic_provider_airtime;

            case "bank":
                return R.drawable.ic_provider_bank;

            default:
                return R.mipmap.ic_launcher;
        }
    }

    public static void processIntent(Activity activity, Intent intent)
    {
        String sender = intent.getStringExtra(Strings.sender);
        if (sender != null)
        {
            if (sender.equals(Strings.push))
            {
                Rewards.processPush(activity, intent);
            }
        }
        else
        {
            Rewards.processLink(activity, intent);
        }
    }

    public static void processPush(Activity activity, Intent intent)
    {
        String pid = intent.getStringExtra(ServerData.pid);
        String title = intent.getStringExtra(Strings.title);
        String path = intent.getStringExtra(Strings.path);

        RewardsAnalytics.logEvent(activity, "push_open", pid);

        if (path == null)
        {
            return;
        }

        if (path.equals(Strings.rewards))
        {
            long id = Utils.getLong(intent.getStringExtra(Strings.data));

            String type = intent.getStringExtra(Strings.type);
            if (id != 0 && !TextUtils.isEmpty(title))
            {
                Intent newIntent = new Intent(activity, ActivityRewards.class);
                newIntent.putExtra(ServerData.id, id);
                newIntent.putExtra(ServerData.type, type);
                newIntent.putExtra(ServerData.title, title);
                activity.startActivity(newIntent);
            }
        }
        else if (path.equals(Strings.youtube))
        {
            String data = intent.getStringExtra(Strings.data);  //TODO ??
            if (data != null)
            {
                Intent newIntent = new Intent(activity, ActivityYoutube.class);
                newIntent.putExtra(Strings.item, data);
                activity.startActivity(newIntent);
            }
        }
        else if (path.equals(Strings.rankings))
        {
            Intent newIntent = new Intent(activity, ActivityRankings.class);
            activity.startActivity(newIntent);
        }
        else if (path.equals(Strings.earnings))
        {
            Intent newIntent = new Intent(activity, ActivityEarnings.class);
            activity.startActivity(newIntent);
        }
        else if (path.equals(Strings.cashout))
        {
            Intent newIntent = new Intent(activity, ActivityPayout.class);
            activity.startActivity(newIntent);
        }
        else if (path.equals(Strings.faq))
        {
            Intent newIntent = new Intent(activity, ActivityFaq.class);
            activity.startActivity(newIntent);
        }
        else if (path.equals(Strings.profile))
        {
            Intent newIntent = new Intent(activity, ActivityProfile.class);
            activity.startActivity(newIntent);
        }
        else if (path.equals(Strings.link))
        {
            String data = intent.getStringExtra(Strings.data);
            if (data != null)
            {
                Links.openExternalUrl(activity, data);
            }
        }
        else if (path.equals(Strings.rate))
        {
            Links.openStoreUrl(activity);
        }
    }

    public static void processLink(Activity activity, Intent intent)
    {
        String action = intent.getAction();
        Uri data = intent.getData();
        if (data != null)
        {
            String host = data.getHost();
            if (host != null && (host.equals("nairalance.com") || host.equals("www.nairalance.com")))
            {
                String path = data.getPath();
                if (path != null)
                {
                    /*
                    String match = path.substring(1);

                    if(match.equals(Strings.feed))
                    {
                        viewPager.setCurrentItem(0);
                    }
                    else if(match.equals(Strings.sources))
                    {
                        viewPager.setCurrentItem(1);
                    }
                    else if(match.equals(Strings.trending))
                    {
                        viewPager.setCurrentItem(2);
                    }
                    else if(match.equals(Strings.interests))
                    {
                        viewPager.setCurrentItem(3);
                    }
                    else if(match.equals(Strings.account))
                    {
                        viewPager.setCurrentItem(4);
                    }
                    else if(match.equals(Strings.favorites))
                    {
                        Catchup.favorites(activity);
                    }
                    else
                    {
                        List<String> segments = data.getPathSegments();
                        if(segments != null && segments.size() > 1)
                        {
                            match = segments.get(0);
                            long id = Utils.getLong(segments.get(1));
                            if(id != 0)
                            {
                                if(match.equals(Strings.content))  //content/xx
                                {
                                    //content(id);
                                }
                                else if(match.equals(Strings.source))   //source/xx
                                {
                                    source(id);
                                }
                                else if(match.equals(Strings.interest)) //interest/xx
                                {
                                    interest(id);
                                }
                            }
                        }
                    }*/
                }
            }
        }
    }
}