package com.arty.Comment;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class CommentViewModel extends AndroidViewModel {
    private CommentRepository   commentRepository;

    private MutableLiveData<ArrayList<Comment>>     pCommentLists;
    private MutableLiveData<ArrayList<Comment>>     cCommentLists;
    private MutableLiveData<Comment>                comment;

    public CommentViewModel(Application application) {
        super(application);

        commentRepository   = new CommentRepository(application);

        pCommentLists   = commentRepository.getpCommentLists();
        cCommentLists   = commentRepository.getcCommentLists();
        comment         = commentRepository.getComment();
    }

    public MutableLiveData<ArrayList<Comment>> getpCommentLists() {
        return pCommentLists;
    }

    public MutableLiveData<ArrayList<Comment>> getcCommentLists() {
        return cCommentLists;
    }

    public MutableLiveData<Comment> getComment() {
        return comment;
    }


    public void selectQnaItemComment(String uuId) {
        commentRepository.selectQnaItemComment(uuId);
    }

    public void selectQnaComment(String uuId) {
        commentRepository.selectParentComment(uuId);
    }

    public void selectChildComment(String uuId) {
        commentRepository.selectChildComment(uuId);
    }

    public void insertParentComment(String uuId, String userId, String comment, String createTime) {
        // commentRepository.insertParentComment(uuId, userId, comment, createTime);
        commentRepository.insertParentComment2(uuId, userId, comment, createTime);
    }

    public void insertChildComment(String uuId, String userId, String comment, String createTime, String commentParentId, String sortId) {
        // commentRepository.insertChildComment(uuId, userId, comment, createTime, commentParentId, sortId);
        commentRepository.insertChildComment2(uuId, userId, comment, createTime, commentParentId, sortId);
    }
}
