package com.htetznaing.lowcostvideo.Sites;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;
import com.htetznaing.lowcostvideo.Utils.Helpers;
import com.htetznaing.lowcostvideo.Utils.Promise;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StreamTape {

    private static WebView webView;
    private static String COOKIE = null;
    private static OkHttpClient client = null;
    private static Headers heasers = Headers.of(new HashMap<String, String>() {
        {
            put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0.1; Moto G (4)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Mobile Safari/537.36");
            put("Host", "streamtape.net");
            put("Referer", "https://streamtape.net/");
        }
    });

    private static CookieJar cookieJar = new CookieJar() {
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


    public static void fetch(Context context, String url, final LowCostVideo.OnTaskCompleted onTaskCompleted) {

        String ID = getID(url);
        String _url = "https://streamtape.net/e/" + ID;
        Call call;

        webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLoadsImagesAutomatically(false);
        webView.getSettings().setBlockNetworkLoads (true);



        if (ID == null) {
            onTaskCompleted.onError();
            return;
        }


        final Activity activity = (Activity) context;


        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                Log.e("URL",url);

                if(url != null && url.indexOf("https://streamtape.net/e/") > -1){
                    view.evaluateJavascript(
                            "(function() { " +
                                    "function checkElement(ele, callback) {\n" +
                                    "\n" +
                                    "    var intervalo = setInterval(function () {\n" +
                                    "        var element = document.querySelector(ele);\n" +
                                    "        if (element) {\n" +
                                    "            clearInterval(intervalo);\n" +
                                    "            callback(element);\n" +
                                    "        }\n" +
                                    "    }, 10);\n" +
                                    "\n" +
                                    "\n" +
                                    "    return function cancel() {\n" +
                                    "        clearInterval(intervalo);\n" +
                                    "    }\n" +
                                    "\n" +
                                    "\n" +
                                    "};\n" +
                                    "\n" +
                                    "function htmlDecode(input) {\n" +
                                    "    var e = document.createElement('textarea');\n" +
                                    "    e.innerHTML = input;\n" +
                                    "    // handle case of empty input\n" +
                                    "    return e.childNodes.length === 0 ? \"\" : e.childNodes[0].nodeValue;\n" +
                                    "};\n" +
                                    "\n" +
                                    "\n" +
                                    "function videLinkElement(callback) {\n" +
                                    "\n" +
                                    "    return document.querySelector(\"#videolink\");\n" +
                                    "\n" +
                                    "};\n" +
                                    "\n" +
                                    "\n" +
                                    "if (!videLinkElement()) {\n" +
                                    "\n" +
                                    "        window.location.href = \"data:blank_#ERROR#\";\n" +
                                    "\n" +
                                    "} else {\n" +
                                    "\n" +
                                    "    checkElement(\"#mainvideo\", function (ele) {\n" +
                                    "        ele.click();\n" +
                                    "        var videolink = videLinkElement().innerHTML;\n" +
                                    "        window.location.href = \"data:blank_#URL#https:\" + htmlDecode(videolink)\n" +
                                    "    });\n" +
                                    "\n" +
                                    "};\n" +
                                    "\n" +
                                    "\n" +
                                    "\n" +
                                    "\n" +
                                    "return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String html) {
                                    Log.e("HTML", html);
                                    // code here
                                }
                            });

                }else if( url != null && url.indexOf("blank_#URL#") > -1){

                    String link = url.split("blank_#URL#")[1];

                    destroyWebView();

                    try{

                        Uri.parse(link);
                        onTaskCompleted.onTaskCompleted(Helpers.uriToListXModel(link),false);

                    }catch (Exception e){

                        onTaskCompleted.onError();

                    }

                }else if(url != null && url.indexOf("blank_#ERROR#") > -1){

                    onTaskCompleted.onError();
                    destroyWebView();

                }


             }
        });

        webView.loadUrl(_url);



    }


    private static void destroyWebView() {
        if (webView!=null) {
            webView.loadUrl("about:blank");
            webView.destroy();
        }
    }



    private static String getID(String string) {

        String returner = null;


        if (string != null && string.indexOf("streamtape.net/") > -1) {

            String id = string.split("streamtape.net/")[1];
            id = id.replace("v/", "");
            id = id.replace("e/", "");

            if (!id.trim().isEmpty()) {
                returner = id.trim();
            }

        }



        if (returner == null && string.indexOf("streamtape.com/") > -1) {

            String id = string.split("streamtape.com/")[1];
            id = id.replace("v/", "");
            id = id.replace("e/", "");

            if (!id.trim().isEmpty()) {
                returner = id.trim();
            }

        }


        return returner;
    }

}