package com.example.handwritingboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CameraPreview extends AppCompatActivity {
    private Button btnSend;
    private Button btnDetectObject;
    private Button btnconfirm;
    private StorageReference mStorageRef;
    private CameraView cameraView;
    private ImageView imageView;
    private ImageView im;
    private Bitmap bitmap;
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private RelativeLayout rl;
    private RelativeLayout mainLayout;
    private int xDelta;
    private int yDelta;
    private ImageView move;
    private Bitmap finalimg;
    private String user_name;
    private String room_name;
    int view=R.layout.activity_camera_preview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(view);
        Intent intent=getIntent();
        room_name=intent.getStringExtra("room");
        user_name=intent.getStringExtra("name");
        setTitle(room_name+" - "+user_name);
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(room_name);
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        move=findViewById(R.id.im2);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot.child("URI").exists()) {
                    String value = dataSnapshot.child("URI").getValue().toString();
                    //TextView t = findViewById(R.id.text);
                   // t.setText(value);
                    if (value != null)
                        Glide.with(getApplicationContext()).load(value).into(move);
                    else {

                    }
                }
                // Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //  Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnSend=findViewById(R.id.btSend);
        btnDetectObject=findViewById(R.id.btCapture);
        cameraView = findViewById(R.id.cameraView);

        imageView=findViewById(R.id.imageview);
        im=findViewById(R.id.view);
        rl = findViewById(R.id.work);
        btnconfirm=findViewById(R.id.btConfirm);


        mainLayout = findViewById(R.id.work);


        move.setOnTouchListener(onTouchListener());
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                finalimg.compress(Bitmap.CompressFormat.JPEG, 50, bStream);
                byte[] byteArray = bStream.toByteArray();
//
//
//
//                final StorageReference childRef =  mStorageRef.child("abcd.jpg");
//                UploadTask uploadTask = childRef.putBytes(byteArray);
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Handle unsuccessful uploads
//                    }
//                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//
//                    }
//                });

                Intent intent=getIntent();
//                if(finalimg!=null)
                intent.putExtra("image", byteArray);


                intent.putExtra("ans","ans");
                setResult(2,intent);
                finish();

            }
        });
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }



            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                bitmap1 = cameraKitImage.getBitmap();
                imageView.setImageBitmap(bitmap1);


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }

        });

//        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cameraView.toggleFacing();
//            }
//
//        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });


        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenShot();
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    public void screenShot() {
        finalimg = Bitmap.createBitmap(rl.getWidth() , rl.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(finalimg);
        rl.draw(c);
        imageView.setImageBitmap(null);
        ImageView x=findViewById(R.id.im2);
        x.setImageBitmap(null);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        im.setImageBitmap(finalimg);
    }

    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:

                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }

                mainLayout.invalidate();
                return true;
            }
        };
    }
}
