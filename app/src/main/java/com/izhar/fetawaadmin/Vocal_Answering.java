package com.izhar.fetawaadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
        File file = new File(Environment.getExternalStorageDirectory() + "/fetawa");
        if (!file.exists())
            file.mkdir();
        question_string = getIntent().getExtras().getString("question");
        filePath = Environment.getExternalStorageDirectory() + "/fetawa/" + id + ".mp3";
        int color = getResources().getColor(R.color.purple_200);
        int requestCode = 1999;
        AndroidAudioRecorder.with(this)
                // Required
                .setFilePath(filePath)
                .setColor(color)
                .setRequestCode(requestCode)
                // Optional
                //.setSource(AudioSource.MIC)
                .setChannel(AudioChannel.MONO)
                .setSampleRate(AudioSampleRate.HZ_8000)
                .setAutoStart(true)
                .setKeepDisplayOn(true)
                // Start recording
                .record();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1999) {
                if (resultCode == RESULT_OK) {
                    // Great! User has recorded and saved the audio file
                    String username = getIntent().getExtras().getString("username");
                    String question_date = getIntent().getExtras().getString("question_date");
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("answered").child(id);
                    databaseReference = FirebaseDatabase.getInstance().getReference("answered");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    String date = simpleDateFormat.format(new Date());
                    try {
                        InputStream stream = new FileInputStream(filePath);
                        storageReference.putStream(stream)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        try {
                                            stream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        storageReference.getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Answer answer = new Answer(question_string, uri.toString());
                                                        databaseReference.child(date).child(id).setValue(answer);
                                                        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username);
                                                        databaseReference.child(id).setValue(answer)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(Vocal_Answering.this, "answered successfully", Toast.LENGTH_SHORT).show();
                                                                        //onBackPressed();
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
                                                                        Toast.makeText(Vocal_Answering.this, "1\n" + e.toString(), Toast.LENGTH_LONG).show();
                                                                    }
                                                                });

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Vocal_Answering.this, "2\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                });
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, "5\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    /*storageReference.putFile(Uri.parse(filePath))
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
                                                            Toast.makeText(Vocal_Answering.this,"1\n" +  e.toString(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Vocal_Answering.this,"2\n" +  e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Vocal_Answering.this,"3\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });*/
                } else if (resultCode == RESULT_CANCELED) {
                    // Oops! User has canceled the recording
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "7\n" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}