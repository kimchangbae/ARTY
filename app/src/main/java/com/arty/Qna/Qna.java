package com.arty.Qna;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Date;

public class Qna {
    String  seqId;     // 글 번호
    String  title;   // 제목
    String  qnaType; // 글 타입 (식물이 아파요 or 식물이 궁금해요)
    String  content; // 내용
    String  userId;  // 작성자 아이디
    String  img;     // 업로드 이미지
    //String userName;
    //Date timeStamp;
    //Image img;

    public Qna() {}

    @Override
    public String toString() {
        return "Qna{" +
                "seq=" + seqId +
                ", title='" + title + '\'' +
                ", qnaType='" + qnaType + '\'' +
                ", content='" + content + '\'' +
                ", userId='" + userId + '\'' +
                ", img='" + img + '\'' +
                '}';
    }

    public String getSeqId() {
        return seqId;
    }

    public void setSeqId(String seqId) {
        this.seqId = seqId;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
