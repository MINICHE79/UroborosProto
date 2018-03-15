package com.example.inspiron.uroborosproto;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by INSPIRON on 07/03/2018.
 */

public class ExtractThreads {

    ArrayList<String> Html;
    ArrayList<String> Threads;
    ArrayList<String> Images;
    private String url = null; //Prueba
    private Document doc; //elemento del Jsoup para guardar los links
    private String board; //los tipos de links que se extraerán
    private String Title;
    private Context context = null;
    Pattern pat;
    Pattern patIm;
    Matcher mat;

    public ExtractThreads(Context context) throws IOException {
        this.context = context;

    }

    public void boards(String board) throws ExecutionException, InterruptedException {//el patron que se usará para los links de los boards
        this.board = board;
        Html = new ArrayList<>();
        Threads = new ArrayList<>();
        Images = new ArrayList<>();
        url = "https://boards.4chan.org"+board+"catalog";
        new DataGrabber().execute().get();

        pat = Pattern.compile("([^,{}]*)");
        String[] codes = Html.get(2).split("\"");

        Html.clear();
        for (int i = 0; i<codes.length;i++){
            mat = pat.matcher(codes[i]);
            if (mat.matches()){
                Html.add(codes[i]);
            }
        }
        Threads.add(Title);
        for (int i = 0; i < Html.size(); i++){
            pat = Pattern.compile("[0-9]{6,12}");
            mat = pat.matcher(Html.get(i));
            if (mat.matches())
                Threads.add("https://boards.4chan.org" + board + "thread/" + Html.get(i));
            patIm = Pattern.compile("[0-9]{13,16}");
            mat = patIm.matcher(Html.get(i));
            if (mat.matches()) {
                Images.add("https://i.4cdn.org" + board + Html.get(i) + "s.jpg");
            }
        }
        //for (int i = 0; i < Threads.size(); i++){
        //    System.out.println(Threads.get(i));
        //}
    }


    public ArrayList<String> returnThreads(){
        return Threads;
    }
    public ArrayList<String> returnImages(){
        return Images;
    }

    /*Clase para implementar un thread para la extraccion de los links*/
    private class DataGrabber extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {//Thread
            try {
                //Se conecta a la pagina de internet y extrae los links
                doc = Jsoup.connect(url).get();
                Title = doc.title().split("-")[1];
                Elements links = doc.select("script");
                for (Element link : links) {
                    Html.add(link.data());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
