package com.example.inspiron.uroborosproto;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by INSPIRON on 01/02/2018.
 */


public class ImageDownloader {
    private String imageUrl;
    private String destinationPath;

    public ImageDownloader(String imageUrl, String destinationPath) {
        this.imageUrl = imageUrl;
        this.destinationPath = destinationPath;
    }
    public void download() throws Exception {
        URL url = new URL(imageUrl);

        InputStream is = url.openStream();
        FileOutputStream fos = new FileOutputStream(destinationPath);

        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = is.read(buffer)) != -1)
            fos.write(buffer,0,bytesRead);

        fos.close();
        is.close();
    }
}
