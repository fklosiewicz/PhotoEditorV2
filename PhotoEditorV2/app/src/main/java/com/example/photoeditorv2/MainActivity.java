package com.example.photoeditorv2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    final String[] json_string = new String[1]; // Final array because lets you pass variable from threads to global, neat trick
    final String[] url_address = new String[11];
    final Bitmap[] bitmap = new Bitmap[11];
    public int photoIndex = 0; // Index used for cycling pictures, will be used as a way to select them
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        URL url = new URL("http://eulerity-hackathon.appspot.com/image");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        // use a string builder to bufferize the response body
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append('\n');
                        }
                        // use the string builder directly,
                        String body = sb.toString();
                        json_string[0] = body;
                        Log.d("HTTP-GET", body);
                        connection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("The string is: " + json_string[0]);

        try {

            JSONArray JSONImageArray = new JSONArray(json_string[0]);
            ArrayList<EulerityImage> images = new ArrayList<EulerityImage>();
            if (JSONImageArray.length() != 0) {
                for (int i = 0; i < JSONImageArray.length(); i++) {
                    JSONObject EulerityImageNew = JSONImageArray.getJSONObject(i);
                    EulerityImage JSONFile = new EulerityImage(EulerityImageNew.getString("url"), EulerityImageNew.getString("created"), EulerityImageNew.getString("updated"));
                    images.add(JSONFile);
                }
            }

            for(int i = 0; i <= 10; i++) {
                url_address[i] = images.get(i).address;
            }

    } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("The global address is: " + url_address[0]);

        imageView2 = (ImageView)findViewById(R.id.photo);

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        bitmap[0] = BitmapFactory.decodeStream((InputStream) new URL(url_address[0]).getContent());
                        // Literally unable to loop and create an array of bitmaps because emulator lacks memory. Next/prev button will multi-thread.
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        imageView2.setImageBitmap(bitmap[0]);
    }

    public void edit(View view) throws IOException {
        String url = url_address[photoIndex];
        Intent i = new Intent(MainActivity.this, EditActivity.class);
        i.putExtra("url", url);
        startActivity(i);
    }

    public void next(View view) throws IOException {
        photoIndex = (photoIndex + 1) % 11;

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        bitmap[0] = BitmapFactory.decodeStream((InputStream) new URL(url_address[photoIndex]).getContent());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        imageView2.setImageBitmap(bitmap[0]);
    }

    public void prev(View view) throws IOException {
        photoIndex = (photoIndex + 10) % 11;

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        bitmap[0] = BitmapFactory.decodeStream((InputStream) new URL(url_address[photoIndex]).getContent());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread2.start();
        try {
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        imageView2.setImageBitmap(bitmap[0]);
    }
}