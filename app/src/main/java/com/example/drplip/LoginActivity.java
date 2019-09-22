package com.example.drplip;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    final private static int RC_SIGN_IN = 123;
    Dialog details;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pb = findViewById(R.id.pb);
        details = new Dialog(LoginActivity.this);
        details.setContentView(R.layout.dialog_layout);
        details.setCancelable(false);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            pb.setVisibility(View.VISIBLE);
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .build(),
                    RC_SIGN_IN);
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        Button go = details.findViewById(R.id.go);
        final EditText nm = details.findViewById(R.id.name);
        final EditText ag = details.findViewById(R.id.age);
        final Switch gen = details.findViewById(R.id.gen);
        gen.setTextOff("Male");
        gen.setTextOn("Female");
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser fu = FirebaseAuth.getInstance().getCurrentUser();
                String gender = gen.getTextOff().toString();
                if (gen.getShowText())
                    gender = gen.getTextOn().toString();

                User u = new User(nm.getText().toString(), fu.getUid(), fu.getPhoneNumber(), fu.getEmail(), gender, Integer.parseInt(ag.getText().toString()));
                addUser(u);
                details.cancel();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            pb.setVisibility(View.GONE);
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "SIGN IN SUCCESSFULL", Toast.LENGTH_SHORT).show();
                details.show();


            } else
                Toast.makeText(this, "SIGN IN FAILED", Toast.LENGTH_SHORT).show();
        }
    }

    void addUser(User u) {
        pb.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://dh1n3sh.pythonanywhere.com/users/add";
        Gson g = new Gson();
        final String mRequestBody = g.toJson(u);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pb.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
//                Log.i("LOG_VOLLEY", response);
//                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb.setVisibility(View.GONE);
                Log.e("LOG_VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {

                    responseString = String.valueOf(response.statusCode);

                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }
}
