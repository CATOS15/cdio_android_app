package group15.card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Image stuff
    private Executor exeggutor = Executors.newSingleThreadExecutor();
    private int requestCode = 1001;
    private final String[] permissions = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    PreviewView mPreviewView;
    ImageView captureImage;

    // Buttons/Navigation
    Button nextMoveButton, rulesbutton;

    // Movedialog/loadingdialog
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private TextView popup_header_text, popup_instructions_text;
    private Button popup_close_button;
    ImageView popup_leftimage,popup_rightimage;

    final LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPreviewView = findViewById(R.id.previewView);
        captureImage = findViewById(R.id.captureImg);

        popup_instructions_text = findViewById(R.id.popup_instructions_text);

        nextMoveButton = findViewById(R.id.nextMoveButton);
        nextMoveButton.setOnClickListener(this);
        rulesbutton = findViewById(R.id.rulesbutton);
        rulesbutton.setOnClickListener(this);

        //Permissioncheck
        if(permissionsGranted()){
            startCamera();
        } else{
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == nextMoveButton){
            createNewMoveDialog();
        }
        if(v == rulesbutton){
            Intent i = new Intent(this, RulesActivity.class);
            startActivity(i);
        }
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

        popup_close_button.setOnClickListener(view -> dialog.dismiss());
    }

    //Functions for camera.
    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException ignored) {
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();
        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Gradle extension
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());

        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, preview, imageAnalysis, imageCapture);

        captureImage.setOnClickListener(v -> {
            //Mulig navnændring med hensyn til flere billeder pr. spil
            File file = new File(getDirectoryName(), "image.jpg");

            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
            imageCapture.takePicture(outputFileOptions, exeggutor, new ImageCapture.OnImageSavedCallback () {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            new SendImageTask(MainActivity.this).execute(file.getAbsolutePath()));

                }
                @Override
                public void onError(@NonNull ImageCaptureException error) {
                    error.printStackTrace();
                }
            });
        });
    }

    //Lægger billedfiler i en nemt tilgængelig image mappe
    public String getDirectoryName() {
        String app_folder_path;
        app_folder_path = Environment.getExternalStorageDirectory().toString() + "/images";
        File dir = new File(app_folder_path);
        //if (!dir.exists() && !dir.mkdirs()) {}
        return app_folder_path;
    }

    private boolean permissionsGranted(){
        for(String permission : permissions){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == this.requestCode){
            if(permissionsGranted()){
                startCamera();
            } else{
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
    }
}
