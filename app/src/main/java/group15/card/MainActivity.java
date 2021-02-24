package group15.card;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView kabale_imageview;
    Button takeImgButton, nextMoveButton;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView popup_header_text, popup_instructions_text;
    private Button popup_close_button;

    final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

    private File photoFile;
    private String FILE_NAME = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kabale_imageview = findViewById(R.id.kabale_imageview);
        takeImgButton = findViewById(R.id.takeImgButton);

        // Get camera permission
        getCameraPermission();

        takeImgButton.setOnClickListener(this);

        nextMoveButton = findViewById(R.id.nextMoveButton);
        nextMoveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == takeImgButton){
           openCameraApp();
        }
        if(v == nextMoveButton){
            createNewMoveDialog();
        }
    }

    private void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
    }

    private void openCameraApp(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            photoFile = getPhotoFile(FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Uri fileProvider = FileProvider.getUriForFile(this, "group15.card.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        startActivityForResult(intent, 100);
    }

    private File getPhotoFile(String fileName) throws IOException {
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(fileName,"jpg",storageDirectory);
    }

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
            Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            kabale_imageview.setImageBitmap(takenImage);

            new SendImageTask(MainActivity.this).execute(photoFile.getAbsolutePath());
        }
    }

    public void setMessage(String message){
        popup_instructions_text.setText(message);
    }
}
