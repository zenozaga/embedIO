package com.htetznaing.lowcostvideo.Sites;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;
import com.htetznaing.lowcostvideo.Utils.Helpers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.htetznaing.lowcostvideo.LowCostVideo.agent;
import static com.htetznaing.lowcostvideo.Utils.FacebookUtils.getFbLink;
import static com.htetznaing.lowcostvideo.Utils.Utils.putModel;

/*
This is direct link getter for Facebook
    By
Khun Htetz Naing
 */

public class GooglePhoto {
    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onTaskCompleted){
        AndroidNetworking.get(url)
                .addHeaders("User-agent", agent)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        if(response != null){

                            String regex = "\\<meta property\\=\\\"og\\:video\\:secure_url\\\" content\\=\\\"(.*?)\\\"";

                            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(response);

                            if(matcher.find()){

                                String url = matcher.group(1);
                                onTaskCompleted.onTaskCompleted(Helpers.uriToListXModel(url),false);
                                return ;

                            }

                        }


                        onTaskCompleted.onError();

                    }

                    @Override
                    public void onError(ANError anError) {
                        onTaskCompleted.onError();
                    }
                });
    }
}
