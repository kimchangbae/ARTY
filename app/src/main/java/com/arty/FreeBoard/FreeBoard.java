package com.arty.FreeBoard;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class FreeBoard implements Parcelable {
    private String  uuId;           // Document Key(PK);
    private String  userId;         // 글 작성자
    private String  uploadTime;     // 작성일자
    private String  content;        // 내용
    private String  image1;         // 첨부이미지1(다운로드URL)
    private String  image2;         // 첨부이미지2(다운로드URL)
    private String  image3;         // 첨부이미지3(다운로드URL)
    private String  filePath;       // storage 이미지 저장경로

    public FreeBoard(Parcel parcel) {
        this.uuId           = parcel.readString();
        this.userId         = parcel.readString();
        this.content        = parcel.readString();
        this.uploadTime     = parcel.readString();
        this.image1         = parcel.readString();
        this.image2         = parcel.readString();
        this.image3         = parcel.readString();
        this.filePath       = parcel.readString();
    }

    public static final Creator<FreeBoard> CREATOR = new Creator<FreeBoard>() {
        @Override
        public FreeBoard createFromParcel(Parcel source) {
            return new FreeBoard(source);
        }

        @Override
        public FreeBoard[] newArray(int size) {
            return new FreeBoard[size];
        }
    };

    public FreeBoard() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuId);
        dest.writeString(this.userId);
        dest.writeString(this.content);
        dest.writeString(this.uploadTime);
        dest.writeString(this.image1);
        dest.writeString(this.image2);
        dest.writeString(this.image3);
        dest.writeString(this.filePath);
    }

    @Override
    public String toString() {
        return "FreeBoard{" +
                "uuid='" + uuId + '\'' +
                ", userId='" + userId + '\'' +
                ", uploadtime=" + uploadTime +
                ", content='" + content + '\'' +
                ", image1='" + image1 + '\'' +
                ", image2='" + image2 + '\'' +
                ", image3='" + image3 + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
