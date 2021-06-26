package com.arty.Qna;

public class Comment {
    private long seqNo;
    private long followNo;
    private String userId;
    private String comment;
    private String uploadTime;

    @Override
    public String toString() {
        return "Comment{" +
                "seqNo=" + seqNo +
                ", followNo=" + followNo +
                ", userId='" + userId + '\'' +
                ", comment='" + comment + '\'' +
                ", uploadTime='" + uploadTime + '\'' +
                '}';
    }

    public long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(long seqNo) {
        this.seqNo = seqNo;
    }

    public long getFollowNo() {
        return followNo;
    }

    public void setFollowNo(long followNo) {
        this.followNo = followNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
}
