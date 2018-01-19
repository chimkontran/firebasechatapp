package com.example.chimkontran.firebasechatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by chimkontran on 11/23/2017.
 */

public class Users extends AppCompatActivity{

    ListView usersList;
    TextView noUserText;
    ArrayList<String> arrayList = new ArrayList<>();
    int totalUsers = 0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);


        // Find view by id
        usersList = (ListView) findViewById(R.id.usersList);
        noUserText = (TextView) findViewById(R.id.noUserText);

        // Progress Dialog
        progressDialog = new ProgressDialog(Users.this);
        progressDialog.setMessage("Loading . . .");
        progressDialog.show();

        // Firebase Json
        String url = "https://fir-chatapp-fe8bf.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                doOnSuccess(response);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("" + error);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(Users.this);
        requestQueue.add(request);

        // set OnClick -> bring to Chat Activity
        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                UserDetails.chatWith = arrayList.get(position);
                startActivity(new Intent(Users.this, Chat.class));
            }
        });
    }

    public void doOnSuccess(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);

            Iterator iterator = jsonObject.keys();
            String key = "";

            while (iterator.hasNext())
            {
                key = iterator.next().toString();

                if (!key.equals(UserDetails.username))
                {
                    arrayList.add(key);
                }
                totalUsers++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Display "No User"
        if (totalUsers <= 1)
        {
            usersList.setVisibility(View.GONE);
            noUserText.setVisibility(View.VISIBLE);
        } else
        // Display user List
        {
            noUserText.setVisibility(View.GONE);
            usersList.setVisibility(View.VISIBLE);
            usersList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList));
        }
        progressDialog.dismiss();
    }
}
