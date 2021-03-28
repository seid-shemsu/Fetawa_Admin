package com.izhar.fetawaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Answering extends AppCompatActivity {
    TextView question;
    EditText txt_answer;
    Button btn_answer;
    ProgressBar progressBar;
    String question_string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answering);
        question = findViewById(R.id.question);
        txt_answer = findViewById(R.id.editText_answer);
        btn_answer = findViewById(R.id.btn_answer);
        progressBar = findViewById(R.id.pbar);
        question_string = getIntent().getExtras().getString("question");
        question.setText(question_string);
        btn_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_answer.getText().toString().trim().length() > 25){
                    setAnswer(question.getText().toString(), txt_answer.getText().toString());
                    txt_answer.setEnabled(false);
                    btn_answer.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
                else {
                    Toast.makeText(Answering.this, "user more words", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setAnswer(String question_txt, String answer_txt) {
        String username = getIntent().getExtras().getString("username");
        String question_date = getIntent().getExtras().getString("question_date");
        String id = getIntent().getExtras().getString("id");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("answered");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(new Date());
        Answer answer = new Answer(question_txt, answer_txt);
        databaseReference.child(date).child("" + System.currentTimeMillis()).setValue(answer);
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(username);
        databaseReference.child("" + System.currentTimeMillis()).setValue(answer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Answering.this, "answered successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Answering.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void answer_voice(View view) {
        String username = getIntent().getExtras().getString("username");
        String question_date = getIntent().getExtras().getString("question_date");
        String id = getIntent().getExtras().getString("id");
        startActivity(new Intent(this, Vocal_Answering.class)
                .putExtra("username",username)
                .putExtra("question_date",question_date)
                .putExtra("id",id)
                .putExtra("question", question_string));
        onBackPressed();
    }
}
