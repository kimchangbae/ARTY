package com.arty.Comment;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.arty.Common.TimeComponent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommentRepository {
    private final static String TAG = "CommentRepository";
    private Application app;
    private TimeComponent   timeComponent;

    private MutableLiveData<ArrayList<Comment>>     pCommentLists;
    private MutableLiveData<ArrayList<Comment>>     cCommentLists;
    private MutableLiveData<Comment>                comment;

    private FirebaseFirestore   mStore;
    private FirebaseDatabase    mDB;

    public CommentRepository(Application app) {
        this.app = app;
        timeComponent = new TimeComponent();

        mStore      = FirebaseFirestore.getInstance();
        mDB         = FirebaseDatabase.getInstance();

        comment             = new MutableLiveData<>();
        pCommentLists       = new MutableLiveData<>();
        cCommentLists       = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Comment>> getcCommentLists() {
        return cCommentLists;
    }

    public MutableLiveData<ArrayList<Comment>> getpCommentLists() {
        return pCommentLists;
    }

    public MutableLiveData<Comment> getComment() {
        return comment;
    }

    public void selectParentComment(String uuId) {
        ArrayList<Comment> itemList = new ArrayList<>();
        try{
            mStore.collection("COMMENT")
            .whereEqualTo("uuId",uuId)
            .orderBy("createTime", Query.Direction.DESCENDING)
            .addSnapshotListener((snapshot, error) -> {
                if(error != null) {
                    Log.e(TAG,error.getMessage());
                    return;
                }

                for(DocumentChange dc : snapshot.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "부모 댓글: " + dc.getDocument().getData());

                        Comment item = dc.getDocument().toObject(Comment.class);
                        String time = timeComponent.switchTime(item.getCreateTime());
                        item.setCreateTime(time);
                        itemList.add(item);
                    }
                }
                pCommentLists.postValue(itemList);
            });
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            itemList.clear();
        }
    }

    public void selectChildComment(String uuId) {
        ArrayList<Comment> itemList = new ArrayList<>();
        try {
            mStore.collection("COMMENT")
            .document(uuId)
            .collection("CHILD")
            .orderBy("createTime", Query.Direction.DESCENDING)
            .addSnapshotListener((snapshot, error) -> {
                if(error != null) {
                    Log.e(TAG,error.getMessage());
                    return;
                }

                for(DocumentChange dc : snapshot.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "자식 댓글: " + dc.getDocument().getData());

                        Comment item = dc.getDocument().toObject(Comment.class);
                        String time = timeComponent.switchTime(item.getCreateTime());
                        item.setCreateTime(time);
                        itemList.add(item);
                    }
                }
                cCommentLists.postValue(itemList);
            });
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            itemList.clear();
        }
    }

    public void selectQnaItemComment(String uuId) {
        ArrayList<Comment> itemList = new ArrayList<>();

        mDB.getReference("QNA_BOARD_COMMENT/" + uuId)
        .orderByChild("sortId")
        .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Comment item = snapshot.getValue(Comment.class);
                String time = timeComponent.switchTime(item.getCreateTime());
                item.setCreateTime(time);
                itemList.add(item);
                pCommentLists.postValue(itemList);
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG,"selectQnaItemComment-->onChildChanged");
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Log.d(TAG,"selectQnaItemComment-->onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                Log.d(TAG,"selectQnaItemComment-->onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG,error.getMessage());
            }
        });

        itemList.clear();
    }

    public void insertParentComment2(String uuId, String userId, String comment, String createTime) {
        String comtId    = UUID.randomUUID().toString().substring(0,10);

        Map<String, Object> map = new HashMap<>();
        map.put("uuId"              , uuId);
        map.put("comtId"           , comtId);
        map.put("userId"           , userId);
        map.put("comment"          , comment);
        map.put("createTime"       , createTime);

        mStore.collection("COMMENT")
        .document(comtId)
        .set(map, SetOptions.merge())
        .addOnCompleteListener(task1 -> {
            Log.d(TAG,comtId + " 댓글 등록완료");
            map.clear();
        });
    }

    public void insertChildComment2(String uuId, String userId, String comment, String createTime, String parentId, String sortId) {
        String comtId    = UUID.randomUUID().toString().substring(0,10);

        Map<String, Object> map = new HashMap<>();
        map.put("uuId"          , uuId);
        map.put("comtId"        , comtId);
        map.put("parentId"      , parentId);
        map.put("userId"        , userId);
        map.put("comment"       , comment);
        map.put("createTime"    , createTime);

        mStore.collection("COMMENT")
        .document(parentId)
        .collection("CHILD")
        .document(comtId)
        .set(map, SetOptions.merge())
        .addOnCompleteListener(task1 -> {
            Log.d(TAG,comtId + " 댓글 등록완료");
            map.clear();
        });
    }

}
