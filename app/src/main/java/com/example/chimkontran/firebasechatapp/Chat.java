package com.example.chimkontran.firebasechatapp;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chimkontran on 11/23/2017.
 */

public class Chat extends AppCompatActivity{

    LinearLayout chatLayout1;
    RelativeLayout chatLayout2;

    ImageView sendButton;
//    Button uploadButton;

    EditText messageArea;
    ImageView imageView;

    ScrollView scrollView;
    Firebase reference1, reference2;

    private StorageReference storageReference;
    private ProgressDialog progressDialog;

    private String GOOGLE_API_KEY = "AIzaSyBvN1dMwPYBGdaJ42U8S4GNM7UVlk_LHAc";
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int PLACE_PICKER_REQUEST = 2;


    // Create Chat Menu Option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true;
    }

    // set Onclick Chat Menu Option
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.send_image:
                // CAMERA INTENT
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                return true;

            case R.id.send_location:
                // PLACE PICKER INTENT
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Find view by id
        chatLayout1 = (LinearLayout) findViewById(R.id.chatLayout1);
        chatLayout2 = (RelativeLayout) findViewById(R.id.chatLayout2);
        sendButton = (ImageView) findViewById(R.id.btnSend);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.sView);

        // Get Reference of Users (You -> chatWith) (chatWith -> you)
        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://fir-chatapp-fe8bf.firebaseio.com/messages" +
                UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://fir-chatapp-fe8bf.firebaseio.com/messages" +
                UserDetails.chatWith + "_" + UserDetails.username);

        // set OnClick to send Message
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals(""))
            {
                // put MESSAGETEXT and USERNAME to map
                Map<String, String> map = new HashMap<String, String>();
                map.put("message", messageText);
                map.put("user", UserDetails.username);
                reference1.push().setValue(map);
                reference2.push().setValue(map);
                messageArea.setText("");
            }
        }
        });

        // Get Instance of Storage
        progressDialog = new ProgressDialog(Chat.this);
        storageReference = FirebaseStorage.getInstance().getReference();


        // DISPLAY MESSAGES DEPENDS ON TYPES OF MESSAGES
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // get MESSAGETEXT and USERNAME from map
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                // Check for message as Image type
                if (message.contains("https://firebasestorage.googleapis.com/v0/b/fir-chatapp-fe8bf.appspot.com/o/Images"))
                {
                    if (userName.equals(UserDetails.username))
                    {
                        addImageBox(message,1);
                    } else
                    {
                        addImageBox(message, 2);
                    }

                } else if (message.contains("https://maps.googleapis.com/maps/api/staticmap?"))
                {
                    if (userName.equals(UserDetails.username))
                    {
                        addImageBox(message,1);
                    } else
                    {
                        addImageBox(message, 2);
                    }

                } else
                {
                    if (userName.equals(UserDetails.username))
                    {
                        addMessageBox("You:\n" + message,1);
                    } else
                    {
                        addMessageBox(UserDetails.chatWith + ":\n" + message, 2);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    // Add MESSAGE
    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        // Create new layout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;

        // Add bubble drawables
        if (type == 1)
        {
            layoutParams.gravity = Gravity.START;
            textView.setBackgroundResource(R.drawable.bubble_out);
        } else
        {
            layoutParams.gravity = Gravity.END;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }

        textView.setLayoutParams(layoutParams);
        chatLayout1.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    // Add IMAGE
    private void addImageBox(String imageUri, int type) {

        ImageView imageView = new ImageView(Chat.this);
        Uri uri = Uri.parse(imageUri);
        // Load ImageView with URI
        Picasso.with(Chat.this).load(uri).into(imageView);

        // Log
        Log.i("Method", "Added Image");

        // Create new Layout
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;

        // Add bubble drawables
        if (type == 1)
        {
            layoutParams.gravity = Gravity.START;
            imageView.setBackgroundResource(R.drawable.bubble_out);
        } else
        {
            layoutParams.gravity = Gravity.END;
            imageView.setBackgroundResource(R.drawable.bubble_in);
        }

        imageView.setLayoutParams(layoutParams);
        chatLayout1.addView(imageView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    // Get Image URI
    public Uri getImageUri(Context context, Bitmap bitmap)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        // Log
        Log.i("Method", "Got ImageURI");

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    // Get result from Camera Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // CAMERA REQUEST
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Progess
            progressDialog.setMessage("Uploading ...");
            progressDialog.show();

            // Get Image URI
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            Uri uri = getImageUri(this, bitmap);

            // Path to store image
            StorageReference filepath = storageReference.child("Images").child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                // SUCCESS UPLOAD
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(Chat.this, "Upload successful", Toast.LENGTH_LONG).show();

                    // Get Image downloadUri
                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                    // Load Image from downloadUri
                    if (!downloadUri.toString().equals(""))
                    {
                        // put MESSAGETEXT and USERNAME to map
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("message", downloadUri.toString());
                        map.put("user", UserDetails.username);
                        reference1.push().setValue(map);
                        reference2.push().setValue(map);
                        messageArea.setText("");
                    }
                }
            });


        } else if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            Place place = PlacePicker.getPlace(this, data);

            // Get Image URI
            String mapUri = "https://maps.googleapis.com/maps/api/staticmap?markers=" +
                    place.getLatLng().latitude + "," + place.getLatLng().longitude + "&zoom=" + 14 +
                    "&size=400x250&key=" + GOOGLE_API_KEY;
            Uri uri = Uri.parse(mapUri);

            // put mapUri into Message
            Map<String, String> map = new HashMap<String, String>();
            map.put("message", mapUri);
            map.put("user", UserDetails.username);
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            messageArea.setText("");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(Chat.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
        {
            Toast.makeText(Chat.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        Log.d("onMethod", "The chat is Pausing");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("onMethod", "The chat is Resuming");
        super.onResume();
    }

    @Override
    protected void onStart() {
        Log.d("onMethod", "The chat is Starting");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d("onMethod", "The chat is Stopping");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("onMethod", "The chat is Restarting");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        Log.d("onMethod", "The chat is Destroying");
        super.onDestroy();
    }
}