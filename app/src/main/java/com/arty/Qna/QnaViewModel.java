package com.arty.Qna;

import android.app.Application;
import android.net.Uri;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class QnaViewModel extends AndroidViewModel {
    private QnaRepository       qnaRepository;
    private CommentRepository   commentRepository;

    private MutableLiveData<ArrayList<Qna>>     qnaLists;
    private MutableLiveData<Qna>                qnaItem;
    private MutableLiveData<String>             userId;
    private MutableLiveData<Boolean>            imgUpResult, update;

    private MutableLiveData<ArrayList<Comment>>     commentLists;
    private MutableLiveData<Comment>                comment;


    public QnaViewModel(Application application) {
        super(application);

        qnaRepository       = new QnaRepository(application);
        commentRepository   = new CommentRepository(application);

        qnaLists        = qnaRepository.getQnaLists();
        qnaItem         = qnaRepository.getQnaItem();
        imgUpResult     = qnaRepository.getImgUpResult();
        update          = qnaRepository.getUpdate();
        userId          = qnaRepository.getUserId();
        commentLists    = commentRepository.getCommentLists();
        comment         = commentRepository.getComment();
    }

    public MutableLiveData<ArrayList<Qna>> getQnaLists() {
        return qnaLists;
    }

    public MutableLiveData<Qna> getQnaItem() {
        return qnaItem;
    }

    public MutableLiveData<String> getUserId() {
        return userId;
    }

    public MutableLiveData<Boolean> getImgUpResult() {
        return imgUpResult;
    }

    public MutableLiveData<Boolean> getUpdate() {
        return update;
    }

    public MutableLiveData<ArrayList<Comment>> getCommentLists() {
        return commentLists;
    }

    public MutableLiveData<Comment> getComment() {
        return comment;
    }

    public void selectQnaList() {
        qnaRepository.selectQnaList();
    }

    public void selectQnaItem(String uuId) {
        qnaRepository.selectQnaItem(uuId);
    }

    public void insertQuestion(String uuId, String type, String content, String userId, String uploadTime, String filePath) {
        qnaRepository.insertQuestion(uuId, type, content, userId, uploadTime, filePath);
    }

    public void updateQuestion(String uuId, String content, String uploadTime) {
        qnaRepository.updateQuestion(uuId, content, uploadTime);
    }

    public void deleteQuestion(String uuId) {
        qnaRepository.deleteQuestion(uuId);
    }

    public void insertImage(String uuId, String filePath, Uri uri, int index) {
        qnaRepository.insertImage(uuId, filePath, uri, index);
    }

    public void deleteImage(String filePath) {
        qnaRepository.deleteImage(filePath);
    }

    public void deleteImage(String uuId, String filePath, int index) {
        qnaRepository.deleteImage(uuId, filePath, index);
    }

    public void getUserId(String email) {
        qnaRepository.getUserId(email);
    }

    public void getUserId(long kakaoId) {
        qnaRepository.getUserId(kakaoId);
    }

    public void selectQnaCommentFirst(String uuId) {
        commentRepository.selectQnaCommentFirst(uuId);
    }

    public void selectQnaItemComment(String uuId) {
        commentRepository.selectQnaItemComment(uuId);
    }

    public int getCommentSeqNo(String uuId) {
        return commentRepository.getLastCommentNo(uuId);
    }

    public void insertQnaComment(String uuId, long followNo, String userId, String comment, String uploadTime) {
        commentRepository.insertQnaComment(uuId,followNo,userId, comment,uploadTime);
    }
}
