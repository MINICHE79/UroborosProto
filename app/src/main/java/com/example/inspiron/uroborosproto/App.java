package com.example.inspiron.uroborosproto;

/*Uroboros es una aplicación que permite al usuario descargar Threads de imagenes de cualquier
 *board de la pagina "4chan.org" y almacenarlas en el dispositivo*/


import android.*;
import android.Manifest;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by Portillo,Alderete on 25/01/2018.
 */
public class App extends AppCompatActivity {
    private GridView gridView = null;
    private itemAdapter adapter = null;
    String URL = "http://4chan.org/";//el URL que se va a usar
    String title = "";
    String Board = null;
    ArrayList<String> Threads = new ArrayList<>();
    ArrayList<String> Images;
    ArrayList<item> lista;
    private Button prueba = null;
    private Spinner spinner = null;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Board name");
        }


        ExtractImages EI = null;
        try {
            EI = new ExtractImages(App.this);
            //Threads = EI.boards("an");
            Images = EI.threads("https://boards.4chan.org/an/thread/2607983");
            title = EI.returnTitle();
        } catch (Exception e) {
            e.printStackTrace();
        }

        prueba = (Button) findViewById(R.id.prueba);
        prueba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(App.this, "Descargando" + title, Toast.LENGTH_SHORT).show();
                Download dl = new Download(App.this, title);
                for (int i = 0; i<Images.size();i++){
                    dl.downloadFile(Images.get(i));
                }
            }
        });

        spinner = (Spinner)findViewById(R.id.Spinner);


        gridView = (GridView) findViewById(R.id.gridView);

        adapter = new itemAdapter(this, R.layout.griditems, fillImages());
        gridView.setAdapter(adapter);
    }

    private ArrayList<item> fillImages() {
        try {
            new GridImages().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return lista;
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


    class GridImages extends AsyncTask<String,Void,Bitmap> {


        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                int values = Images.size();
                lista = new ArrayList<>();
                for (int i = 0; i <= values; i++) {

                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(Images.get(i)).getContent());
                    lista.add((new item(bitmap, "Image" + i)));

                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;

        }
    }

}
