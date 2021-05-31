package com.arty;

import android.media.Image;
import android.util.Log;

import java.util.Date;

public class QnaList {
    String title;
    String qnaType;
    String context;
    String userName;
    //Date timeStamp;
    //Image img;

    public QnaList (String title, String qnaType, String userName) {
        this.title = title;
        this.qnaType = qnaType;
        this.userName = userName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQnaType() {
        return qnaType;
    }

    public void setQnaType(String qnaType) {
        this.qnaType = qnaType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
