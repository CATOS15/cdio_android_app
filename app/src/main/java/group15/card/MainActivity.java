package group15.card;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView kabale_imageview;
    Button takeImgButton, nextMoveButton, rulesbutton;
    ImageView popup_leftimage,popup_rightimage;

    LinearLayout kabale_linearlayout;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView popup_header_text, popup_instructions_text;
    private Button popup_close_button;

    Context context;

    final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

    private File photoFile;
    private String FILE_NAME = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kabale_imageview = findViewById(R.id.kabale_imageview);
        kabale_imageview.setImageResource(R.drawable.green_background);

        takeImgButton = findViewById(R.id.takeImgButton);

        popup_instructions_text = findViewById(R.id.popup_instructions_text);

        // Get camera permission
        getCameraPermission();

        takeImgButton.setOnClickListener(this);

        nextMoveButton = findViewById(R.id.nextMoveButton);
        nextMoveButton.setOnClickListener(this);
        rulesbutton = findViewById(R.id.rulesbutton);
        rulesbutton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == takeImgButton){
           openCameraApp();
        }
        if(v == nextMoveButton){
            createNewMoveDialog();
        }
        if(v == rulesbutton){
            Intent i = new Intent(this, RulesActivity.class);
            startActivity(i);
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

        popup_close_button = movePopupView.findViewById(R.id.popup_close_button);

        popup_leftimage = movePopupView.findViewById(R.id.popup_leftimage);
        popup_rightimage = movePopupView.findViewById(R.id.popup_rightimage);

        popup_instructions_text = movePopupView.findViewById(R.id.popup_instructions_text);
        popup_instructions_text.setText((String) SendImageTask.moveInformation.movemessage);

        //fiks billeder
        context = popup_leftimage.getContext();

        String firstcard_temp = SendImageTask.moveInformation.firstcard;
        String secondcard_temp = SendImageTask.moveInformation.secondcard;

        if(firstcard_temp != null && secondcard_temp != null){
            Resources res = getResources();
            int resId1 = this.getResources().getIdentifier("card_"+firstcard_temp.toLowerCase(), "drawable", context.getPackageName());
            int resId2 = this.getResources().getIdentifier("card_"+secondcard_temp.toLowerCase(), "drawable", context.getPackageName());

            popup_leftimage.setImageResource(resId1);
            popup_rightimage.setImageResource(resId2);
        } else {
            popup_leftimage.setImageResource(R.drawable.card_red_back);
            popup_rightimage.setImageResource(R.drawable.card_red_back);
            popup_instructions_text.setText(context.getString(R.string.popup_your_move_text));
        }

        //show dialog
        dialogBuilder.setView(movePopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

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
}
