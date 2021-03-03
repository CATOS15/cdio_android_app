package group15.card;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendImageTask extends AsyncTask<String, Void, String> {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private MainActivity activity;
    final LoadingDialog loadingDialog;

    public static String responseMessage;

    Response response;

    public SendImageTask(MainActivity activity) {  // can take other params if needed
        this.activity = activity;
        this.loadingDialog = new LoadingDialog(activity);
    }

    @Override
    protected void onPreExecute() {
        loadingDialog.startLoadingDialog();
    }

    @Override
    protected String doInBackground(String... strings) {
        OkHttpClient okHttpClient = new OkHttpClient();
        File file = new File(strings[0]);
        RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", strings[0], image)
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.40:5005/upload")
                .post(requestBody)
                .build();
        response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject reader = new JSONObject(response.body().string());
            responseMessage = reader.getString("id");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return response.message();
    }

    @Override
    protected void onPostExecute(String s) {
        loadingDialog.dismissDialog();
        activity.createNewMoveDialog();
    }
}
