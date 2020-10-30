package com.htetznaing.lowcostvideo.Utils;

import androidx.annotation.NonNull;
import com.htetznaing.lowcostvideo.Model.XModel;
import java.util.ArrayList;

public class Helpers {

    static public String FromTo(String source , String from, String to){


        if(source != null && from != null && to != null){

            Integer indeof = source.indexOf(from);

            if(indeof > -1){

                source = source.substring(indeof).replace(from,"");

                Integer toIndexOf = source.indexOf(to);

                if(toIndexOf > -1){
                    source = source.substring(0,toIndexOf);
                }

            }

        }


        return source;

    }


    static public XModel findXmodel(String quality, ArrayList<XModel> models){

        XModel returner = null;

        for (int i = 0; i < models.size(); i++) {

            XModel model = models.get(i);

            if(model.getQuality().equals(quality)){
                returner = model;
            };
        }




        if(returner == null){

            for (int i = 0; i < models.size(); i++) {

                XModel model = models.get(i);
                String quality_ = model.getQuality();

                if(quality_.equals("480p") || quality_.equals("360p")  || quality_.equals("720p") ){

                    if(returner == null){
                        returner = model;
                    }

                }

            }

        }


        if(returner == null){

            returner = models.get(0);

        }

        return returner;

    }


    static  public XModel  uriToXModel(String url){

        XModel model = new XModel();
        model.setUrl(url);
        model.setQuality("720p");
        model.setCookie("");

        return model;

    }


    static  public ArrayList<XModel> uriToListXModel(@NonNull String url ){


        ArrayList<XModel> list = new ArrayList<XModel>();
        list.add(uriToXModel(url));

        return list;

    }


}
