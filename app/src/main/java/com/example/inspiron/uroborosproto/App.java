package com.example.inspiron.uroborosproto;

/*Uroboros es una aplicación que permite al usuario descargar Threads de imagenes de cualquier
 *board de la pagina "4chan.org" y almacenarlas en el dispositivo*/


import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by Portillo,Alderete on 25/01/2018.
 */
public class App extends AppCompatActivity{
    private GridView gridView = null;
    private itemAdapter adapter = null;
    String URL = "http://4chan.org/";//el URL que se va a usar
    String title = "";
    String BoardTitle = null;
    String Board = "/a/";
    ArrayList<String> Threads;
    ArrayList<String> Images;
    ArrayList<item> lista;
    private Button prueba = null;
    private Spinner spinner = null;
    Bitmap bitmap;
    private int ImgNum = 0;
    GridImages g = null;
    DownloadIm di = null;
    ArrayList<String> TImages;
    ExtractImages EI = null;
    ExtractThreads ET = null;
    private Document doc;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {//Metodo de inicio
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                int permissionCall = 1;
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, permissionCall);
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /*prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    EI = new ExtractImages(App.this);
                    //Threads = EI.boards("an");
                    Images = EI.threads("https://boards.4chan.org/an/thread/2607983");
                    title = EI.returnTitle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(App.this, "Descargando" + title, Toast.LENGTH_SHORT).show();
                new DownloadIm().execute();
            }
        });*/

        try {
            ET = new ExtractThreads(App.this);
            ET.boards("/a/");
            TImages = ET.returnImages();
            Threads = ET.returnThreads();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Threads.get(0));
        }

        g = new GridImages();
        di = new DownloadIm();

        spinner = (Spinner)findViewById(R.id.Spinner);
        gridView = (GridView) findViewById(R.id.gridView);
        lista = new ArrayList<>();
        fillImages();

        adapter = new itemAdapter(this, R.layout.griditems, lista);
        gridView.setAdapter(adapter);


        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                if(ImgNum <150)
                    new GridImages().execute();
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v,
                                    final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(App.this);
                View mview = getLayoutInflater().inflate(R.layout.btndownload,null);
                Button DL = (Button) mview.findViewById(R.id.download);
                TextView TV = (TextView) mview.findViewById(R.id.TV);
                TV.setText("Download: " + lista.get(position).getName());

                DL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            EI = new ExtractImages(App.this);
                            Images = EI.threads(Threads.get(position + 1));
                            title = EI.returnTitle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(App.this, "Descargando" + title, Toast.LENGTH_SHORT).show();
                        new DownloadIm().execute();
                    }
                });
                builder.setView(mview);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(flag) {
                    reLoad();
                }
                flag = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void fillImages() {
        try {
            g.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reLoad(){
        lista.clear();
        adapter.notifyDataSetChanged();
        ImgNum = 0;
        try {
            ET = new ExtractThreads(App.this);
            ET.boards(spinner.getSelectedItem().toString());
            TImages = ET.returnImages();
            Threads = ET.returnThreads();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Threads.get(0));
        }
        g = new GridImages();
        di = new DownloadIm();
        fillImages();
    }


    class GridImages extends AsyncTask<String,Void,Bitmap> {

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(App.this);
            // Set progress dialog title
            dialog.setTitle("Loading");
            // Set progress dialog message
            dialog.setMessage("Loading");
            dialog.setIndeterminate(false);
            // Show progress dialog
            dialog.show();
        }*/

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565;

                for (int i = ImgNum; i <= ImgNum+14; i++) {
                    if (i<150) {
                        try {
                            bitmap = BitmapFactory.decodeStream((InputStream) new URL(TImages.get(i)).getContent());

                            doc = Jsoup.connect(Threads.get(i+1)).get();
                            String text = doc.title().split("-")[1];
                            lista.add((new item(bitmap, text)));
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
                ImgNum = ImgNum + 15;

            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            adapter.notifyDataSetChanged();
            //dialog.dismiss();
        }
    }

    class DownloadIm extends  AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            Download dl = new Download(App.this, title);
            for (int i = 0; i<Images.size();i++){
                dl.downloadFile(Images.get(i));
            }
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//Menu del toolbar
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//Opciones de la toolbar
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
