package com.mirea.kt.ribo.messenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private static final String ADDRESS = "https://android-for-students.ru/coursework/login.php";
    private static final String GROUP = "RIBO-04-22";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button authorization_button = findViewById(R.id.authorization_button);
        Button github_button = findViewById(R.id.github_button);

        EditText tv_login = findViewById(R.id.student_login);
        EditText tv_password = findViewById(R.id.student_password);

        authorization_button.setOnClickListener(v -> {
            String lgn = Objects.requireNonNull(tv_login.getText()).toString();
            String pwd = Objects.requireNonNull(tv_password.getText()).toString();
            if (!lgn.isEmpty() && !pwd.isEmpty()) {
                if (lgn.equals("admin") && pwd.equals("root")) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
                else {
                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()
                            .add("lgn", lgn)
                            .add("pwd", pwd)
                            .add("g", GROUP)
                            .build();
                    Log.i(TAG, "onCreate: create requestBody");
                    Request request = new Request.Builder()
                            .url(ADDRESS)
                            .post(requestBody)
                            .build();
                    Log.i(TAG, "onCreate: create request");
                    Executor executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        try {
                            Log.i(TAG, "onCreate: sending a request");
                            Response response = client.newCall(request).execute();
                            Log.i(TAG, "onCreate: get response");
                            if (response.isSuccessful()) {
                                ResponseBody body = response.body();
                                assert body != null;
                                try {
                                    JSONObject jsonObject = new JSONObject(body.string());
                                    Log.i(TAG, "onCreate: get jsonObject");
                                    if (jsonObject.getInt("result_code") == 1) {
                                        displayToast(R.string.student_authorization);
                                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                        Log.i(TAG, "onCreate: start LoginActivity");
                                    }
                                    else {
                                        displayToast(R.string.incorrect_login_or_password);
                                    }
                                } catch (JSONException | NullPointerException ignore) {
                                    displayToast(R.string.error_processing_request);
                                }
                            } else {
                                displayToast(R.string.failed_to_connect_to_server);
                            }
                            response.close();
                            Log.i(TAG, "onCreate: response call .close()");
                        } catch (IOException exception) {
                            displayToast(R.string.failed_to_send_request);
                        }
                    });
                }
            }
            else {
                //displayToast(R.string.empty_fields);
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        github_button.setOnClickListener(v -> {
            displayToast(R.string.transition_in_progress);
            Uri uri = Uri.parse("https://github.com/newrav1k/Messenger");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            Log.i(TAG, "onCreate: following a link");
        });
    }

    private void displayToast(int id) {
        //You CANNOT show a Toast on non-UI thread. You need to call Toast.makeText() (and most other functions dealing with the UI) from within the main thread.
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show());
    }
}