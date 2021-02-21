package group15.card;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;

import java.util.Objects;
public class MainActivity extends AppCompatActivity {

    ImageView kabale_imageview;
    Button takeImgButton, nextMoveButton;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView popup_header_text, popup_instructions_text;
    private Button popup_close_button;

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

        nextMoveButton = findViewById(R.id.nextMoveButton);
        nextMoveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                createNewMoveDialog();
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

    public void createNewMoveDialog(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View movePopupView = getLayoutInflater().inflate(R.layout.popup, null);

        popup_close_button = (Button) movePopupView.findViewById(R.id.popup_close_button);
        popup_instructions_text = (TextView) movePopupView.findViewById(R.id.popup_instructions_text);

        dialogBuilder.setView(movePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        popup_close_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if(requestCode == 100){
            Bitmap captureImage = (Bitmap) data.getExtras().get("data");
            kabale_imageview.setImageBitmap(captureImage);
        }
    }
}
