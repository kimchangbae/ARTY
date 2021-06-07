package com.arty.User;

import java.io.Serializable;

public class UserAccount implements Serializable {
    private String uId;         // uId
    private String userNm;     // 유저 아이디 (PK)
    private String email;       // 이메일
    private long kakaoId;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserNm() {
        return userNm;
    }

    public void setUserNm(String userNm) {
        this.userNm = userNm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getKakaoId() {
        return kakaoId;
    }

    public void setKakaoId(long kakaoId) {
        this.kakaoId = kakaoId;
    }
}
