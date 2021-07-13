package com.arty.Comment;

public class Comment {
    private String      comtId;
    private String      parentId;
    private String      userId;         // 작성자 ID
    private int         depth;          // 댓글 0, 대댓글 1
    private String      comment;        // 댓글
    private String      createTime;    // 작성한 시간
    private String      updateTime;    // 수정된 시간
    private String      sortId;         // 정렬기준

    @Override
    public String toString() {
        return "Comment{" +
                "comtId='" + comtId + '\'' +
                ", parentId='" + parentId + '\'' +
                ", userId='" + userId + '\'' +
                ", depth=" + depth +
                ", comment='" + comment + '\'' +
                ", createTime='" + createTime + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", sortId='" + sortId + '\'' +
                '}';
    }

    public String getComtId() {
        return comtId;
    }

    public void setComtId(String comtId) {
        this.comtId = comtId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }
}
