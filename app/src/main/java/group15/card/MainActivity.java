package group15.card;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class MainActivity extends AppCompatActivity {

    ImageView kabale_imageview;
    Button takeImgButton, nextMoveButton;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView popup_header_text, popup_instructions_text;
    private Button popup_close_button;

    private File photoFile;
    private String FILE_NAME = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

   //     Intent i = new Intent(this, TestPostActivity.class);
     //   startActivity(i);

        //loading
        final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);


        kabale_imageview = findViewById(R.id.kabale_imageview);
        takeImgButton = findViewById(R.id.takeImgButton);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        takeImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = getPhotoFile(FILE_NAME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // muligvis fejl her
                Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(), "group15.card.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);


                startActivityForResult(intent, 100);

                loadingDialog.startLoadingDialog();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                }, 5000);

            }
        });

        nextMoveButton = findViewById(R.id.nextMoveButton);
        nextMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewMoveDialog();
            }
        });

        /*
              if(!hasCameraPermission()) {
            requestCameraPermission();
        }
        */
    }

    private File getPhotoFile(String fileName) throws IOException {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName,"jpg",storageDirectory);
    }

    private boolean hasCameraPermission() {
        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
    }

    /*
    @Override
    public void onClick(View view) {
        if (view == takeImgButton) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,100);
        }
    }
     */

    public void createNewMoveDialog() {
        dialogBuilder = new AlertDialog.Builder(this);
        final View movePopupView = getLayoutInflater().inflate(R.layout.popup, null);

        popup_close_button = (Button) movePopupView.findViewById(R.id.popup_close_button);
        popup_instructions_text = (TextView) movePopupView.findViewById(R.id.popup_instructions_text);

        dialogBuilder.setView(movePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        popup_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
          //  Bitmap captureImage = (Bitmap) data.getExtras().get("data")


            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            kabale_imageview.setImageBitmap(takenImage);

/*
            String postUrl = "https://192.168.1.40:5005/test";
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject postData = new JSONObject();
            try {
                postData.put("name", URLEncoder.encode("Jonathan", "UTF-8"));
                postData.put("job", URLEncoder.encode("Software Engineer", "UTF-8"));

            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }



            );


            requestQueue.add(jsonObjectRequest);
*/
        }


    }





}
