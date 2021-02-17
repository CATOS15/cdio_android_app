package group15.card;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Objects;
public class MainActivity extends AppCompatActivity{

    ImageView kabale_imageview;
    Button takeImgButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //loading
        final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);


        kabale_imageview = findViewById(R.id.kabale_imageview);
        takeImgButton = findViewById(R.id.takeImgButton);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        takeImgButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);

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

/*
              if(!hasCameraPermission()) {
            requestCameraPermission();
        }
*/
    }

    private boolean hasCameraPermission(){
        return ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA},1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode == 100){
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            kabale_imageview.setImageBitmap(captureImage);
        }
    }
}
