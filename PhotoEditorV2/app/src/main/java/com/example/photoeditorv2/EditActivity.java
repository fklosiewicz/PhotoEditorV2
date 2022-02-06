package com.example.photoeditorv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.photoeditorv2.R;
import com.mukesh.image_processing.ImageProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class EditActivity extends AppCompatActivity {

    // creating a bitmap for our original
    // image and all image filters.
    public Bitmap bitmap;
    final Bitmap[] bitmap2 = new Bitmap[1];
    public String text_overlay = "";
    public boolean bool = false;
    final String[] upload_string = new String[1];
    public String url_address;
    public String url;

    // creating a variable for image view.
    ImageView oneIV, twoIV, threeIV, fourIV, fiveIV, sixIV, sevenIV, originalIV;
    Bitmap oneBitMap, twoBitMap, threeBitmap, fourBitMap, fiveBitMap, sixBitMap, sevenBitMap, currentBitmap, overlayedBitmap;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        // Extract the upload URL
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://eulerity-hackathon.appspot.com/upload");
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
                    upload_string[0] = body;
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

        System.out.println("The string is: " + upload_string[0]);
        try {
            JSONObject object = new JSONObject(upload_string[0]);
            url_address = object.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("The Eulerity Upload Address Is: " + url_address);

        // creating a variable for our image processor.
        ImageProcessor processor = new ImageProcessor();

        BitmapFactory.Options options = new BitmapFactory.Options();  // This is necessary because these images are so high quality, if not compressed, emulator has NO memory. Neat trick to save frames.
        options.inSampleSize = 20;

        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        bitmap2[0] = BitmapFactory.decodeStream((InputStream) new URL(url).getContent(), null, options);
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

        // initializing bitmap with our image resource.
        bitmap = bitmap2[0];

        // this keeps track of the current bitmap in use, very important for when filters are applied
        currentBitmap = bitmap;


        // initializing image views for our filters 
        // and original image on which we will 
        // be applying our filters.
        oneIV = findViewById(R.id.idIVOne);
        twoIV = findViewById(R.id.idIVTwo);
        threeIV = findViewById(R.id.idIVThree);
        fourIV = findViewById(R.id.idIVFour);
        fiveIV = findViewById(R.id.idIVFive);
        sixIV = findViewById(R.id.idIVSix);
        sevenIV = findViewById(R.id.idIVSeven);
        originalIV = findViewById(R.id.idIVOriginalImage);


        // Sets the original bitmap into imageview
        originalIV.setImageBitmap(bitmap);

        // below line is use to add tint effect to our original
        // image bitmap and storing that in one bitmap.
        oneBitMap = processor.tintImage(bitmap, 90);

        // after storing it to one bitmap 
        // we are setting it to imageview.
        oneIV.setImageBitmap(oneBitMap);

        // below line is use to apply gaussian blur effect
        // to our original image bitmap.
        twoBitMap = processor.applyGaussianBlur(bitmap);
        twoIV.setImageBitmap(twoBitMap);

        // below line is use to add sepia toing effect
        // to our original image bitmap.
        threeBitmap = processor.createSepiaToningEffect(bitmap, 1, 50, 1, 5);
        threeIV.setImageBitmap(threeBitmap);

        // below line is use to apply saturation
        // filter to our original image bitmap.
        fourBitMap = processor.applySaturationFilter(bitmap, 3);
        fourIV.setImageBitmap(fourBitMap);

        // below line is use to apply snow effect
        // to our original image bitmap.
        fiveBitMap = processor.applySnowEffect(bitmap);
        fiveIV.setImageBitmap(fiveBitMap);

        // below line is use to add gray scale
        // to our image view.
        sixBitMap = processor.doGreyScale(bitmap);
        sixIV.setImageBitmap(sixBitMap);

        // below line is use to add engrave effect
        // to our image view.
        sevenBitMap = processor.engrave(bitmap);
        sevenIV.setImageBitmap(sevenBitMap);


        // below line is use to call on click 
        // listener for our all image filters.
        initializeOnCLickListerns();
    }

    public static void saveJPGE_After(Bitmap bitmap, String path) {
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeOnCLickListerns() {
        oneIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on clicking on each filter we are 
                // setting that filter to our original image.
                originalIV.setImageBitmap(oneBitMap);
                currentBitmap = oneBitMap;
            }
        });

        twoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalIV.setImageBitmap(twoBitMap);
                currentBitmap = twoBitMap;
            }
        });

        threeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalIV.setImageBitmap(threeBitmap);
                currentBitmap = threeBitmap;
            }
        });

        fourIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalIV.setImageBitmap(fourBitMap);
                currentBitmap = fourBitMap;
            }
        });

        fiveIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalIV.setImageBitmap(fiveBitMap);
                currentBitmap = fiveBitMap;
            }
        });

        sixIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalIV.setImageBitmap(sixBitMap);
                currentBitmap = sixBitMap;
            }
        });

        sevenIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originalIV.setImageBitmap(sevenBitMap);
                currentBitmap = sevenBitMap;
            }
        });
    }

    public void saveImage(View view) {
        saveJPGE_After(overlayedBitmap, this.getApplicationInfo().dataDir + "/image_new");
        String charset = "UTF-8";

        File uploadFile1 = new File(this.getApplicationInfo().dataDir + "/image_new");
        String requestURL = url_address;
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    try {
                        MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                        multipart.addHeaderField("User-Agent", "Filip Klosiewicz");
                        multipart.addFormField("appid", "fk206@rutgers.edu");
                        multipart.addFormField("original", url);
                        multipart.addFilePart("file", uploadFile1);

                        List<String> response = multipart.finish();

                        System.out.println("SERVER REPLIED:");

                        for (String line : response) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread3.start();
        try {
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setText(View view) throws IOException, InterruptedException {
        EditText imagetext = new EditText(this);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        imagetext.setInputType(1);
        alert.setView(imagetext);


        alert.setMessage("Please enter the text you wish to overlay the image!");
        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                text_overlay = imagetext.getText().toString();
                Bitmap new_bitmap = drawTextToBitmap(getApplicationContext(), currentBitmap, text_overlay);
                originalIV.setImageBitmap(new_bitmap);
                overlayedBitmap = new_bitmap;
                bool = true;
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                bool = false;
            }
        });
        alert.show();
    }

    public Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap2, String mText) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = bitmap2;
            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(0,0, 0));
            // text size in pixels
            paint.setTextSize((int) (18 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
            // bold text (easy visibility)
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            // draw text to the Canvas center
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/4;
            int y = (bitmap.getHeight() + bounds.height())/4;

            canvas.drawText(mText, x * scale, y * scale, paint);

            return bitmap;
        } catch (Exception e) {
            /* TODO: handle exception */
            return null;
        }
    }
}