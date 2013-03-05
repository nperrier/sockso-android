package com.pugh.sockso.android;

import java.io.IOException;

import android.graphics.Bitmap;

public interface SocksoServer {

    public String getRootUrl();
    
    public String getStreamUrl(long id);

    public Bitmap downloadBitmap(String url);

    public String doGet(String url) throws IOException;

}
