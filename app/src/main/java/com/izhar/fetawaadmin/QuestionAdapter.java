package com.izhar.fetawaadmin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    List<Question> questionList;
    Context context;
    List<String> id;
    String question_date;
    public QuestionAdapter(List<Question> questionList, Context context, List<String> id, String question_date) {
        this.questionList = questionList;
        this.context = context;
        this.id = id;
        this.question_date = question_date;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question current = questionList.get(position);
        holder.question.setText(current.getQuestion());
        holder.roll.setText(Integer.toString(position + 1));
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView question, roll;
        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            roll = itemView.findViewById(R.id.roll);
            question.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, Answering.class)
                    .putExtra("username", questionList.get(getAdapterPosition()).getUsername())
                    .putExtra("question_date", question_date)
                    .putExtra("id", id.get(getAdapterPosition()))
                    .putExtra("question",questionList.get(getAdapterPosition()).getQuestion()));
                    deleteItem(getAdapterPosition());
                }
            });
        }

        private void setAnswer(String question_txt, String answer_txt, final Dialog dialog) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("answered");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = simpleDateFormat.format(new Date());
            Answer answer = new Answer(question_txt, answer_txt);
            databaseReference.child(date).child("" + System.currentTimeMillis()).setValue(answer);
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(questionList.get(getAdapterPosition()).getUsername());
            databaseReference.child("" + System.currentTimeMillis()).setValue(answer)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "answered successfully", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
            databaseReference = FirebaseDatabase.getInstance().getReference("questions").child(question_date).child(id.get(getAdapterPosition()));
            databaseReference.removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

        private void deleteItem(int adapterPosition) {
            questionList.remove(adapterPosition);
            id.remove(adapterPosition);
            notifyItemRemoved(adapterPosition);
            notifyItemRangeChanged(0, questionList.size());
        }
    }
}
