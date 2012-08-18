package com.pugh.sockso.android;

import java.io.IOException;

import android.graphics.Bitmap;

public interface SocksoServer {

    public String getRootUrl();

    public Bitmap downloadBitmap(String url);

    public String doGet(String url) throws IOException;

}
