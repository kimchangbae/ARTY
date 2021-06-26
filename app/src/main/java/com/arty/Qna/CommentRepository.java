package com.arty.Qna;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.arty.Common.TimeComponent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentRepository {
    private final static String TAG = "CommentRepository";
    private Application app;
    private TimeComponent   timeComponent;

    private MutableLiveData<ArrayList<Comment>>     commentLists;
    private MutableLiveData<Comment>                comment;

    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private FirebaseDatabase mDB;

    public CommentRepository(Application app) {
        this.app = app;
        timeComponent = new TimeComponent();

        mStore      = FirebaseFirestore.getInstance();
        mStorage    = FirebaseStorage.getInstance();
        mDB         = FirebaseDatabase.getInstance();
    }

    public MutableLiveData<ArrayList<Comment>> getCommentLists() {
        return commentLists;
    }

    public MutableLiveData<Comment> getComment() {
        return comment;
    }

    public void selectQnaItemComment2(String uuId) {
        ArrayList<Comment> itemList = new ArrayList<>();
        Log.d(TAG,"selectQnaItemComment");
        mDB.getReference("QNA_BOARD/" + uuId)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Comment item = dataSnapshot.getValue(Comment.class);
                    String time = timeComponent.switchTime(item.getUploadTime());
                    item.setUploadTime(time);
                    Log.d(TAG,"dataSnapshot.key : " + dataSnapshot.getKey());
                    Log.d(TAG,"dataSnapshot.value : " + item);
                    itemList.add(item);
                }
                commentLists.postValue(itemList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG,error.getMessage());
            }
        });
    }

    public void selectQnaCommentFirst(String uuId) {
        ArrayList<Comment> itemList = new ArrayList<>();
        Log.d(TAG,"selectQnaItemComment");
        mDB.getReference("QNA_BOARD/" + uuId)
        .get()
        .addOnCompleteListener(task -> {
            for(DataSnapshot snapshot : task.getResult().getChildren()) {
                Log.d(TAG,"그래.. 처음엔 이렇게 가져오쟈...");
                Log.d(TAG,snapshot.getKey() + " / " + snapshot.getValue());
                Comment item = snapshot.getValue(Comment.class);
                String time = timeComponent.switchTime(item.getUploadTime());
                item.setUploadTime(time);
                itemList.add(item);
            }
            commentLists.postValue(itemList);
        });
    }

    public void selectQnaItemComment(String uuId) {
        Log.d(TAG,"selectQnaItemComment");

        mDB.getReference("QNA_BOARD/" + uuId)
        .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Comment item = snapshot.getValue(Comment.class);
                String time = timeComponent.switchTime(item.getUploadTime());
                item.setUploadTime(time);
                comment.postValue(item);

                Log.d(TAG,"onChildAdded.key --> " + snapshot.getKey());
                Log.d(TAG,"onChildAdded.value --> " + item.toString());
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG,error.getMessage());
            }
        });
    }

    public int getLastCommentNo(String uuId) {
        mStore
        .collection("QNA_BOARD")
        .document(uuId)
        .get()
        .addOnCompleteListener(task -> {
            Log.d(TAG,"마지막 댓글 번호 --> " + task.getResult().getData().get("lastComtNo"));
        });

        return 0;
    }

    public void insertQnaComment(String uuId, long followNo, String userId, String comment, String uploadTime) {
        mStore.collection("QNA_BOARD")
        .document(uuId)
        .get()
        .addOnCompleteListener(task -> {
            long lastSeqNo = (Long) task.getResult().getData().get("lastComtNo");
            Log.d(TAG,"마지막 댓글 번호 --> " + lastSeqNo);

            Map<String, Object> map = new HashMap<>();
            map.put("lastComtNo"         , (lastSeqNo+1));

            mStore.collection("QNA_BOARD")
            .document(uuId)
            .set(map, SetOptions.merge())
            .addOnCompleteListener(command -> map.clear())
            .addOnFailureListener(e -> Log.d(TAG,e.getMessage()));

            Map<String, Object> map2 = new HashMap<>();
            map2.put("seqNo"         , lastSeqNo);
            map2.put("followNo"      , followNo > 0 ? followNo : null);
            map2.put("userId"        , userId);
            map2.put("comment"       , comment);
            map2.put("uploadTime"    , uploadTime);

            mDB.getReference("QNA_BOARD/" + uuId)
            .child(String.valueOf(lastSeqNo))
            .setValue(map2)
            .addOnCompleteListener(task1 -> {
                Log.d(TAG,lastSeqNo + " 번 댓글 등록완료");
                map2.clear();
            });
        });
    }
}
