package com.example.inspiron.uroborosproto;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Uroboros on 28/01/2018.
 */

public class ExtractImages {
    ArrayList<String> Images;
    String url = "https://boards.4chan.org/an/thread/2588833";
    Document doc;
    String tmp = "";
    Elements links;
    public ExtractImages() throws IOException {
        Images = new ArrayList<>();
        new DataGrabber().execute();
    }

    private static String guardar(String msg, Object... args) {
        return (String.format(msg, args));
    }

    private class DataGrabber extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                doc = Jsoup.connect(url).get();
                System.out.println(doc.title());
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    Pattern pat = Pattern.compile(tmp);
                    Matcher mat = pat.matcher(guardar(link.attr("abs:href")));
                    Pattern patE = Pattern.compile(".*4cdn.*");
                    Matcher matE = patE.matcher(guardar(link.attr("abs:href")));
                    if (!mat.matches() && matE.matches()){
                        //Pattern pat1 = Pattern.compile(".*.jpg$");
                        //Matcher mat1 = pat1.matcher(guardar(link.attr("abs:href")));
                        //if (mat1.matches()){
                        Images.add(guardar(link.attr("abs:href")));
                        tmp = guardar(link.attr("abs:href"));
                        //}
                    }
                }
                DownLoader DL = new DownLoader(Images);

                for (int i = 0; i < Images.size(); i++) {
                    System.out.println(Images.get(i));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}



