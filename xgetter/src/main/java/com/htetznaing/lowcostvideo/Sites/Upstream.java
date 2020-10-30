package com.htetznaing.lowcostvideo.Sites;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.CookieManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;
import com.htetznaing.lowcostvideo.Utils.Helpers;
import com.htetznaing.lowcostvideo.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static com.htetznaing.lowcostvideo.LowCostVideo.agent;
import static com.htetznaing.lowcostvideo.Utils.Utils.putModel;
import static com.htetznaing.lowcostvideo.Utils.Utils.sortMe;

/*
This is direct link getter for UpToStream,UpToBox
    By
Khun Htetz Naing
 */

public class Upstream {
    private static String COOKIE = null;
    public static void fetch(final String url, final LowCostVideo.OnTaskCompleted onComplete) {

        String ID = getUpStreamID(url);


        if(ID == null) {
            onComplete.onError();
            return ;
        }

        CookieJar cookieJar = new CookieJar() {
            private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                COOKIE = cookies.toString();
                cookieStore.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url.host());
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        Log.e("Info","https://upstream.to/"+ID);

        AndroidNetworking.get("https://upstream.to/"+ID)
                .setOkHttpClient(okHttpClient)
                .addHeaders("User-agent", "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        Uri link = null;
                        String code = Helpers.FromTo(response,"master|urlset|","|hls");
                        String subdomain = Helpers.FromTo(response,"|srt|file|","|");

                        try{

                            ArrayList<XModel> models = new ArrayList<>();

                            link = Uri.parse("https://"+subdomain+".upstream.to/hls/,"+code+",.urlset/master.m3u8");
                            Utils.putModel(link.toString(),"720p",models);

                            onComplete.onTaskCompleted(models,false);


                        }catch(Exception r){

                            onComplete.onError();

                        };


                    }

                    private String get(String regex,String code){
                        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                        final Matcher matcher = pattern.matcher(code);
                        code = null;
                        while (matcher.find()) {
                            for (int i = 1; i <= matcher.groupCount(); i++) {
                                code = matcher.group(i);
                            }
                        }

                        return code;
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("info",anError.getErrorBody());
                        onComplete.onError();
                    }
                });
    }



    private static String getUpStreamID(String string) {

        String returner = null;


        if(string != null && string.indexOf("upstream.to/") > -1){

            String id = string.split("upstream.to/")[1];

            if(!id.trim().isEmpty()){
                returner = id.trim();
            };
        }

        return returner;
    }

    private static String parse(String response){

        return null;
    }

}
