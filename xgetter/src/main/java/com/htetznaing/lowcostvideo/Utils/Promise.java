package com.htetznaing.lowcostvideo.Utils;


public  abstract class Promise {

    public Object value = null;

    public void resolve(Object args){}

    public void reject( Exception error ) {}

    public void onHeaders(Object args){}

}
