package com.example.chimkontran.firebasechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by chimkontran on 11/23/2017.
 */

public class Register extends AppCompatActivity{

    EditText username, password;
    Button registerButton;
    String user, pass;
    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Find view by id
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        registerButton = (Button) findViewById(R.id.registerButton);
        login = (TextView) findViewById(R.id.login);

        Firebase.setAndroidContext(this);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = username.getText().toString();
                pass = password.getText().toString();

                // Check
                if (user.equals("")) // empty Username
                {
                    username.setError("Can't be blank");
                }
                else if (pass.equals("")) // empty Password
                {
                    password.setError("Can't be blank");
                }
                else if (!user.matches("[A-Za-z0-9]+")) // No pecial characters
                {
                    username.setError("Only alphabet or number allowed");
                }
                else if (user.length()<5) // Username less than 5 char
                {
                    username.setError("At least 5 characters long");
                }
                else if (pass.length()<5) // Password less than 5 char
                {
                    password.setError("At least 5 characters long");
                }
                else
                {
                    // Progress Dialog
                    final ProgressDialog progressDialog = new ProgressDialog(Register.this);
                    progressDialog.setMessage("Loading . . .");
                    progressDialog.show();

                    // Firebase Json
                    String url = "https://fir-chatapp-fe8bf.firebaseio.com/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Firebase reference = new Firebase("https://fir-chatapp-fe8bf.firebaseio.com/users");

                            if (response.equals("null"))
                            {
                                reference.child(user).child("password").setValue(pass);
                                Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();
                            } else
                            {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                    if (!jsonObject.has(user))
                                    {
                                        reference.child(user).child("password").setValue(pass);
                                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();
                                    } else
                                    {
                                        Toast.makeText(Register.this, "Username already exists", Toast.LENGTH_LONG).show();
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

                    RequestQueue requestQueue = Volley.newRequestQueue(Register.this);
                    requestQueue.add(request);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        Log.d("onMethod", "The Register is Pausing");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("onMethod", "The Register is Resuming");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("onMethod", "The Register is Starting");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("onMethod", "The Register is Stopping");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("onMethod", "The Register is Restarting");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("onMethod", "The Register is Destroying");
        super.onDestroy();
    }
}
