package com.arty.User;

import java.io.Serializable;

public class UserAccount {
    private String uuId;         // uId
    private String userId;     // 유저 아이디 (PK)
    private String email;       // 이메일
    private long kakaoId;

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
