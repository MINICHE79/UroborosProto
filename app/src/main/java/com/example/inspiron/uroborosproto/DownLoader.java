package com.example.inspiron.uroborosproto;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by INSPIRON on 01/02/2018.
 */

public class DownLoader {
    public DownLoader(ArrayList<String> Images) {
        String destinationFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Uroboros/";


        String Name = "prueba";
        int nameIndex 	= 0;
        try {
            for (String url: Images) {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/Uroboros/"+ Name + (nameIndex++) + ".jpg");
                path.mkdirs();

                new ImageDownloader(url,destinationFolder + Name + (nameIndex++) + ".jpg").download();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
