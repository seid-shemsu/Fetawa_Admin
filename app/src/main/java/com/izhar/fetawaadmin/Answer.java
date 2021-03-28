package com.izhar.fetawaadmin;

import android.net.Uri;

public class Answer {
    String question, answer;

    public Answer(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    public Answer(){}
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    Uri uri;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Answer(String question, Uri uri) {
        this.question = question;
        this.uri = uri;
    }
}
