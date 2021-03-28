package com.izhar.fetawaadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    QuestionAdapter adapter;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    List<Question> questionList;
    String today;
    TextView none;
    List<String> id = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        none = findViewById(R.id.none);
        progressBar = findViewById(R.id.pbar);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        today = simpleDateFormat.format(new Date());
        getData(today);
    }

    private void getData(final String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("questions").child(date);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questionList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Question request = snapshot.getValue(Question.class);
                    questionList.add(request);
                    id.add(snapshot.getKey());
                }
                adapter = new QuestionAdapter(questionList, MainActivity.this, id, date);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                if (questionList.size() == 0 )
                    none.setVisibility(View.VISIBLE);
                else
                    none.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.date){
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.date_picker);
            final DatePicker datePicker = dialog.findViewById(R.id.date_picker);
            Button ok = dialog.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String day = datePicker.getDayOfMonth() + "";
                    String month = datePicker.getMonth()+1 + "";
                    if (day.length() == 1 )
                        day = "0" + day;
                    if (month.length() == 1 )
                        month = "0" + month;
                    today = day + "-" + month + "-" + datePicker.getYear();
                    getData(today);
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        return true;
    }
}
