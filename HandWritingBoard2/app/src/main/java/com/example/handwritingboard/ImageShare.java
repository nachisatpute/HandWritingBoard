package com.example.handwritingboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageShare extends AppCompatActivity {
    private Bitmap bitmap;
    private ImageView imageView;
    private Button bt;
    private Button clear;
    private Button btnsend;
    private String user_name;
    private String room_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_share);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

 Intent intent=getIntent();
 room_name=intent.getStringExtra("room");
 user_name=intent.getStringExtra("name");

 setTitle(room_name+" - "+user_name);
//        TextView t = findViewById(R.id.text);
//        t.setText(user_name);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(room_name);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //myRef.setValue("Hello, World!");

        clear=findViewById(R.id.clear);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot.child("URI").exists()) {
                    String value = dataSnapshot.child("URI").getValue().toString();

                    if (value != null)
                        Glide.with(getApplicationContext()).load(value).into(imageView);
                    else {

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
              //  Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        btnsend=findViewById(R.id.btSend);

        bt=findViewById(R.id.camera);
        imageView= findViewById(R.id.imageview);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),CameraPreview.class);
                intent.putExtra("room",room_name);
                intent.putExtra("name",user_name);
                startActivityForResult(intent,2);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               reference.child("URI").setValue(null);
            }
        });
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String,Object>();
               String temp_key = reference.push().getKey();
                reference.updateChildren(map);

                DatabaseReference child_ref = reference.child(temp_key);
               // DatabaseReference child_ref = reference;
                Map<String,Object> map2 = new HashMap<>();
                map2.put("name",user_name);
                map2.put("msg", "I have sent a photo in ImageChat");
                child_ref.updateChildren(map2).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });


                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReferenceFromUrl("gs://handwritingboard-f7d5e.appspot.com");
                final StorageReference mountainsRef = storageRef.child(room_name+"/"+room_name+".jpg");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = mountainsRef.putBytes(data);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return mountainsRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

//                            TextView t=findViewById(R.id.text);
//                            t.setText(user_name);

                            Map<String,Object> map = new HashMap<>();
                            map.put("URI",downloadUri.toString());
                            reference.updateChildren(map).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getApplicationContext(),"success", Toast.LENGTH_LONG).show();
                                }
                            });
                            Map<String,Object> flag=new HashMap<>();
                            flag.put("flag",room_name);
                            ref.updateChildren(flag).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(getApplicationContext(),"success", Toast.LENGTH_LONG).show();
                                }
                            });
                            Glide.with(getApplicationContext()).load(downloadUri.toString()).into(imageView);
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

            }
        });



        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

//Toast.makeText(getApplicationContext(), (Integer) dataSnapshot.child("URI").getValue(),Toast.LENGTH_LONG).show();
               Glide.with(getApplicationContext()).load(dataSnapshot.getValue().toString()).into(imageView);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.chat));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.chat));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            byte[] byteArray = data.getByteArrayExtra("image");
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            // imageView.setImageBitmap(bitmap);
            Toast.makeText(this,data.getStringExtra("ans"),Toast.LENGTH_SHORT);

        }
    }
}
