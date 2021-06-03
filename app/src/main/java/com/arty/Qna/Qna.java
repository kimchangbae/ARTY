package com.arty.Qna;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Date;

public class Qna implements Parcelable {
    private String  title;          // 제목
    private String  contentType;    // 글 속성(식물이 아파요 or 식물이 궁금해요)
    private String  userId;         // 글 작성자
    private Date    date;           // 작성일자
    private String  content;        // 내용
    private String  image1;         // 첨부이미지1
    private String  image2;         // 첨부이미지2
    private String  image3;         // 첨부이미지3

    public Qna() {
    }

    public Qna(Parcel parcel) {
        this.title          = parcel.readString();
        this.contentType    = parcel.readString();
        this.userId         = parcel.readString();
        this.content        = parcel.readString();
        this.image1         = parcel.readString();
    }

    public static final Parcelable.Creator<Qna> CREATOR = new Parcelable.Creator<Qna>() {
        @Override
        public Qna createFromParcel(Parcel source) {
            return new Qna(source);
        }

        @Override
        public Qna[] newArray(int size) {
            return new Qna[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.contentType);
        dest.writeString(this.userId);
        dest.writeString(this.content);
        dest.writeString(this.image1);
    }

    @Override
    public String toString() {
        return "Qna{" +
                "title='" + title + '\'' +
                ", contentType='" + contentType + '\'' +
                ", userId='" + userId + '\'' +
                ", date=" + date +
                ", content='" + content + '\'' +
                ", image1='" + image1 + '\'' +
                ", image2='" + image2 + '\'' +
                ", image3='" + image3 + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }
}
