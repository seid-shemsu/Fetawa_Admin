package com.izhar.fetawaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder;
import cafe.adriel.androidaudiorecorder.model.AudioChannel;
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate;

public class Vocal_Answering extends AppCompatActivity {
    String id;
    String filePath, question_string;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocal__answering);
        id = getIntent().getExtras().getString("id");
        question_string = getIntent().getExtras().getString("question");
        filePath = Environment.getExternalStorageDirectory() + "/fetawa/"+ id + ".mp3";
        int color = getResources().getColor(R.color.purple_200);
        int requestCode = 0;
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(filePath)
                .setColor(color)
                .setRequestCode(requestCode)
                // Optional
                //.setSource(AudioSource.MIC)
                .setChannel(AudioChannel.MONO)
                .setSampleRate(AudioSampleRate.HZ_16000)
                .setAutoStart(true)
                .setKeepDisplayOn(true)
                // Start recording
                .record();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                // Great! User has recorded and saved the audio file
                String username = getIntent().getExtras().getString("username");
                String question_date = getIntent().getExtras().getString("question_date");
                StorageReference storageReference = FirebaseStorage.getInstance().getReference("answered").child(id);
                databaseReference = FirebaseDatabase.getInstance().getReference("answered");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                String date = simpleDateFormat.format(new Date());
                storageReference.putFile(Uri.parse(filePath))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Answer answer = new Answer(question_string, uri);
                                        databaseReference.child(date).child("" + System.currentTimeMillis()).setValue(answer);
                                        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username);
                                        databaseReference.child("" + System.currentTimeMillis()).setValue(answer)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Vocal_Answering.this, "answered successfully", Toast.LENGTH_SHORT).show();
                                                        onBackPressed();
                                                    }
                                                });
                                        databaseReference = FirebaseDatabase.getInstance().getReference("questions").child(question_date).child(id);
                                        databaseReference.removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Vocal_Answering.this, e.toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Vocal_Answering.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Vocal_Answering.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else if (resultCode == RESULT_CANCELED) {
                // Oops! User has canceled the recording
            }
        }
    }
}