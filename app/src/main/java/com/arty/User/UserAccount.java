package com.arty.User;

public class UserAccount {
    String uId;         // uId
    String userNm;     // 유저 아이디 (PK)
    String email;       // 이메일
    long kakaoId;

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
