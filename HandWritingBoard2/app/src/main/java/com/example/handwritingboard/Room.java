package com.example.handwritingboard;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Room extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);
        Button login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//                LayoutInflater factory = LayoutInflater.from(getApplicationContext());
//                final View textEntryView = factory.inflate(R.layout.text_entry, null);
////text_entry is an Layout XML file containing two text field to display in alert dialog
//                final EditText input1 = (EditText) textEntryView.findViewById(R.id.EditText1);
//                final EditText input2 = (EditText) textEntryView.findViewById(R.id.EditText2);
//                input1.setText("DefaultValue", TextView.BufferType.EDITABLE);
//                input2.setText("DefaultValue", TextView.BufferType.EDITABLE);
//                final AlertDialog.Builder alert = new AlertDialog.Builder(getApplicationContext());
//
//                alert
//                        .setTitle("Enter the Text:")
//                        .setView(textEntryView)
//                        .setPositiveButton("Save",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        Log.i("AlertDialog","TextEntry 1 Entered "+input1.getText().toString());
//                                        Log.i("AlertDialog","TextEntry 2 Entered "+input2.getText().toString());
//                                        /* User clicked OK so do some stuff */
//                                    }
//                                })
//                        .setNegativeButton("Cancel",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog,
//                                                        int whichButton) {
//                                    }
//                                });
//                alert.show();


            }
        });
    }
}
