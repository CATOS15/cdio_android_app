package group15.card;
        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Context;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;

        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.net.URI;
        import java.net.URISyntaxException;
        import java.net.URL;

        import okhttp3.FormBody;
        import okhttp3.MediaType;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;

public class  TestPostActivity extends AppCompatActivity {

    private TextView get_response_text, post_response_text;
    Button get_request_button, post_request_button;

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        get_request_button = findViewById(R.id.get_data);
        post_request_button = findViewById(R.id.post_data);

        get_response_text = findViewById(R.id.get_respone_data);
        post_response_text = findViewById(R.id.post_respone_data);
        System.out.println("HALLO");

        post_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            postImage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });


    }

    public void postRequest() throws Exception {
        RequestBody formBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        Request request = new Request.Builder()
                .url("http://192.168.1.40:5005/test")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }
    }

    public void postImage() throws IOException, URISyntaxException {


        File file = new File(getFilesDir(), "README.md");

        BufferedWriter bf = new BufferedWriter(new FileWriter(file));
        bf.write("HEJSA");
        bf.close();

        Request request = new Request.Builder()
                .url("http://192.168.1.40:5005/testImage")
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(response.body().string());
        }
    }
}
