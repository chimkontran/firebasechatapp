package com.example.chimkontran.firebasechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by chimkontran on 11/23/2017.
 */

public class Login extends AppCompatActivity{

    EditText username, password;
    TextView registerUser;
    Button loginButton;
    String user, pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find view by id
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerUser = (TextView) findViewById(R.id.register);

        // set OnClick bring user to Register Activity
        registerUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        // set OnClick check for user Login Authentication
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = username.getText().toString();
                pass = password.getText().toString();

                // Check empty Input
                if (user.equals(""))
                {
                    username.setError("Can't be blank");
                } else if (pass.equals(""))
                {
                    password.setError("Can't be blank");
                } else
                {
                    // Progress Dialog
                    final ProgressDialog progressDialog = new ProgressDialog(Login.this);
                    progressDialog.setMessage("Loading . . .");
                    progressDialog.show();

                    // Firebase Json
                    String url = "https://fir-chatapp-fe8bf.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("null")) {
                                Toast.makeText(Login.this, "User not found (NULL)", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if (!jsonObject.has(user)) {

                                        // Log
                                        Log.i("Username", user);
                                        Log.i("Password", pass);

                                        Toast.makeText(Login.this, "User not found", Toast.LENGTH_LONG).show();

                                    } else if (jsonObject.getJSONObject(user).getString("password").equals(pass)) {
                                        UserDetails.username = user;
                                        UserDetails.password = pass;
                                        startActivity(new Intent(Login.this, Users.class));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println("" + error);
                            progressDialog.dismiss();
                        }
                    });

                    RequestQueue requestQueue = Volley.newRequestQueue(Login.this);
                    requestQueue.add(request);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d("onMethod", "The Login is Pausing");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("onMethod", "The Login is Resuming");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("onMethod", "The Login is Starting");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("onMethod", "The Login is Stopping");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("onMethod", "The Login is Restarting");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("onMethod", "The Login is Destroying");
        super.onDestroy();
    }
}
